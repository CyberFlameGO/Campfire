package xyz.nkomarn.Campfire.listener.tree;

import com.songoda.ultimatetimber.events.TreeFallEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class TreeFallListener implements Listener {
    @EventHandler
    public void onTreeFall(TreeFallEvent event) {
        if (Advancements.isComplete(event.getPlayer(), "tree")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:tree",
                event.getPlayer().getName()));
    }
}
