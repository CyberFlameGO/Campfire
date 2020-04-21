package xyz.nkomarn.Campfire.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import xyz.nkomarn.Campfire.gui.GuiHolder;
import xyz.nkomarn.Campfire.gui.GuiType;
import xyz.nkomarn.Campfire.gui.handler.GuiHandler;
import xyz.nkomarn.Campfire.gui.handler.TogglesMenuHandler;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null || !(event.getInventory().getHolder() instanceof GuiHolder)) return;
        if (!(event.getRawSlot() < event.getInventory().getSize())) return; // TODO ignore number keys selection

        final int slot = event.getSlot();
        final InventoryView view = event.getView();

        if (slot < 0) return;
        if (slot == view.convertSlot(slot)) {
            event.setCancelled(true);
            final GuiHolder holder = (GuiHolder) event.getInventory().getHolder();
            final GuiType type = holder.getType();
            final Player player = (Player) event.getWhoClicked();

            GuiHandler handler = null;
            if (type == GuiType.RANKS) return;
            else if (type == GuiType.TOGGLES) handler = new TogglesMenuHandler();

            if (handler != null) {
                handler.handle(player, slot, event);
                player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
            }
        }
    }
}
