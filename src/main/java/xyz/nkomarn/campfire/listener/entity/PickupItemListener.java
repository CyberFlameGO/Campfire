package xyz.nkomarn.campfire.listener.entity;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.kerosene.util.item.ItemBuilder;

public class PickupItemListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPickupItem(@NotNull EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack item = event.getItem().getItemStack();
            Material material = item.getType();

            if (material == Material.BEEHIVE || material == Material.BEE_NEST) {
                BlockStateMeta stateMeta = (BlockStateMeta) item.getItemMeta();
                Beehive hive = (Beehive) stateMeta.getBlockState();

                event.getItem().setItemStack(ItemBuilder.of(item)
                        .lore(ChatColor.WHITE + String.format("%s bee(s)", hive.getEntityCount()))
                        .build());
            }
        }
    }
}
