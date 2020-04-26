package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.Ranks;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int players = (Bukkit.getOnlinePlayers().size() - 1) - Ranks.getVanishedPlayers();
        PvPListener.ENABLED_PLAYERS.remove(event.getPlayer().getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                // Update player count in player list
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.setPlayerListHeader(ChatColor.translateAlternateColorCodes('&',
                            Config.getString("tablist.header").replace("[online]", String.valueOf(players)))
                    );
                }
            }
        }.runTaskAsynchronously(Campfire.getCampfire());
    }
}
