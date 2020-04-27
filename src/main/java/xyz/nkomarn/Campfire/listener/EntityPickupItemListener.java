package xyz.nkomarn.Campfire.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Campfire.util.Config;

import java.util.Collections;

public class EntityPickupItemListener implements Listener {
    @EventHandler // Add bee count to the lore of bee nests and beehives
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final ItemStack pickedUpItem = event.getItem().getItemStack();
        final Material itemType = pickedUpItem.getType();

        if (itemType == Material.BEEHIVE || itemType == Material.BEE_NEST) {
            BlockStateMeta stateMeta = (BlockStateMeta) pickedUpItem.getItemMeta();
            Beehive hive = (Beehive) stateMeta.getBlockState();

            ItemMeta meta = pickedUpItem.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&',
                    String.format(Config.getString("messages.beehive"), hive.getEntityCount())))
            );
            pickedUpItem.setItemMeta(meta);
            event.getItem().setItemStack(pickedUpItem);
        }
    }
}
