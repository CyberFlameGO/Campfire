package xyz.nkomarn.Campfire.gui.menu;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.nkomarn.Campfire.gui.GuiHolder;
import xyz.nkomarn.Campfire.gui.GuiType;
import xyz.nkomarn.Campfire.util.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RanksMenu {
    public RanksMenu(final Player player) {
        Inventory menu = Bukkit.createInventory(new GuiHolder(GuiType.RANKS, 1), 27, "Ranks");

        ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26)
                .forEach(slot -> menu.setItem(slot, glass));

        ItemStack scoutHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5" +
                "taW5lY3JhZnQubmV0L3RleHR1cmUvNjhjOTg2Nzk5YmFmZTcxODRhOGFlMjMzZGI2ZDlhYjhlOWI4NDRmYTlkNGRh" +
                "NjhmYTNkYzk0YWM4ZmU4MjAifX19", "&f&lScout", getLore("scout"));
        menu.setItem(11, scoutHead);

        ItemStack camperHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5" +
                "taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ2ZGNjNDc2ODMyMzZhYjU5ZDk2ZWIwYjI3YzM4ZDIzNDFkNDg1MDViOGVh" +
                "NGVhNTdhYWYxNzI3Y2QzYzE4NyJ9fX0=", "&f&lCamper", getLore("camper"));
        menu.setItem(12, camperHead);

        ItemStack pioneerHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy" +
                "5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMzZTJmMWI5MTk4MDVlZjdiNGRhYmNkZjJhZTY2YjNjM2E0NGEzMjg1Yjc" +
                "0ZmU5NWI1ODVhYjEwNTFlYTQifX19", "&f&lPioneer", getLore("pioneer"));
        menu.setItem(13, pioneerHead);

        ItemStack vanguardHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlc" +
                "y5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZkODVhOTY4MzM0ZmZhODc0ZDYzZTgwNjhjNGQxODM1ZTI5YjhkYTE3Y2" +
                "Y1OGQ4ZGUwYTcxMzM4ZWNiZjYwIn19fQ==", "&f&lVanguard", getLore("vanguard"));
        menu.setItem(14, vanguardHead);

        ItemStack sparkHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5" +
                "taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q0NzMwYWQ1MmE5Yjk5Nzk1MGU2MzdlNTk1NDJmNjY2NDFhYTJmY2Q5OWFm" +
                "M2E5Mzc3MzZmMDQyNjQyNjg4In19fQ==", "&f&lSpark", getLore("spark"));
        menu.setItem(15, sparkHead);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
        player.openInventory(menu);
    }

    /**
     * Create a colored list of lore for a rank based on the config.
     * @param rank The name of the rank in the config.
     * @return A ChatColor translated lore list.
     */
    private List<String> getLore(final String rank) {
        final List<String> lore = new ArrayList<>();
        for (String line : Config.getConfig().getStringList(String.format("ranks.%s", rank))) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return lore;
    }

    /**
     * Create a player head ItemStack with a custom texture.
     * @param texture The texture to use for the player head.
     * @return ItemStack of a player head with the texture applied.
     */
    private ItemStack createSkull(final String texture, final String displayName, final List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        skullMeta.setLore(lore);

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", texture));

        try {
            final Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }
}
