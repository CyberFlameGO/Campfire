package xyz.nkomarn.Campfire.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Campfire.util.Config;

import java.util.Collections;

public class EntityPickupItemListener implements Listener {
    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final ItemStack pickedUpItem = event.getItem().getItemStack();
        final Material itemType = pickedUpItem.getType();

        // Add bee count to the lore of bee nests and beehives
        if (itemType != Material.BEE_NEST && itemType != Material.BEEHIVE) return;
        final net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(pickedUpItem);
        int beeCount = 0;

        try {
            final String nmsContent = nmsItemStack.getTag().toString();
            final String query = "HasStung:";
            for (int i = 0; i < nmsContent.length(); i++) {
                if (nmsContent.substring(i).startsWith(query)) beeCount++;
            }
        } catch (Exception exception) {
            beeCount = 0;
        }

        ItemMeta itemMeta = pickedUpItem.getItemMeta();
        itemMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&',
                String.format(Config.getString("messages.beehive"), beeCount)))
        );
        pickedUpItem.setItemMeta(itemMeta);
        event.getItem().setItemStack(pickedUpItem);
    }
}
