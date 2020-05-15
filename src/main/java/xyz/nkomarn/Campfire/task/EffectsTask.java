package xyz.nkomarn.Campfire.task;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.data.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class EffectsTask implements Runnable {
    @Override
    public void run() {
        try (Connection connection = PlayerData.getConnection()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                try (PreparedStatement statement = connection.prepareStatement("SELECT slot1, slot2, slot3 " +
                        "FROM potions WHERE uuid = ?;")) {
                    statement.setString(1, player.getUniqueId().toString());
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            Arrays.asList(result.getString(1), result.getString(2), result.getString(3)).forEach(slot -> {
                                if (slot.trim().length() > 1) {
                                    PotionEffectType effectType = PotionEffectType.getByName(slot);
                                    if (effectType != null)
                                        Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> player.addPotionEffect(
                                                new PotionEffect(effectType, 450, Config.getInteger(String
                                                        .format("perks.potions.%s.level", slot)) - 1, true, false), true));
                                }
                            });
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
