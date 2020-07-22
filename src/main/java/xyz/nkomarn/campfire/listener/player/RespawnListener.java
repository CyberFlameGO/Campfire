package xyz.nkomarn.campfire.listener.player;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.nkomarn.campfire.util.Config;

public class RespawnListener implements Listener {

    private static final String TITLE = ChatColor.translateAlternateColorCodes('&', Config.getString("messages.death.top"));
    private static final String SUBTITLE = ChatColor.translateAlternateColorCodes('&', Config.getString("messages.death.bottom"));

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().sendTitle(TITLE, SUBTITLE,10, 140,20);
    }
}
