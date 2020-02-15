package xyz.nkomarn.Campfire.listener.claim;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.ryanhamshire.GriefPrevention.events.AccrueClaimBlocksEvent;
import xyz.nkomarn.Campfire.util.Advancements;

public class AccrueClaimBlocksListener implements Listener {
    @EventHandler
    public void onAccrueClaimBlocks(AccrueClaimBlocksEvent event) {
        if (Advancements.isComplete(event.getPlayer(), "claim-blocks")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:claim-blocks",
                event.getPlayer().getName()));
    }
}
