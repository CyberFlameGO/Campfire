package xyz.nkomarn.Campfire.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.nkomarn.Campfire.util.Border;

/**
 * Listener class that sends the fake world border to
 * players after world change, teleport, and respawn.
 */
public class BorderListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Border.sendBorder(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Border.sendBorder(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Border.sendBorder(event.getPlayer());
    }
}
