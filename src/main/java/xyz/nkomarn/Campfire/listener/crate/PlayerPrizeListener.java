package xyz.nkomarn.Campfire.listener.crate;

import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class PlayerPrizeListener implements Listener {
    @EventHandler
    public void onPlayerPrize(PlayerPrizeEvent event) {
        if (Advancements.isComplete(event.getPlayer(), "crates")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:crates",
                event.getPlayer().getName()));
    }
}
