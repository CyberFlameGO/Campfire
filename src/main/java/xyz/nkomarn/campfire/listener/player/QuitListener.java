package xyz.nkomarn.campfire.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.listener.PvPListener;
import xyz.nkomarn.campfire.util.PlayerList;
import xyz.nkomarn.campfire.util.cache.EffectsCache;
import xyz.nkomarn.kerosene.Kerosene;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerList.updateHeader();
        PvPListener.ENABLED_PLAYERS.remove(player.getUniqueId());
        EffectsCache.invalidate(player.getUniqueId());

        Kerosene.getPool().submit(() -> {
            // TODO clear shop log
        });

        /*Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try (Connection connection = PlayerData.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `shop_log` WHERE `uuid` = ?")) {
                    statement.setString(1, player.getUniqueId().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });*/
    }
}
