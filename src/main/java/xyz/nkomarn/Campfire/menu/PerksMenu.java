package xyz.nkomarn.Campfire.menu;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.menu.MenuButton;
import xyz.nkomarn.Kerosene.util.item.ItemBuilder;

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
        ItemStack potions = new ItemBuilder(Material.POTION)
                .name(ChatColor.translateAlternateColorCodes('&', "&f&lPotion Effects"))
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
                .name(ChatColor.translateAlternateColorCodes('&', "&f&lParticle Effects"))
                .lore(
                        ChatColor.GRAY + "Configure your" + ChatColor.AQUA + " particle",
                        ChatColor.AQUA + "effects" + ChatColor.GRAY + " and styles."
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .build();
        addButton(new MenuButton(this, particles, 11, (button, clickType) -> {
            Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> player.chat("/pp"));
        }));

        open();
    }
}
