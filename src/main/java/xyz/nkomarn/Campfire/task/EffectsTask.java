package xyz.nkomarn.Campfire.task;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.data.LocalStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class EffectsTask implements Runnable {
    @Override
    public void run() {
        try (Connection connection = LocalStorage.getConnection()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                try {
                    try (PreparedStatement statement = connection.prepareStatement("SELECT slot1, slot2, slot3 " +
                            "FROM potions WHERE uuid = ?;")) {
                        statement.setString(1, player.getUniqueId().toString());
                        try (ResultSet result = statement.executeQuery()) {
                            while (result.next()) {
                                Arrays.asList(result.getString(1), result.getString(2), result.getString(3)).forEach(effect -> {
                                    PotionEffectType effectType = PotionEffectType.getByName(effect);
                                    if (effectType != null) {
                                        player.addPotionEffect(new PotionEffect(effectType, 300, Config.getInteger(String
                                                .format("perks.potions.%s.level", effect)) - 1, true, false), true);
                                    }
                                });
                            }
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
