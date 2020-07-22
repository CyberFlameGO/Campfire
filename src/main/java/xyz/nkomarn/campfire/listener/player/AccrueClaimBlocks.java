package xyz.nkomarn.campfire.listener.player;

import me.ryanhamshire.GriefPrevention.events.AccrueClaimBlocksEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.kerosene.data.Toggle;

public class AccrueClaimBlocks implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAccrue(@NotNull AccrueClaimBlocksEvent event) {
        if (event.getPlayer().hasPermission("campfire.perks.claim-boost") && Toggle.get(event.getPlayer().getUniqueId(), "claim-boost")) {
            event.setBlocksToAccrue(event.getBlocksToAccrue() * 2);
        }
    }
}
