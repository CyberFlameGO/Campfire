package xyz.nkomarn.Campfire.listener.player;

import me.ryanhamshire.GriefPrevention.events.AccrueClaimBlocksEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Kerosene.util.ToggleUtil;

public class AccrueClaimBlocks implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onAccrue(AccrueClaimBlocksEvent event) {
        if (event.getPlayer().hasPermission("campfire.perks.claim-boost")) {
            if (ToggleUtil.get(event.getPlayer().getUniqueId(), "claim-boost")) {
                event.setBlocksToAccrue(event.getBlocksToAccrue() * 2);
            }
        }
    }
}
