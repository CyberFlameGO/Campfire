package xyz.nkomarn.Campfire.gui.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.gui.GuiHolder;
import xyz.nkomarn.Campfire.gui.GuiType;
import xyz.nkomarn.Kerosene.Kerosene;

import java.util.Arrays;
import java.util.Collections;

public class TogglesMenu {
    public TogglesMenu(final Player player) {
        Inventory menu = Bukkit.createInventory(new GuiHolder(GuiType.TOGGLES, 1), 27, "Toggles");

        new BukkitRunnable() {
            @Override
            public void run() {
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

                ItemStack treeToggle = new ItemStack(Material.DIAMOND_AXE, 1);
                ItemMeta treeToggleMeta = treeToggle.getItemMeta();
                treeToggleMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lTree Feller"));
                treeToggleMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&',
                        String.format("&7Status: %s", getStatus(player, "tree-feller")))));
                treeToggle.setItemMeta(treeToggleMeta);
                treeToggle.addEnchantment(Enchantment.MENDING, 1);
                treeToggle.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                menu.setItem(13, treeToggle);

                player.openInventory(menu);
            }
        }.runTask(Campfire.getCampfire());
    }

    private String getStatus(final Player player, final String type) {
        final boolean status = Kerosene.getToggles().getState(player.getUniqueId().toString(), type);
        if (status) return "&aEnabled";
        else return "&cDisabled";
    }
}
