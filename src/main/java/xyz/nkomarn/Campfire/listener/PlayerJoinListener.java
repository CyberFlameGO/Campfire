package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Border;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.PlayerList;
import xyz.nkomarn.Campfire.util.Webhooks;
import xyz.nkomarn.Kerosene.data.PlayerData;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerList.updateTeams(player);
        Border.sendBorder(player);

        /*if (player.hasPermission("firstarter.vanish")) {
            event.setJoinMessage("");
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                if (onlinePlayer.hasPermission("firestarter.vanish")) {
                    onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "&c&lVanish: &7%s joined silently.", player.getName()
                    )));
                }
            });
        } else {
            if (VanishUtil.isVanished(player)) {
                VanishUtil.showPlayer(player);
            }

            VanishUtil.getOnlineVanishedPlayers().forEach(uuid ->
                    player.hidePlayer(Kerosene.getKerosene(), Bukkit.getPlayer(uuid)));
        }*/

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',
                    Config.getString("messages.join.top")), ChatColor.translateAlternateColorCodes('&',
                    Config.getString("messages.join.bottom")), 10, 70, 20
            );
            player.setPlayerListFooter(ChatColor.translateAlternateColorCodes('&',
                    Config.getString("tablist.footer")));

            PlayerList.updateHeader();

            if (player.hasPlayedBefore()) {
                try (Connection connection = PlayerData.getConnection()) {
                    try (PreparedStatement statement = connection.prepareStatement("SELECT SUM(`price`) FROM " +
                            "`shop_log` WHERE `uuid` = ?;")) {
                        statement.setString(1, player.getUniqueId().toString());
                        try (ResultSet result = statement.executeQuery()) {
                            if (result.next()) {
                                if (result.getDouble(1) != 0) {
                                    String word = result.getDouble(1) > 0 ? "earned" : "lost";
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                                            "&6&lShops: &7You %s &6$%s&7 while you were away- &6/shoplog&7 for details.",
                                            word, NumberFormat.getNumberInstance(Locale.US).format(result.getDouble(1))
                                    )));
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                final Webhooks hook = new Webhooks(Config.getString("webhooks.notifications"));
                hook.addEmbed(new Webhooks.EmbedObject()
                        .setDescription(":checkered_flag: " + event.getPlayer().getName() + " joined!")
                        .setColor(Color.WHITE));
                try {
                    hook.execute();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
