package xyz.nkomarn.Campfire.gui.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Campfire.gui.GuiHolder;
import xyz.nkomarn.Campfire.gui.GuiType;
import xyz.nkomarn.Kerosene.util.ToggleUtil;

import java.util.Arrays;
import java.util.Collections;

public class TogglesMenu {
    public TogglesMenu(final Player player) {
        final Inventory menu = Bukkit.createInventory(new GuiHolder(GuiType.TOGGLES, 1), 27, "Toggles");

        ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta redGlassMeta = redGlass.getItemMeta();
        redGlassMeta.setDisplayName(" ");
        redGlass.setItemMeta(redGlassMeta);
        Arrays.asList(0, 2, 4, 6, 8, 18, 20, 22, 24, 26)
                .forEach(slot -> menu.setItem(slot, redGlass));

        ItemStack orangeGlass = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE, 1);
        ItemMeta orangeGlassMeta = orangeGlass.getItemMeta();
        orangeGlassMeta.setDisplayName(" ");
        orangeGlass.setItemMeta(orangeGlassMeta);
        Arrays.asList(1, 3, 5, 7, 9, 17, 19, 21, 23, 25)
                .forEach(slot -> menu.setItem(slot, orangeGlass));

        ItemStack armorStandToggle = new ItemStack(Material.ARMOR_STAND, 1);
        ItemMeta armorStandToggleMeta = armorStandToggle.getItemMeta();
        armorStandToggleMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lArmor Stand Arms"));
        armorStandToggleMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&',
                String.format("&7Status: %s", getStatus(player, "armor-stand-arms")))));
        armorStandToggle.setItemMeta(armorStandToggleMeta);
        menu.setItem(13, armorStandToggle);

        player.openInventory(menu);
    }

    private String getStatus(final Player player, final String type) {
        final boolean status = ToggleUtil.getToggleState(player.getUniqueId(), type);
        if (status) return "&aEnabled";
        else return "&cDisabled";
    }
}
