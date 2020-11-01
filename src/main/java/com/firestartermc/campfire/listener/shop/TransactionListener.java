package com.firestartermc.campfire.listener.shop;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import com.firestartermc.campfire.Campfire;

public class TransactionListener implements Listener {

    private Campfire campfire;

    public TransactionListener(Campfire campfire) {
        this.campfire = campfire;
    }

    @EventHandler(ignoreCancelled = true)
    public void onTransaction(@NotNull TransactionEvent event) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(event.getOwnerAccount().getUuid());

        if (owner.isOnline()) {
            return;
        }

        campfire.getShopLog().log(owner, event);
    }
}
