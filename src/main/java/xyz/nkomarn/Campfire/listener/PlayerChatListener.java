package xyz.nkomarn.Campfire.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.nkomarn.Campfire.Campfire;

import java.util.regex.Pattern;

public class PlayerChatListener implements Listener {
    final Pattern PATTERN = Pattern.compile("([n5]+(\\W|\\d|_)*[i1!l¡]+(\\W|\\d|_)*[g96q]+(\\W|\\d|_)*[e3]+(\\W|\\d|_)*[r®]+)");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        /*if (PATTERN.matcher(event.getMessage().replace(" ", "")).find()) {
            Campfire.getCampfire().getLogger().info(String.format("%s said the n-word.", event.getPlayer().getName()));
            event.setCancelled(true);
        }*/
    }
}
