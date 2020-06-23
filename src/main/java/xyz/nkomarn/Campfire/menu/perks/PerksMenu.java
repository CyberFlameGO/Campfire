package xyz.nkomarn.Campfire.menu.perks;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.menu.MenuButton;
import xyz.nkomarn.Kerosene.util.ToggleUtil;
import xyz.nkomarn.Kerosene.util.item.ItemBuilder;
import xyz.nkomarn.Kerosene.util.item.PotionBuilder;
import xyz.nkomarn.Kerosene.util.item.SkullBuilder;

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
                .lore("&7Configure your &dpermanent", "&dpotion effect &7slots.")
                .enchantUnsafe(Enchantment.MENDING, 1)
                .color(255, 95, 195)
                .build();
        addButton(new MenuButton(this, potions, 10, (button, clickType) -> new PotionSlotsMenu(player)));

        // Particles
        ItemStack particles = new ItemBuilder(Material.PINK_TULIP)
                .name("&f&lParticle Effects")
                .lore("&7Configure your &bparticle", "&beffects &7and styles.")
                .enchantUnsafe(Enchantment.MENDING, 1)
                .build();
        addButton(new MenuButton(this, particles, 11, (button, clickType) -> {
            Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> player.chat("/pp"));
        }));

        // Claim Block Boost
        ItemBuilder claimBlocks = new ItemBuilder(Material.GOLDEN_SHOVEL)
                .name(String.format("&f&lClaim Boost (%s&f&l)", ToggleUtil
                        .get(player.getUniqueId(), "claim-boost") ? "&c&lOFF" : "&a&lON"))
                .lore("&eDoubles &7the amount of &eclaim", "&eblocks &7you get per hour.")
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if (!player.hasPermission("campfire.perks.claim-boost")) claimBlocks.addLore("&cComes with Ember rank.");

        addButton(new MenuButton(this, claimBlocks.build(), 15, ((button, clickType) -> {
            if (player.hasPermission("campfire.perks.claim-boost")) {
                UUID uuid = player.getUniqueId();
                ToggleUtil.set(uuid, "claim-boost", !ToggleUtil.get(uuid, "claim-boost"));
                button.setItem(claimBlocks.name(String.format("&f&lClaim Boost (%s&f&l)", ToggleUtil
                        .get(player.getUniqueId(), "claim-boost") ? "&c&lOFF" : "&a&lON")).build());
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        })));


        // Skull
        SkullBuilder skull = new SkullBuilder()
                .name("&f&lSkulls")
                .lore("&7Obtain the heads of players,", "&7even if they're offline.")
                .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTd" +
                        "hYjVkZDNhMDM5NTliMTQ3ZWMwNzg4MmRiMjFmY2UzNTI4NzAzMjRiNTliYTk1ZTdlYTJmMzlmMzViOTIifX19");
        if (!player.hasPermission("campfire.perks.skull")) skull.addLore("&cComes with Pyre rank.");

        addButton(new MenuButton(this, skull.build(), 16, (button, clickType) -> {
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
