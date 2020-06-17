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
import xyz.nkomarn.Campfire.task.EffectsTask;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.data.PlayerData;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.menu.MenuButton;
import xyz.nkomarn.Kerosene.util.item.ItemBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;

public class PotionSlotsMenu extends Menu {

    private static final String SQL_INSERT_SLOTS = "INSERT IGNORE INTO `potions`(`uuid`) VALUES (?);";

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
            if (!EffectsTask.EFFECT_CACHE.containsKey(player.getUniqueId())) {
                createSlots(player.getUniqueId());
                EffectsTask.EFFECT_CACHE.put(player.getUniqueId(), new String[] { "", "", "" });
            }

            // render
            for (int slot = 1; slot <= 3; slot++) {
                if (player.hasPermission(String.format("campfire.perks.potions.%s", slot))) {
                    final int finalSlot = slot;
                    addButton(new MenuButton(this, getSlotItem(player.getUniqueId(), slot), 11 + slot, ((menuButton, clickType) -> {
                        new PotionSlotSelectionMenu(player, finalSlot);
                    })));
                } else {
                    ItemStack unavailable = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .name("&7&lUnavailable Slot")
                            .lore(
                                    ChatColor.GRAY + "Upgrade your rank to",
                                    ChatColor.GRAY + "use more effect slots."
                            ).build();
                    addButton(new MenuButton(this, unavailable, 11 + slot, (button, clickType) -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f);
                    }));
                }
            }

            open();
        });
    }

    private void createSlots(UUID uniqueId) {
        try (Connection connection = PlayerData.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_SLOTS)) {
                statement.setString(1, uniqueId.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ItemStack getSlotItem(UUID uniqueId, int slot) {
        String effect = EffectsTask.EFFECT_CACHE.get(uniqueId)[slot - 1];

        if (effect == null) {
            return new ItemBuilder(Material.GRAY_CARPET)
                    .name("&f&lEmpty Slot")
                    .lore(
                            ChatColor.GRAY + "Click on this slot",
                            ChatColor.GRAY + "to set its effect."
                    )
                    .build();
        }

        ConfigurationSection section = Config.getConfig().getConfigurationSection("perks.potions." + effect);
        return new ItemBuilder(Material.getMaterial(section.getString("item")))
                .name(section.getString("name"))
                .lore(Collections.singletonList(section.getString("lore")))
                .enchantUnsafe(Enchantment.MENDING, 1)
                .build();
    }

}
