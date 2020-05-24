package xyz.nkomarn.Campfire.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.nkomarn.Campfire.util.ChatFilters;

public class PlayerChatListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (ChatFilters.stringMatchesFilters(event.getMessage())) {
            ChatFilters.notifyStaff(event.getPlayer().getName(), event.getMessage());
        }
    }
}
