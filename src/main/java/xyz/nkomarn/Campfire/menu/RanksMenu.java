package xyz.nkomarn.Campfire.menu;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.util.item.ItemBuilder;
import xyz.nkomarn.Kerosene.util.item.SkullBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A Gui menu displaying all of the obtainable
 * ranks on the server, including playtime ranks.
 */
public class RanksMenu extends Menu {
    public RanksMenu(final Player player) {
        super(player, "Ranks", 27);
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE);

        ItemStack scoutHead = new SkullBuilder()
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjh" +
                        "jOTg2Nzk5YmFmZTcxODRhOGFlMjMzZGI2ZDlhYjhlOWI4NDRmYTlkNGRhNjhmYTNkYzk0YWM4ZmU4MjAifX19")
                .name("&f&lScout")
                .lore(getLore("scout"))
                .build();
        getInventory().setItem(11, scoutHead);

        ItemStack camperHead = new SkullBuilder()
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ" +
                        "2ZGNjNDc2ODMyMzZhYjU5ZDk2ZWIwYjI3YzM4ZDIzNDFkNDg1MDViOGVhNGVhNTdhYWYxNzI3Y2QzYzE4NyJ9fX0=")
                .name("&f&lCamper")
                .lore(getLore("camper"))
                .build();
        getInventory().setItem(12, camperHead);

        ItemStack pioneerHead = new SkullBuilder()
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM" +
                        "zZTJmMWI5MTk4MDVlZjdiNGRhYmNkZjJhZTY2YjNjM2E0NGEzMjg1Yjc0ZmU5NWI1ODVhYjEwNTFlYTQifX19")
                .name("&f&lPioneer")
                .lore(getLore("pioneer"))
                .build();
        getInventory().setItem(13, pioneerHead);

        ItemStack vanguardHead = new SkullBuilder()
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZ" +
                        "kODVhOTY4MzM0ZmZhODc0ZDYzZTgwNjhjNGQxODM1ZTI5YjhkYTE3Y2Y1OGQ4ZGUwYTcxMzM4ZWNiZjYwIn19fQ==")
                .name("&f&lVanguard")
                .lore(getLore("vanguard"))
                .build();
        getInventory().setItem(14, vanguardHead);

        ItemStack sparkHead = new SkullBuilder()
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q" +
                        "0NzMwYWQ1MmE5Yjk5Nzk1MGU2MzdlNTk1NDJmNjY2NDFhYTJmY2Q5OWFmM2E5Mzc3MzZmMDQyNjQyNjg4In19fQ==")
                .name("&f&lSpark")
                .lore(getLore("spark"))
                .build();
        getInventory().setItem(15, sparkHead);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.8f, 1.0f);
        open();
    }

    /**
     * Create a colored list of lore for a rank based on the config.
     * @param rank The name of the rank in the config.
     * @return A ChatColor translated lore list.
     */
    private List<String> getLore(String rank) {
        return Config.getConfig().getStringList(String.format("perks.ranks.%s", rank));
    }
}
