package xyz.nkomarn.Campfire.menu.perks;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.menu.MenuButton;
import xyz.nkomarn.Kerosene.util.ToggleUtil;
import xyz.nkomarn.Kerosene.util.item.ItemBuilder;
import xyz.nkomarn.Kerosene.util.item.PotionBuilder;
import xyz.nkomarn.Kerosene.util.item.SkullBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The donor perks menu. This menu features things like potion slots,
 * particle effects, and many more intended to reward donors.
 */
public class PerksMenu extends Menu {
    public PerksMenu(Player player) {
        super(player, "Donor Perks", 27);
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.MAGENTA_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS_PANE);

        // Potions
        ItemStack potions = new PotionBuilder()
                .name("&f&lPotion Effects")
                .lore(
                        "&7Configure your &dpermanent",
                        "&dpotion effect &7slots."
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .color(255, 95, 195)
                .build();
        addButton(new MenuButton(this, potions, 10, (button, clickType) -> new PotionSlotsMenu(player)));

        // Particles
        ItemStack particles = new ItemBuilder(Material.PINK_TULIP)
                .name("&f&lParticle Effects")
                .lore(
                        "&7Configure your &bparticle",
                        "&beffects &7and styles."
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .build();
        addButton(new MenuButton(this, particles, 11, (button, clickType) -> {
            Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> player.chat("/pp"));
        }));

        // Claim Block Boost TODO comes with xxx rank (possibly make a method for that since its also used below)
        ItemStack claimBlocks = new ItemBuilder(Material.GOLDEN_SHOVEL)
                .name(String.format("&f&lClaim Boost (%s&f&l)", ToggleUtil
                        .get(player.getUniqueId(), "claim-boost") ? "&c&lOFF" : "&a&lON"))
                .lore(
                        "&eDoubles &7the amount of &eclaim", // TODO also show status of it in here
                        "&eblocks &7you get per hour."
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .build();
        addButton(new MenuButton(this, claimBlocks, 15, ((button, clickType) -> {
            UUID uuid = player.getUniqueId();
            ToggleUtil.set(uuid, "claim-boost", !ToggleUtil.get(uuid, "claim-boost"));
            button.setItem(new ItemBuilder(claimBlocks).name(String.format("&f&lClaim Boost (%s&f&l)", ToggleUtil
                    .get(player.getUniqueId(), "claim-boost") ? "&c&lOFF" : "&a&lON")).build());
        })));


        // Skull
        List<String> skullLore = new ArrayList<>(Arrays.asList(
                "&7Obtain the heads of players,",
                "&7even if they're offline."
        ));
        if (!player.hasPermission("campfire.perks.skull")) {
            skullLore.add("&dComes with xxx rank.");
        }


        ItemStack skull = new SkullBuilder()
                .name("&f&lSkulls")
                .lore(skullLore)
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTd" +
                        "hYjVkZDNhMDM5NTliMTQ3ZWMwNzg4MmRiMjFmY2UzNTI4NzAzMjRiNTliYTk1ZTdlYTJmMzlmMzViOTIifX19")
                .build();
        addButton(new MenuButton(this, skull, 16, (button, clickType) -> {
            if (player.hasPermission("campfire.perks.skull")) {
                close();
                Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> player.chat("/skull"));
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }));

        open();
    }
}
