package xyz.nkomarn.Campfire.listener.claim;

import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class ClaimCreatedListener implements Listener {
    @EventHandler
    public void onClaimCreated(ClaimCreatedEvent event) {
        if (!(event.getCreator() instanceof Player)) return;
        if (Advancements.isComplete((Player) event.getCreator(), "claim")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:claim",
                event.getCreator().getName()));
    }
}
