package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.util.ToggleUtil;

public class EntitySpawnListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        for (Entity entity : event.getLocation().getNearbyEntities(10, 10, 10)) {
            if (entity instanceof Player) {
                Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> ((ArmorStand) event.getEntity())
                        .setArms(ToggleUtil.getToggleState(entity.getUniqueId(), "armor-stand-arms")));
            }
        }
    }
}
