package xyz.nkomarn.Campfire.listener.auction;

import me.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class AuctionBuyListener implements Listener {
    @EventHandler
    public void onAuctionBuy(AuctionBuyEvent event) {
        if (Advancements.isComplete(event.getPlayer(), "ah-sold")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:ah-sold",
                event.getPlayer().getName())); // TODO this doesn't work as intended, might have to modify API
    }
}
