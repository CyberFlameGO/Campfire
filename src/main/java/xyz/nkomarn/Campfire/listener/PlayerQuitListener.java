package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.util.VanishUtil;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PvPListener.ENABLED_PLAYERS.remove(event.getPlayer().getUniqueId());

        if (player.hasPermission("firstarter.vanish")) {
            event.setQuitMessage("");
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                if (onlinePlayer.hasPermission("firestarter.vanish")) {
                    onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "&c&lVanish: &7%s left silently.", player.getName()
                    )));
                }
            });
        }

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.setPlayerListHeader(ChatColor
                    .translateAlternateColorCodes('&', String.format(Config.getString("tablist.header"),
                            (Bukkit.getOnlinePlayers().size() - 1) - VanishUtil.getVanishedPlayers().size()
            ))));
        });
    }
}
