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
import java.util.concurrent.atomic.AtomicInteger;

public class PotionSlotSelectionMenu extends Menu {
    public PotionSlotSelectionMenu(Player player, int slot) {
        super(player, String.format("Set Slot #%s", slot), 27);
        fillBorderAlternating(Material.GRAY_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE);

        ItemStack none = new ItemStack(Material.BARRIER);
        ItemMeta noneMeta = none.getItemMeta();
        noneMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lNone"));
        noneMeta.setLore(Collections.singletonList(ChatColor.GRAY + "No effects"));
        noneMeta.addEnchant(Enchantment.MENDING, 1, true);
        none.setItemMeta(noneMeta);
        addButton(new MenuButton(this, none, 10, (button, clickType) -> setPotionSlot(player, slot, null)));

        AtomicInteger effectSlot = new AtomicInteger(11);
        ConfigurationSection section = Config.getConfig().getConfigurationSection("perks.potions");
        section.getKeys(false).forEach(effectName -> {
            ConfigurationSection effectSection = section.getConfigurationSection(effectName);
            ItemStack effectItem = new ItemBuilder(Material.valueOf(effectSection.getString("item")))
                    .name(effectSection.getString("name"))
                    .lore(Collections.singletonList(effectSection.getString("lore")))
                    .build();
            addButton(new MenuButton(this, effectItem, effectSlot.getAndIncrement(), ((button, clickType) ->
                    setPotionSlot(player, slot, effectName))));
        });

        open();
    }

    /**
     * Cache an effect slot and update the database. Also play sound effects based on effect.
     *
     * @param player   The player for which to update the effect slot.
     * @param slot   The effect slot to update.
     * @param effect The effect to which to set the slot to.
     */
    private void setPotionSlot(Player player, int slot, String effect) {
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            EffectsCache.put(player.getUniqueId(), slot, effect);
            if (effect == null) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.8f, 0.6f);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_WITCH_DRINK, 1.0f, 0.9f);
            }
            new PotionSlotsMenu(player);
        });
    }
}
