package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.Ranks;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final int players = (Bukkit.getOnlinePlayers().size() - 1) - Ranks.getVanishedPlayers();
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.setPlayerListHeader(ChatColor
                    .translateAlternateColorCodes('&', Config.getString("tablist.header")
                            .replace("[online]", String.valueOf(players)))
            ));
        });

        PvPListener.ENABLED_PLAYERS.remove(event.getPlayer().getUniqueId());
    }
}
