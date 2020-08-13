package xyz.nkomarn.campfire.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.command.WildCommand;
import xyz.nkomarn.campfire.listener.PvPListener;
import xyz.nkomarn.campfire.log.ShopLog;
import xyz.nkomarn.campfire.util.PlayerList;
import xyz.nkomarn.campfire.util.cache.EffectsCache;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerList.updateHeader();
        PvPListener.ENABLED_PLAYERS.remove(player.getUniqueId());
        WildCommand.COOLDOWNS.remove(player.getUniqueId());
        EffectsCache.invalidate(player.getUniqueId());
        ShopLog.clear(event.getPlayer());
    }
}
