package xyz.nkomarn.campfire.listener.entity;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.kerosene.data.Toggle;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(@NotNull EntitySpawnEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            event.getLocation().getNearbyEntities(5, 3, 5).stream()
                    .filter(entity -> entity instanceof Player)
                    .forEach(player -> {
                        if (Toggle.get(player.getUniqueId(), "armor-stand-arms")) {
                            ((ArmorStand) event.getEntity()).setArms(true);
                        }
                    });
        } else if (event.getEntity() instanceof ItemFrame) {
            event.getLocation().getNearbyEntities(5, 3, 5).stream()
                    .filter(entity -> entity instanceof Player)
                    .forEach(player -> {
                        if (Toggle.get(player.getUniqueId(), "invisible-item-frames")) {
                            ((ItemFrame) event.getEntity()).setVisible(false);
                        }
                    });
        }
    }
}
