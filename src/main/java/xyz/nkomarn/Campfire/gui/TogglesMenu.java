package xyz.nkomarn.Campfire.gui;

import com.cnaude.chairs.core.Chairs;
import com.cnaude.chairs.core.PlayerSitData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.listener.PvPListener;
import xyz.nkomarn.Kerosene.gui.Gui;
import xyz.nkomarn.Kerosene.gui.GuiButton;
import xyz.nkomarn.Kerosene.util.ToggleUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TogglesMenu extends Gui {
    public TogglesMenu(Player player) {
        super(player, "Toggles", 27);

        UUID uuid = player.getUniqueId();
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.ORANGE_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE);

        ItemStack armorStandToggle = new ItemStack(Material.ARMOR_STAND, 1);
        ItemMeta armorStandToggleMeta = armorStandToggle.getItemMeta();
        armorStandToggleMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lArmor Stand Arms"));
        armorStandToggleMeta.setLore(getStatusLore(ToggleUtil.getToggleState(uuid, "armor-stand-arms")));
        armorStandToggle.setItemMeta(armorStandToggleMeta);
        addButton(new GuiButton(this, armorStandToggle, 12, (button, clickType) -> {
            boolean state = toggleState(uuid, "armor-stand-arms");
            ItemMeta meta = button.getItem().getItemMeta();
            meta.setLore(getStatusLore(state));
            button.setItemMeta(meta);
        }));

        ItemStack pvpToggle = new ItemStack(Material.GOLDEN_SWORD, 1);
        ItemMeta pvpToggleMeta = pvpToggle.getItemMeta();
        pvpToggleMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lPVP"));
        pvpToggleMeta.setLore(getStatusLore(PvPListener.ENABLED_PLAYERS.contains(uuid)));
        pvpToggle.setItemMeta(pvpToggleMeta);
        addButton(new GuiButton(this, pvpToggle, 13, (button, clickType) -> {
            if (PvPListener.ENABLED_PLAYERS.contains(uuid)) {
                PvPListener.ENABLED_PLAYERS.remove(uuid);
            } else {
                PvPListener.ENABLED_PLAYERS.add(uuid);
            }

            ItemMeta meta = button.getItem().getItemMeta();
            meta.setLore(getStatusLore(PvPListener.ENABLED_PLAYERS.contains(uuid)));
            button.setItemMeta(meta);
        }));

        ItemStack chairsToggle = new ItemStack(Material.SPRUCE_STAIRS, 1);
        ItemMeta chairsToggleMeta = chairsToggle.getItemMeta();
        chairsToggleMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lChairs"));
        chairsToggleMeta.setLore(getStatusLore(!Chairs.getInstance().getPlayerSitData().isSittingDisabled(uuid)));
        chairsToggle.setItemMeta(chairsToggleMeta);
        addButton(new GuiButton(this, chairsToggle, 14, (button, clickType) -> {
            PlayerSitData sitData = Chairs.getInstance().getPlayerSitData();
            if (sitData.isSittingDisabled(uuid)) {
                sitData.enableSitting(uuid);
            } else {
                sitData.disableSitting(uuid);
            }

            ItemMeta meta = button.getItem().getItemMeta();
            meta.setLore(getStatusLore(!Chairs.getInstance().getPlayerSitData().isSittingDisabled(uuid)));
            button.setItemMeta(meta);
        }));

        open();
    }

    private boolean toggleState(UUID uuid, String toggleName) {
        boolean newState = !ToggleUtil.getToggleState(uuid, toggleName);
        ToggleUtil.setToggleState(uuid, toggleName, newState);
        return newState;
    }

    private List<String> getStatusLore(boolean state) {
        return Collections.singletonList(ChatColor.translateAlternateColorCodes('&',
                state ? "&7Status: &aEnabled" : "&7Status: &cDisabled"));
    }
}
