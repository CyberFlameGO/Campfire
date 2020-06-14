package xyz.nkomarn.Campfire.listener.player;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.nkomarn.Campfire.util.Config;

public class RespawnListener implements Listener {
    @EventHandler()
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().sendTitle(
                ChatColor.translateAlternateColorCodes('&', Config.getString("messages.death.top")),
                ChatColor.translateAlternateColorCodes('&', Config.getString("messages.death.bottom")),
                10, 140, 20
        );
    }
}
