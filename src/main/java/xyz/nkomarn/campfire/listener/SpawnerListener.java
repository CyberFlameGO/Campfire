package xyz.nkomarn.campfire.listener;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.kerosene.util.item.ItemBuilder;

public class SpawnerListener implements Listener {

    private static final ItemStack BLANK_SPAWNER;

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SPAWNER) {
            return;
        }

        event.setExpToDrop(0);
        ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();

        if (!heldItem.getType().toString().contains("_PICKAXE")) {
            return;
        }

        if (heldItem.getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH) != 2) {
            return;
        }

        if (event.getBlock().getWorld().getName().equalsIgnoreCase("world_nether")) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), BLANK_SPAWNER);
            return;
        }

        BlockState blockState = event.getBlock().getState();
        CreatureSpawner spawner = (CreatureSpawner) blockState;
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getSpawnerItem(spawner.getSpawnedType()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.SPAWNER) {
            return;
        }

        NamespacedKey key = new NamespacedKey(Campfire.getCampfire(), "spawner");
        PersistentDataContainer container = event.getItemInHand().getItemMeta().getPersistentDataContainer();

        if (container.has(key, PersistentDataType.STRING)) {
            CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
            spawner.setSpawnedType(EntityType.valueOf(container.get(key, PersistentDataType.STRING)));
            spawner.update();
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
        spawner.setSpawnedType(EntityType.PLAYER);
        spawner.update();
    }

    @EventHandler(ignoreCancelled = true)
    public void onRename(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.SPAWNER) {
            event.setCancelled(true);
        }
    }

    @NotNull
    private ItemStack getSpawnerItem(@NotNull EntityType type) {
        if (type == EntityType.PLAYER) {
            return BLANK_SPAWNER;
        }

        return new ItemBuilder(Material.SPAWNER)
                .name("&b&l" + WordUtils.capitalize(type.name().toLowerCase().replace("_", " ")) + " &3&lSpawner")
                .lore("&fMob type can be changed by", "&fright-clicking with a spawn egg.")
                .persistData(new NamespacedKey(Campfire.getCampfire(), "spawner"), PersistentDataType.STRING, type.name())
                .build();
    }

    static {
        BLANK_SPAWNER = new ItemBuilder(Material.SPAWNER)
                .name("&d&lBlank &5&lSpawner")
                .lore("&fMob type can be changed by", "&fright-clicking with a spawn egg.")
                .build();
    }
}
