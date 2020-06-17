package xyz.nkomarn.Campfire.menu;

import com.cnaude.chairs.core.Chairs;
import com.cnaude.chairs.core.PlayerSitData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Campfire.listener.PvPListener;
import xyz.nkomarn.Campfire.util.ItemBuilder;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.menu.MenuButton;
import xyz.nkomarn.Kerosene.util.ToggleUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TogglesMenu extends Menu {
    public TogglesMenu(Player player) {
        super(player, "Toggles", 27);

        UUID uuid = player.getUniqueId();
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.ORANGE_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE);

        // ArmorStand toggle
        ItemStack armorStandToggle = new ItemBuilder(Material.ARMOR_STAND)
                .withDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lArmor Stand Arms"))
                .withLore(getStatusLore(ToggleUtil.getToggleState(uuid, "armor-stand-arms")))
                .build();
        addButton(new MenuButton(this, armorStandToggle, 12, (button, clickType) -> {
            boolean state = toggleState(uuid, "armor-stand-arms");
            ItemStack newItem = ItemBuilder.of(button.getItem())
                    .withLore(getStatusLore(state))
                    .build();
            button.setItem(newItem);
        }));

        // PVP toggle
        ItemStack pvpToggle = new ItemBuilder(Material.GOLDEN_SWORD)
                .withDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lPVP"))
                .withLore(getStatusLore(PvPListener.ENABLED_PLAYERS.contains(uuid)))
                .build();
        addButton(new MenuButton(this, pvpToggle, 13, (button, clickType) -> {
            if (PvPListener.ENABLED_PLAYERS.contains(uuid)) PvPListener.ENABLED_PLAYERS.remove(uuid);
            else  PvPListener.ENABLED_PLAYERS.add(uuid);

            ItemStack newItem = ItemBuilder.of(button.getItem())
                    .withLore(getStatusLore(PvPListener.ENABLED_PLAYERS.contains(uuid)))
                    .build();
            button.setItem(newItem);
        }));

        // Chairs toggle
        ItemStack chairsToggle = new ItemBuilder(Material.SPRUCE_STAIRS)
                .withDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lChairs"))
                .withLore(getStatusLore(!Chairs.getInstance().getPlayerSitData().isSittingDisabled(uuid)))
                .build();
        addButton(new MenuButton(this, chairsToggle, 14, (button, clickType) -> {
            PlayerSitData sitData = Chairs.getInstance().getPlayerSitData();
            if (sitData.isSittingDisabled(uuid)) sitData.enableSitting(uuid);
            else sitData.disableSitting(uuid);

            ItemStack newItem = ItemBuilder.of(button.getItem())
                    .withLore(getStatusLore(!Chairs.getInstance().getPlayerSitData().isSittingDisabled(uuid)))
                    .build();
            button.setItem(newItem);
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
