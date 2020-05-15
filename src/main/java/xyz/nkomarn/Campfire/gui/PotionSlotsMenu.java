package xyz.nkomarn.Campfire.gui;

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
import xyz.nkomarn.Kerosene.data.PlayerData;
import xyz.nkomarn.Kerosene.gui.Gui;
import xyz.nkomarn.Kerosene.gui.GuiButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

public class PotionSlotsMenu extends Gui {
    public PotionSlotsMenu(Player player) {
        super(player, "Potion Slots", 27);
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.PINK_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE);

        ItemStack back = new ItemStack(Material.PAPER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lBack"));
        back.setItemMeta(backMeta);
        addButton(new GuiButton(this, back, 10, (button, clickType) -> new PerksMenu(player)));

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try (Connection connection = PlayerData.getConnection()) {
                createSlots(connection, player);
                Arrays.asList(1, 2, 3).forEach(slot -> {
                    if (player.hasPermission(String.format("campfire.perks.potions.%s", slot))) {
                        addButton(new GuiButton(this, getSlotItem(connection, player, slot), 11 + slot, (button, shiftClicked) -> {
                            if (button.getItem().getType() != Material.BARRIER) new PotionSlotSelectionMenu(player, slot);
                        }));
                    } else {
                        ItemStack unavailable = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                        ItemMeta unavailableMeta = unavailable.getItemMeta();
                        unavailableMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7&lUnavailable Slot"));
                        unavailableMeta.setLore(Arrays.asList(
                                ChatColor.GRAY + "Upgrade your rank to",
                                ChatColor.GRAY + "use more effect slots."
                        ));
                        unavailable.setItemMeta(unavailableMeta);
                        addButton(new GuiButton(this, unavailable, 11 + slot, (button, clickType) ->
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f)));
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }

            open();
        });
    }

    private void createSlots(Connection connection, Player player) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO `potions`(`uuid`) VALUES (?);")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ItemStack getSlotItem(Connection connection, Player player, int slot) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT slot" + slot + " FROM potions WHERE uuid = ?;")) {
            statement.setString(1, player.getUniqueId().toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    if (result.getString(1).trim().length() < 1) {
                        ItemStack empty = new ItemStack(Material.GRAY_CARPET);
                        ItemMeta emptyMeta = empty.getItemMeta();
                        emptyMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lEmpty Slot"));
                        emptyMeta.setLore(Arrays.asList(
                                ChatColor.GRAY + "Click on this slot",
                                ChatColor.GRAY + "to set its effect."
                        ));
                        empty.setItemMeta(emptyMeta);
                        return empty;
                    } else {
                        ConfigurationSection section = Config.getConfig().getConfigurationSection(String
                                .format("perks.potions.%s", result.getString(1)));
                        ItemStack slotItem = new ItemStack(Material.getMaterial(section.getString("item")));
                        ItemMeta slotItemMeta = slotItem.getItemMeta();
                        slotItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
                        slotItemMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', section.getString("lore"))));
                        slotItemMeta.addEnchant(Enchantment.MENDING, 1, true);
                        slotItem.setItemMeta(slotItemMeta);
                        return slotItem;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ItemStack error = new ItemStack(Material.BARRIER);
        ItemMeta errorMeta = error.getItemMeta();
        errorMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lError"));
        errorMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Something broke internally.",
                ChatColor.GRAY + "Notify a staff member."
        ));
        error.setItemMeta(errorMeta);
        return error;
    }
}
