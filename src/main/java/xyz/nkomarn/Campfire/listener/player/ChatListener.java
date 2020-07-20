package xyz.nkomarn.Campfire.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.nkomarn.Campfire.util.ChatFilters;

import java.util.Set;

public class ChatListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (ChatFilters.stringMatchesFilters(event.getMessage())) {
            Set<Player> recipients = event.getRecipients();
            recipients.clear();
            recipients.add(event.getPlayer());

            ChatFilters.notifyStaff(event.getPlayer().getName(), event.getMessage());
        }
    }
}
