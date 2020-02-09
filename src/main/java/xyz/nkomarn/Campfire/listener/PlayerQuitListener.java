package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.Ranks;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int players = Bukkit.getOnlinePlayers().size() - Ranks.getVanishedPlayers();

        // Update player count in player list
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setPlayerListHeader(ChatColor.translateAlternateColorCodes('&',
                    Config.getString("tablist.header").replace("[online]", String.valueOf(players)))
            );
        }
    }
}
