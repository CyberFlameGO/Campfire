package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.Teams;
import xyz.nkomarn.Campfire.util.Webhooks;
import xyz.nkomarn.Kerosene.Kerosene;
import xyz.nkomarn.Kerosene.util.VanishUtil;

import java.awt.*;
import java.io.IOException;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Teams.updateTeams(player);

        if (player.hasPermission("firstarter.vanish")) {
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

            VanishUtil.getVanishedPlayers().forEach(uuid -> {
                OfflinePlayer vanishedPlayer = Bukkit.getOfflinePlayer(uuid);
                if (vanishedPlayer.isOnline()) player.hidePlayer(Kerosene.getKerosene(), (Player) vanishedPlayer);
            });
        }

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',
                    Config.getString("messages.join.top")), ChatColor.translateAlternateColorCodes('&',
                    Config.getString("messages.join.bottom")), 10, 70, 20
            );
            player.setPlayerListFooter(ChatColor.translateAlternateColorCodes('&',
                    Config.getString("tablist.footer")));

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.setPlayerListHeader(ChatColor
                    .translateAlternateColorCodes('&', String.format(Config.getString("tablist.header"),
                            Bukkit.getOnlinePlayers().size() - VanishUtil.getVanishedPlayers().size()
            ))));

            if (!player.hasPlayedBefore()) {
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
