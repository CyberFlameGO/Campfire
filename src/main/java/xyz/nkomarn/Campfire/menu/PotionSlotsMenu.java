package xyz.nkomarn.Campfire.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.cache.EffectsCache;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.menu.MenuButton;
import xyz.nkomarn.Kerosene.util.item.ItemBuilder;

import java.util.Collections;
import java.util.UUID;

public class PotionSlotsMenu extends Menu {

    public PotionSlotsMenu(Player player) {
        super(player, "Potion Slots", 27);
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.PINK_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE);

        ItemStack back = new ItemStack(Material.PAPER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lBack"));
        back.setItemMeta(backMeta);
        addButton(new MenuButton(this, back, 10, (button, clickType) -> new PerksMenu(player)));

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            for (int slot = 1; slot <= 3; slot++) {
                if (player.hasPermission(String.format("campfire.perks.potions.%s", slot))) {
                    final int finalSlot = slot;
                    addButton(new MenuButton(this, getSlotItem(player.getUniqueId(), slot), 11 + slot, ((button, clickType) -> {
                        if (clickType.isShiftClick()) {
                            EffectsCache.put(player.getUniqueId(), finalSlot, null);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.8f, 0.6f);
                            button.setItem(getSlotItem(player.getUniqueId(), finalSlot));
                        } else {
                            new PotionSlotSelectionMenu(player, finalSlot);
                        }
                    })));
                } else {
                    ItemStack unavailable = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .name("&7&lUnavailable Slot")
                            .lore("&7Upgrade your rank to", "&7use more effect slots.")
                            .build();
                    addButton(new MenuButton(this, unavailable, 11 + slot, (button, clickType) -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f);
                    }));
                }
            }
            open();
        });
    }

    /**
     * Returns an ItemStack that represents a player's potion effect slot.
     *
     * @param uuid The player which the slots belong to.
     * @param slot The slot number (1-3).
     * @return An ItemStack representing the effect in the slot.
     */
    private ItemStack getSlotItem(UUID uuid, int slot) {
        String effect = EffectsCache.get(uuid)[slot - 1];
        if (effect == null) {
            return new ItemBuilder(Material.GRAY_CARPET)
                    .name("&f&lEmpty Slot")
                    .lore("&7Click on this slot", "&7to set its effect.")
                    .build();
        }

        ConfigurationSection effectSection = Config.getConfig().getConfigurationSection("perks.potions." + effect);
        return new ItemBuilder(Material.getMaterial(effectSection.getString("item")))
                .name(effectSection.getString("name"))
                .lore(Collections.singletonList(effectSection.getString("lore")))
                .enchantUnsafe(Enchantment.MENDING, 1)
                .build();
    }
}
