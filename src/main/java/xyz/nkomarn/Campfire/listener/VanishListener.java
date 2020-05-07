package xyz.nkomarn.Campfire.listener;

import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.nkomarn.Kerosene.util.VanishUtil;

import java.lang.reflect.Field;

public class VanishListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        if (VanishUtil.isVanished(event.getPlayer())) {
            if (event.getMessage().endsWith("/")) {
                event.setMessage(event.getMessage().substring(0, event.getMessage().length() - 1));
            } else {
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lVanish: &7You can't send chat messages unless they end with &c/&7."
                ));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (VanishUtil.isVanished(event.getPlayer())) {
            if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null &&
                    event.getClickedBlock().getType().equals(Material.FARMLAND)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

    }

    @EventHandler(ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player && VanishUtil.isVanished((Player) event.getEntity())) {
            // TODO pickup toggle
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            if (VanishUtil.isVanished((Player) event.getTarget())) {
                event.setCancelled(true);
            }
        }
    }
}
