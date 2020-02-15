package xyz.nkomarn.Campfire.listener.auction;

import me.badbones69.crazyauctions.api.events.AuctionListEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class AuctionListListener implements Listener {
    @EventHandler
    public void onAuctionList(AuctionListEvent event) {
        if (Advancements.isComplete( event.getPlayer(), "ah-sell")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:ah-sell",
                event.getPlayer().getName()));
    }
}
