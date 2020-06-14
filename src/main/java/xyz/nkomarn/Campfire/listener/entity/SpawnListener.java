package xyz.nkomarn.Campfire.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.util.ToggleUtil;

public class SpawnListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            event.getLocation().getNearbyEntities(10, 5, 10).stream()
                    .filter(entity -> entity instanceof Player)
                    .forEach(player -> Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
                        if (ToggleUtil.getToggleState(player.getUniqueId(), "armor-stand-arms")) {
                            ((ArmorStand) event.getEntity()).setArms(true);
                        }
                    }));
        }
    }
}
