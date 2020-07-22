package xyz.nkomarn.campfire.menu;

import com.cnaude.chairs.core.Chairs;
import com.cnaude.chairs.core.PlayerSitData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import xyz.nkomarn.campfire.listener.PvPListener;
import xyz.nkomarn.kerosene.data.Toggle;
import xyz.nkomarn.kerosene.menu.Menu;
import xyz.nkomarn.kerosene.menu.MenuButton;
import xyz.nkomarn.kerosene.util.item.ItemBuilder;

import java.util.UUID;

public class TogglesMenu extends Menu {
    public TogglesMenu(Player player) {
        super(player, "Toggles", 27);

        UUID uuid = player.getUniqueId();
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.CYAN_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE);

        // Armor Stand Arms
        ItemBuilder armorStandToggle = new ItemBuilder(Material.ARMOR_STAND)
                .name("&f&lArmor Stand Arms")
                .lore(
                        "&7Makes every armor stand",
                        "&7you place have arms.",
                        getStatusText(Toggle.get(uuid, "armor-stand-arms"))
                )
                .addItemFlags(ItemFlag.HIDE_ENCHANTS);
        setGlint(armorStandToggle, Toggle.get(uuid, "armor-stand-arms"));
        addButton(new MenuButton(this, armorStandToggle.build(), 11, (button, clickType) -> {
            boolean newState = !Toggle.get(uuid, "armor-stand-arms");
            Toggle.set(uuid, "armor-stand-arms", newState);
            setGlint(armorStandToggle, newState);
            button.setItem(armorStandToggle.removeLoreLine(3).addLore(getStatusText(newState)).build());
        }));

        // Invisible Item Frames
        ItemBuilder itemFrame = new ItemBuilder(Material.ITEM_FRAME)
                .name("&f&lInvisible Item Frames")
                .lore(
                        "&7Makes every item frame",
                        "&7you place be invisible.",
                        getStatusText(Toggle.get(uuid, "invisible-item-frames"))
                )
                .addItemFlags(ItemFlag.HIDE_ENCHANTS);
        setGlint(itemFrame, Toggle.get(uuid, "invisible-item-frames"));
        addButton(new MenuButton(this, itemFrame.build(), 12, ((button, clickType) -> {
            boolean newState = !Toggle.get(uuid, "invisible-item-frames");
            Toggle.set(uuid, "invisible-item-frames", newState);
            setGlint(itemFrame, newState);
            button.setItem(itemFrame.removeLoreLine(3).addLore(getStatusText(newState)).build());
        })));


        // PvP
        ItemBuilder pvp = new ItemBuilder(Material.IRON_AXE)
                .name("&f&lPvP")
                .lore(
                        "&7Enables your PvP and allows",
                        "&7you to fight with other players.",
                        getStatusText(PvPListener.ENABLED_PLAYERS.contains(uuid))
                )
                .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        setGlint(pvp, PvPListener.ENABLED_PLAYERS.contains(uuid));
        addButton(new MenuButton(this, pvp.build(), 14, (button, clickType) -> {
            boolean newState = !PvPListener.ENABLED_PLAYERS.contains(uuid);
            setGlint(pvp, newState);
            button.setItem(pvp.removeLoreLine(3).addLore(getStatusText(newState)).build());

            if (PvPListener.ENABLED_PLAYERS.contains(uuid)) {
                PvPListener.ENABLED_PLAYERS.remove(uuid);
            }
            else {
                PvPListener.ENABLED_PLAYERS.add(uuid);
            }
        }));

        // Chairs
        PlayerSitData sitData = Chairs.getInstance().getPlayerSitData();
        ItemBuilder chairs = new ItemBuilder(Material.SPRUCE_STAIRS)
                .name("&f&lSitting")
                .lore(
                        "&7Allows you to right-click",
                        "&7on stairs to sit in them.",
                        getStatusText(!Chairs.getInstance().getPlayerSitData().isSittingDisabled(uuid))
                )
                .addItemFlags(ItemFlag.HIDE_ENCHANTS);
        setGlint(chairs, !Chairs.getInstance().getPlayerSitData().isSittingDisabled(uuid));
        addButton(new MenuButton(this, chairs.build(), 15, (button, clickType) -> {
            boolean newState = sitData.isSittingDisabled(uuid);
            setGlint(chairs, newState);
            button.setItem(chairs.removeLoreLine(3).addLore(getStatusText(newState)).build());

            if (sitData.isSittingDisabled(uuid)) {
                sitData.enableSitting(uuid);
            }
            else {
                sitData.disableSitting(uuid);
            }
        }));

        open();
    }

    private void setGlint(ItemBuilder item, boolean state) {
        if (state) {
            item.enchantUnsafe(Enchantment.MENDING, 1);
        } else {
            item.removeEnchant(Enchantment.MENDING);
        }
    }

    private String getStatusText(boolean state) {
        return state ? "&aEnabled" : "&cDisabled";
    }
}
