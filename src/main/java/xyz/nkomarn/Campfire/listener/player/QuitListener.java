package xyz.nkomarn.Campfire.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.listener.PvPListener;
import xyz.nkomarn.Campfire.util.PlayerList;
import xyz.nkomarn.Campfire.util.cache.EffectsCache;
import xyz.nkomarn.Kerosene.data.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PvPListener.ENABLED_PLAYERS.remove(player.getUniqueId());
        EffectsCache.invalidate(player.getUniqueId());

        PlayerList.updateHeader();
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try (Connection connection = PlayerData.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `shop_log` WHERE `uuid` = ?")) {
                    statement.setString(1, player.getUniqueId().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
