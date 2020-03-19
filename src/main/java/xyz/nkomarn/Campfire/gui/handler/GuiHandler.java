package xyz.nkomarn.Campfire.gui.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface GuiHandler {
    void handle(final Player player, final int slot, final InventoryClickEvent event);
}
