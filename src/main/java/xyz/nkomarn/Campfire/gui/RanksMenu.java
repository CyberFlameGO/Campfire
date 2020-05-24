package xyz.nkomarn.Campfire.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.gui.Gui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A Gui menu displaying all of the obtainable
 * ranks on the server, including playtime ranks.
 */
public class RanksMenu extends Gui {
    public RanksMenu(final Player player) {
        super(player, "Ranks", 27);
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE);

        ItemStack scoutHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5" +
                "taW5lY3JhZnQubmV0L3RleHR1cmUvNjhjOTg2Nzk5YmFmZTcxODRhOGFlMjMzZGI2ZDlhYjhlOWI4NDRmYTlkNGRh" +
                "NjhmYTNkYzk0YWM4ZmU4MjAifX19", "&f&lScout", getLore("scout"));
        getInventory().setItem(11, scoutHead);

        ItemStack camperHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5" +
                "taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ2ZGNjNDc2ODMyMzZhYjU5ZDk2ZWIwYjI3YzM4ZDIzNDFkNDg1MDViOGVh" +
                "NGVhNTdhYWYxNzI3Y2QzYzE4NyJ9fX0=", "&f&lCamper", getLore("camper"));
        getInventory().setItem(12, camperHead);

        ItemStack pioneerHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy" +
                "5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMzZTJmMWI5MTk4MDVlZjdiNGRhYmNkZjJhZTY2YjNjM2E0NGEzMjg1Yjc" +
                "0ZmU5NWI1ODVhYjEwNTFlYTQifX19", "&f&lPioneer", getLore("pioneer"));
        getInventory().setItem(13, pioneerHead);

        ItemStack vanguardHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlc" +
                "y5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZkODVhOTY4MzM0ZmZhODc0ZDYzZTgwNjhjNGQxODM1ZTI5YjhkYTE3Y2" +
                "Y1OGQ4ZGUwYTcxMzM4ZWNiZjYwIn19fQ==", "&f&lVanguard", getLore("vanguard"));
        getInventory().setItem(14, vanguardHead);

        ItemStack sparkHead = createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5" +
                "taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q0NzMwYWQ1MmE5Yjk5Nzk1MGU2MzdlNTk1NDJmNjY2NDFhYTJmY2Q5OWFm" +
                "M2E5Mzc3MzZmMDQyNjQyNjg4In19fQ==", "&f&lSpark", getLore("spark"));
        getInventory().setItem(15, sparkHead);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.8f, 1.0f);
        open();
    }

    /**
     * Create a colored list of lore for a rank based on the config.
     * @param rank The name of the rank in the config.
     * @return A ChatColor translated lore list.
     */
    private List<String> getLore(final String rank) {
        final List<String> lore = new ArrayList<>();
        for (String line : Config.getConfig().getStringList(String.format("perks.ranks.%s", rank))) {
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
