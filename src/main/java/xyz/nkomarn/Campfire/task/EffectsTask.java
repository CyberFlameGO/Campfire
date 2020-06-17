package xyz.nkomarn.Campfire.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.data.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EffectsTask implements Runnable {

    private static final String EFFECT_STATEMENT = "SELECT slot1, slot2, slot3 FROM potions WHERE uuid = ?;";
    private static final Map<String, Integer> EFFECT_LEVEL = new HashMap<>();
    public static final Map<UUID, String[]> EFFECT_CACHE = new HashMap<>();

    @Override
    public void run() {
        for (Map.Entry<UUID, String[]> entry: EFFECT_CACHE.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                EFFECT_CACHE.remove(entry.getKey());
                continue;
            }

            for (String effect : entry.getValue()) {
                if (effect == null) continue;

                PotionEffectType effectType = PotionEffectType.getByName(effect);
                if(effectType == null) continue;

                int level = getEffectLevel(effect);
                Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                    player.addPotionEffect(new PotionEffect(effectType, 450, level, true, false));
                });
            }
        }
    }

    public static int getEffectLevel(String effect) {
        if (!EFFECT_LEVEL.containsKey(effect)) {
            EFFECT_LEVEL.put(effect, Config.getInteger(String.format("perks.potions.%s.level", effect), 1) -1);
        }
        return EFFECT_LEVEL.get(effect);
    }

    public static void loadPlayerEffects(UUID uniqueId) {
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try (Connection connection = PlayerData.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(EFFECT_STATEMENT)) {
                    statement.setString(1, uniqueId.toString());
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                           return;
                        }

                        EFFECT_CACHE.put(uniqueId, new String[] {
                                result.getString(1),
                                result.getString(2),
                                result.getString(3),
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
