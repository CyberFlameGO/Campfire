package xyz.nkomarn.Campfire.listener.tree;

import com.songoda.ultimatetimber.events.TreeEvent;
import com.songoda.ultimatetimber.events.TreeFallEvent;
import com.songoda.ultimatetimber.events.TreeFellEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;
import xyz.nkomarn.Kerosene.Kerosene;

public class TreeFallListener implements Listener {
    @EventHandler
    public void onTreeFall(TreeFallEvent event) {
        if (!Kerosene.getToggles().getState(event.getPlayer().getUniqueId().toString(), "tree-feller")) event.setCancelled(true);

        if (Advancements.isComplete(event.getPlayer(), "tree")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:tree",
                event.getPlayer().getName()));
    }
}
