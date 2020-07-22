package xyz.nkomarn.campfire.listener;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.util.PlayerList;

public class VanishListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVanish(@NotNull PlayerVanishStateChangeEvent event) {
        PlayerList.updateHeader();
    }
}
