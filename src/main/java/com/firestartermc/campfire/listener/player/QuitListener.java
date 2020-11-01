package com.firestartermc.campfire.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import com.firestartermc.campfire.Campfire;

public class QuitListener implements Listener {

    private Campfire campfire;

    public QuitListener(Campfire campfire) {
        this.campfire = campfire;
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        campfire.getShopLog().clear(event.getPlayer());
    }
}
