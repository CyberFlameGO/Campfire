package xyz.nkomarn.Campfire.gui.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.gui.GuiHolder;
import xyz.nkomarn.Campfire.gui.GuiType;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.data.LocalStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

public class PotionSlotsMenu {
    public PotionSlotsMenu(Player player) {
        Inventory menu = Bukkit.createInventory(new GuiHolder(GuiType.SLOTS, 1), 27, "Potion Slots");

        ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26)
                .forEach(s -> menu.setItem(s, glass));

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            for (int i = 1; i < 3; i++) {
                if (player.hasPermission(String.format("campfire.perk.potions.%s", i))) {
                    menu.setItem(11 + i, getSlotItem(player, i));
                } else {
                    ItemStack unavailable = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta unavailableMeta = unavailable.getItemMeta();
                    unavailableMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7&lUnavailable Slot"));
                    unavailableMeta.setLore(Arrays.asList(
                            ChatColor.GRAY + "Upgrade your rank to",
                            ChatColor.GRAY + "use more effect slots."
                    ));
                    unavailable.setItemMeta(unavailableMeta);
                    menu.setItem(11 + i, getSlotItem(player, i));
                }
            }
            Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> player.openInventory(menu));
        });
    }

    private ItemStack getSlotItem(Player player, int slot) {
        try (Connection connection = LocalStorage.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(String.format("SELECT slot%s FROM " +
                    "potions WHERE uuid = ?;", slot))) {
                statement.setString(1, player.getUniqueId().toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        if (result.getString(1).equalsIgnoreCase("NONE")) {
                            ItemStack none = new ItemStack(Material.GRAY_CARPET);
                            ItemMeta noneMeta = none.getItemMeta();
                            noneMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lEmpty Slot"));
                            noneMeta.setLore(Arrays.asList(
                                    ChatColor.GRAY + "Click on this slot",
                                    ChatColor.GRAY + "to set the effect."
                            ));
                            none.setItemMeta(noneMeta);
                            return none;
                        } else {
                            ConfigurationSection section = Config.getConfig().getConfigurationSection(String
                                    .format("perks.potions.%s", result.getString(1)));
                            ItemStack slotItem = new ItemStack(Material.getMaterial(section.getString("item"))); // TODO null check
                            ItemMeta slotItemMeta = slotItem.getItemMeta();
                            slotItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
                            slotItemMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', section.getString("lore"))));
                            slotItem.setItemMeta(slotItemMeta);
                            return slotItem;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ItemStack(Material.BARRIER);
    }
}
