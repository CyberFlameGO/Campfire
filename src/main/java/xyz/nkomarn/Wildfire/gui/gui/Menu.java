package xyz.nkomarn.Wildfire.gui.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Wildfire.gui.GUIHolder;
import xyz.nkomarn.Wildfire.gui.GUIType;

import java.util.Arrays;

public class Menu {
    public Menu(final Player player) {
        Inventory gui = Bukkit.createInventory(new GUIHolder(GUIType.MENU), 27, "Perks Menu");
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 14, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26)
                .forEach(slot -> gui.setItem(slot, glass));


    }
}
