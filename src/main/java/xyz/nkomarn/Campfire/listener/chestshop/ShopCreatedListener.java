package xyz.nkomarn.Campfire.listener.chestshop;

import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class ShopCreatedListener implements Listener {
    @EventHandler
    public void onShopCreated(ShopCreatedEvent event) {
        if (Advancements.isComplete(event.getPlayer(), "chestshop-create")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:chestshop-create",
                event.getPlayer().getName()));
    }
}
