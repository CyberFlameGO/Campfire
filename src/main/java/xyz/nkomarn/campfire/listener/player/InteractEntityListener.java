package xyz.nkomarn.campfire.listener.player;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class InteractEntityListener implements Listener {

    @EventHandler
    public void onInteractEntity(@NotNull PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();
            Player clickedPlayer = (Player) event.getRightClicked();

            UUID clickedUUID = clickedPlayer.getUniqueId();
            if (clickedUUID.equals(UUID.fromString("9b6edc28-2ca3-4ede-82b6-777116812905"))) {
                if (player.getInventory().getItemInMainHand().getType().equals(Material.BUCKET)) {
                    player.getInventory().getItemInMainHand().setAmount(Math.max(0, player.getInventory().getItemInMainHand().getAmount() - 1));
                    player.getInventory().addItem(new ItemStack(Material.MILK_BUCKET, 1)).values().forEach(item ->
                            player.getWorld().dropItemNaturally(player.getLocation(), item));
                }
            } else if (clickedUUID.equals(UUID.fromString("dd96e851-b3e8-4b0f-8618-29ec734251c6"))) {
                clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_DOLPHIN_AMBIENT_WATER, 1.0f, 1.0f);
                clickedPlayer.getWorld().spawnParticle(Particle.WATER_SPLASH, clickedPlayer.getLocation(), 3);
            }
        }
    }

    @EventHandler // TODO remove once 1.16 villager crash has been fixed
    public void onInventory(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            if (event.getInventory().getHolder() instanceof Villager) {
                Villager villager = (Villager) event.getInventory().getHolder();

                if (villager.getProfession() == Villager.Profession.CARTOGRAPHER) {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&c&lError: &7Cartographers are currently disabled as trading with them causes server crashes."
                    ));
                    event.setCancelled(true);
                    event.getPlayer().closeInventory();

                    if (event.getPlayer() instanceof Player) {
                        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    }
                }
            }
        }
    }
}
