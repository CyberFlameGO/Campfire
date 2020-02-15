package xyz.nkomarn.Campfire.listener.chestshop;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class TransactionListener implements Listener {
    @EventHandler
    public void onTransaction(PreTransactionEvent event) {
        if (Advancements.isComplete(event.getClient(), "chestshop-buy")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:chestshop-buy",
                event.getClient().getName()));
    }
}
