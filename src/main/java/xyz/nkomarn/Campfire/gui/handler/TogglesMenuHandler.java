package xyz.nkomarn.Campfire.gui.handler;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.nkomarn.Campfire.gui.inventory.TogglesMenu;
import xyz.nkomarn.Kerosene.Kerosene;

public class TogglesMenuHandler implements GuiHandler {
    @Override
    public void handle(Player player, int slot, InventoryClickEvent event) {
        if (slot == 13) {
            final String uuid = player.getUniqueId().toString();
            Kerosene.getToggles().setState(uuid, "tree-feller", !Kerosene.getToggles().getState(uuid, "tree-feller"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
            new TogglesMenu(player);
        }
    }
}
