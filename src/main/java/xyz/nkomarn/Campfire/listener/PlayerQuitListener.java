package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.PlayerList;
import xyz.nkomarn.Kerosene.data.PlayerData;
import xyz.nkomarn.Kerosene.util.VanishUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PvPListener.ENABLED_PLAYERS.remove(event.getPlayer().getUniqueId());
        PlayerList.updateHeader();

        /*if (player.hasPermission("firstarter.vanish")) {
            event.setQuitMessage("");
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                if (onlinePlayer.hasPermission("firestarter.vanish")) {
                    onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "&c&lVanish: &7%s left silently.", player.getName()
                    )));
                }
            });
        }*/

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try (Connection connection = PlayerData.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `shop_log` WHERE `uuid` = ?")) {
                    statement.setString(1, player.getUniqueId().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
