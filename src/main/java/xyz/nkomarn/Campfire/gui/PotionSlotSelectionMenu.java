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
import java.sql.SQLException;
import java.util.Collections;

public class PotionSlotSelectionMenu extends Gui {
    public PotionSlotSelectionMenu(Player player, int slot) {
        super(player, String.format("Set Slot #%s", slot), 27);
        fillBorderAlternating(Material.GRAY_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE);

        ItemStack none = new ItemStack(Material.BARRIER);
        ItemMeta noneMeta = none.getItemMeta();
        noneMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lNone"));
        noneMeta.setLore(Collections.singletonList(ChatColor.GRAY + "No effects"));
        noneMeta.addEnchant(Enchantment.MENDING, 1, true);
        none.setItemMeta(noneMeta);
        addButton(new GuiButton(this, none, 10, (button, clickType) -> setPotionSlot(player, slot, "")));

        ConfigurationSection speedSection = Config.getConfig().getConfigurationSection("perks.potions.SPEED");
        ItemStack speed = new ItemStack(Material.valueOf(speedSection.getString("item")));
        ItemMeta speedMeta = speed.getItemMeta();
        speedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', speedSection.getString("name")));
        speedMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', speedSection.getString("lore"))));
        speed.setItemMeta(speedMeta);
        addButton(new GuiButton(this, speed, 11, (button, clickType) -> setPotionSlot(player, slot, "SPEED")));

        ConfigurationSection jumpBoostSection = Config.getConfig().getConfigurationSection("perks.potions.JUMP");
        ItemStack jumpBoost = new ItemStack(Material.valueOf(jumpBoostSection.getString("item")));
        ItemMeta jumpBoostMeta = jumpBoost.getItemMeta();
        jumpBoostMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', jumpBoostSection.getString("name")));
        jumpBoostMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', jumpBoostSection.getString("lore"))));
        jumpBoost.setItemMeta(jumpBoostMeta);
        addButton(new GuiButton(this, jumpBoost, 12, (button, clickType) -> setPotionSlot(player, slot, "JUMP")));

        ConfigurationSection strengthSection = Config.getConfig().getConfigurationSection("perks.potions.INCREASE_DAMAGE");
        ItemStack strength = new ItemStack(Material.valueOf(strengthSection.getString("item")));
        ItemMeta strengthMeta = strength.getItemMeta();
        strengthMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', strengthSection.getString("name")));
        strengthMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', strengthSection.getString("lore"))));
        strength.setItemMeta(strengthMeta);
        addButton(new GuiButton(this, strength, 13, (button, clickType) -> setPotionSlot(player, slot, "INCREASE_DAMAGE")));

        ConfigurationSection nightVisionSection = Config.getConfig().getConfigurationSection("perks.potions.NIGHT_VISION");
        ItemStack nightVision = new ItemStack(Material.valueOf(nightVisionSection.getString("item")));
        ItemMeta nightVisionMeta = nightVision.getItemMeta();
        nightVisionMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', nightVisionSection.getString("name")));
        nightVisionMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', nightVisionSection.getString("lore"))));
        nightVision.setItemMeta(nightVisionMeta);
        addButton(new GuiButton(this, nightVision, 14, (button, clickType) -> setPotionSlot(player, slot, "NIGHT_VISION")));

        ConfigurationSection waterBreathingSection = Config.getConfig().getConfigurationSection("perks.potions.WATER_BREATHING");
        ItemStack waterBreathing = new ItemStack(Material.valueOf(waterBreathingSection.getString("item")));
        ItemMeta waterBreathingMeta = waterBreathing.getItemMeta();
        waterBreathingMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', waterBreathingSection.getString("name")));
        waterBreathingMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', waterBreathingSection.getString("lore"))));
        waterBreathing.setItemMeta(waterBreathingMeta);
        addButton(new GuiButton(this, waterBreathing, 15, (button, clickType) -> setPotionSlot(player, slot, "WATER_BREATHING")));

        ConfigurationSection slowFallingSection = Config.getConfig().getConfigurationSection("perks.potions.SLOW_FALLING");
        ItemStack slowFalling = new ItemStack(Material.valueOf(slowFallingSection.getString("item")));
        ItemMeta slowFallingMeta = slowFalling.getItemMeta();
        slowFallingMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', slowFallingSection.getString("name")));
        slowFallingMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', slowFallingSection.getString("lore"))));
        slowFalling.setItemMeta(slowFallingMeta);
        addButton(new GuiButton(this, slowFalling, 16, (button, clickType) -> setPotionSlot(player, slot, "SLOW_FALLING")));

        open();
    }

    private void setPotionSlot(Player player, int slot, String effect) {
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try (Connection connection = PlayerData.getConnection()) {
                String query = String.format("UPDATE `potions` SET `slot%s` = ? WHERE `uuid` = ?;", slot);
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, effect);
                    statement.setString(2, player.getUniqueId().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (effect.length() < 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.8f, 0.6f);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_WITCH_DRINK, 1.0f, 0.9f);
                }
                new PotionSlotsMenu(player);
            }
        });
    }
}
