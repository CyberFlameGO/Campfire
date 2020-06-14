package xyz.nkomarn.Campfire.menu;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.menu.MenuButton;

import java.util.Arrays;

/**
 * The donor perks menu. This menu features things like potion slots,
 * particle effects, and many more intended to reward donors.
 */
public class PerksMenu extends Menu {
    public PerksMenu(Player player) {
        super(player, "Donor Perks", 27);
        fill(Material.WHITE_STAINED_GLASS_PANE);
        fillBorderAlternating(Material.MAGENTA_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS_PANE);

        ItemStack potions = new ItemStack(Material.POTION);
        PotionMeta potionsMeta = (PotionMeta) potions.getItemMeta();
        potionsMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lPotion Effects"));
        potionsMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Configure your " + ChatColor.LIGHT_PURPLE + "permanent",
                ChatColor.LIGHT_PURPLE + "potion effect" + ChatColor.GRAY + " slots."
        ));
        potionsMeta.addEnchant(Enchantment.MENDING, 1, true);
        potionsMeta.setColor(Color.fromRGB(255, 95, 195));
        potions.setItemMeta(potionsMeta);
        addButton(new MenuButton(this, potions, 10, (button, clickType) -> new PotionSlotsMenu(player)));

        ItemStack particles = new ItemStack(Material.PINK_TULIP);
        ItemMeta particlesMeta = particles.getItemMeta();
        particlesMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lParticle Effects"));
        particlesMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Configure your" + ChatColor.AQUA + " particle",
                ChatColor.AQUA + "effects" + ChatColor.GRAY + " and styles."
        ));
        particlesMeta.addEnchant(Enchantment.MENDING, 1, true);
        particles.setItemMeta(particlesMeta);
        addButton(new MenuButton(this, particles, 11, (button, clickType) -> Bukkit.getScheduler()
                .runTask(Campfire.getCampfire(), () -> player.chat("/pp"))));

        open();
    }
}
