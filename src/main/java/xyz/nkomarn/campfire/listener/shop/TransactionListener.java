package xyz.nkomarn.campfire.listener.shop;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.log.ShopLog;

public class TransactionListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onTransaction(@NotNull TransactionEvent event) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(event.getOwnerAccount().getUuid());

        if (!owner.isOnline()) {
            ShopLog.log(owner, event);
        }
    }
}
