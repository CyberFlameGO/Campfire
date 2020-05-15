package xyz.nkomarn.Campfire.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.nkomarn.Campfire.util.Config;

public class PlayerRespawnListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&',
                Config.getString("messages.death.top")), ChatColor.translateAlternateColorCodes('&',
                Config.getString("messages.death.bottom"))
        );
    }
}
