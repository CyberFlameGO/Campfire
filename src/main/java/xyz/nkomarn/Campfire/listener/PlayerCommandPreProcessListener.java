package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.nkomarn.Campfire.util.Advancements;

public class PlayerCommandPreProcessListener implements Listener {
    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String[] message = event.getMessage().split("\\s+");

        if (message.length < 2 && message[0].equalsIgnoreCase("/ah")) {
            if (Advancements.isComplete(event.getPlayer(), "ah-menu")) return;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:ah-menu",
                    event.getPlayer().getName()));
        } else if (message.length < 2 && message[0].equalsIgnoreCase("/hdb") ||
                message.length < 2 && message[0].equalsIgnoreCase("/heads")) {
            if (Advancements.isComplete(event.getPlayer(), "heads")) return;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:heads",
                    event.getPlayer().getName()));
        }
    }
}
