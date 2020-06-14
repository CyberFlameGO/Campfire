package xyz.nkomarn.Campfire.listener;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.PlayerList;

public class VanishListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onVanish(PlayerVanishStateChangeEvent event) {
        PlayerList.updateHeader();
    }
}
