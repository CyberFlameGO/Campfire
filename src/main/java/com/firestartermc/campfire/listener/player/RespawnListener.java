package com.firestartermc.campfire.listener.player;

import com.firestartermc.campfire.Campfire;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final Campfire campfire;

    public RespawnListener(Campfire campfire) {
        this.campfire = campfire;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRespawn(PlayerDeathEvent event) {
        Bukkit.getScheduler().runTaskLater(campfire, () -> {
            event.getEntity().sendTitle("", "/back to go to death location", 10, 140, 10);
        }, 20L);
    }
}
