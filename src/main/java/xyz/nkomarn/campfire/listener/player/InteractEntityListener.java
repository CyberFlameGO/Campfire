package xyz.nkomarn.campfire.listener.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InteractEntityListener implements Listener {

    @EventHandler
    public void onInteractEntity(@NotNull PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();
            Player clickedPlayer = (Player) event.getRightClicked();

            if (clickedPlayer.getUniqueId().equals(UUID.fromString("9b6edc28-2ca3-4ede-82b6-777116812905"))) {
                if (player.getInventory().getItemInMainHand().getType().equals(Material.BUCKET)) {
                    player.getInventory().getItemInMainHand().setAmount(Math.max(0, player.getInventory().getItemInMainHand().getAmount() - 1));
                    player.getInventory().addItem(new ItemStack(Material.MILK_BUCKET, 1)).values().forEach(item ->
                            player.getWorld().dropItemNaturally(player.getLocation(), item));
                }
            }
        }
    }
}
