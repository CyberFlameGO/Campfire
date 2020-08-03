package xyz.nkomarn.campfire.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.kerosene.util.item.ItemBuilder;

import java.util.Optional;

public class SpawnerListener implements Listener {

    private static final ItemStack BLANK_SPAWNER;

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SPAWNER) {
            return;
        }

        ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();

        if (!heldItem.getType().toString().contains("_PICKAXE")) {
            return;
        }

        if (heldItem.getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH) != 2) {
            return;
        }

        event.setExpToDrop(0);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), BLANK_SPAWNER);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.SPAWNER) {
            return;
        }

        Optional<EntityType> msType = getMSEntity(event.getItemInHand());
        if (msType.isPresent()) {
            CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
            spawner.setSpawnedType(msType.get());
            spawner.update();
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
        spawner.setSpawnedType(EntityType.PLAYER);
        spawner.update();
    }

    @EventHandler(ignoreCancelled = true) // TODO remove when MS support is removed
    public void onRename(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.SPAWNER) {
            event.setCancelled(true);
        }
    }

    public static Optional<EntityType> getMSEntity(@NotNull ItemStack spawner) {
        String entityName = ChatColor.stripColor(spawner.getItemMeta().getDisplayName()).split(" Spawner")[0].replace("[", "").replace(" ", "_").toUpperCase();

        for (EntityType type : EntityType.values()) {
            if (type.name().equals(entityName.trim().toUpperCase())) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }

    static {
        BLANK_SPAWNER = new ItemBuilder(Material.SPAWNER)
                .name("&d&lBlank &5&lSpawner")
                .lore("&fMob type can be changed by", "&fright-clicking with a spawn egg.")
                .build();
    }
}
