package xyz.nkomarn.Campfire.gui.handler;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.gui.menu.TogglesMenu;
import xyz.nkomarn.Kerosene.util.ToggleUtil;

import java.util.UUID;

public class TogglesMenuHandler implements GuiHandler {
    @Override
    public void handle(Player player, int slot, InventoryClickEvent event) {
        if (slot == 13) {
            Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () ->  {
                final UUID uuid = player.getUniqueId();
                ToggleUtil.setToggleState(uuid, "armor-stand-arms",
                        !ToggleUtil.getToggleState(uuid, "armor-stand-arms"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> new TogglesMenu(player));
            });
        }
    }
}
