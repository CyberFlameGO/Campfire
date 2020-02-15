package xyz.nkomarn.Campfire.listener.claim;

import me.ryanhamshire.GriefPrevention.events.TrustChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class TrustChangedListener implements Listener {
    @EventHandler
    public void onTrustChanged(TrustChangedEvent event) {
        if (Advancements.isComplete(event.getChanger(), "claim-trust")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:claim-trust",
                event.getChanger().getName()));
    }
}
