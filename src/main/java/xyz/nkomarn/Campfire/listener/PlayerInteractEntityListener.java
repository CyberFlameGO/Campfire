package xyz.nkomarn.Campfire.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class PlayerInteractEntityListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        Player rightClicked = (Player) event.getRightClicked();

        if (rightClicked.getName().equals("iriscow")) {
            if (event.getPlayer().getItemInHand().getType().equals(Material.BUCKET)) {
                event.getPlayer().getItemInHand().setType(Material.MILK_BUCKET);
            }
        }
    }
}
