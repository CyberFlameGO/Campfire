package xyz.nkomarn.campfire.listener.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.util.Config;
import xyz.nkomarn.campfire.util.PlayerList;
import xyz.nkomarn.campfire.util.cache.EffectsCache;
import xyz.nkomarn.kerosene.Kerosene;
import xyz.nkomarn.kerosene.util.webhook.DiscordWebhook;

import java.awt.*;
import java.io.IOException;

public class JoinListener implements Listener {

    private static final String TITLE = ChatColor.translateAlternateColorCodes('&', Config.getString("messages.join.top"));
    private static final String SUBTITLE = ChatColor.translateAlternateColorCodes('&', Config.getString("messages.join.bottom"));
    private static final String FOOTER = ChatColor.translateAlternateColorCodes('&', Config.getString("tablist.footer"));

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerList.updateTeams(player);

        Kerosene.getPool().submit(() -> {
            PlayerList.updateHeader();
            player.setPlayerListFooter(FOOTER);
            player.sendTitle(TITLE, SUBTITLE, 10, 70, 20);
            EffectsCache.cache(player.getUniqueId());

            if (player.hasPlayedBefore()) {
                // TODO Shop log
            } else {
                DiscordWebhook hook = new DiscordWebhook(Config.getString("webhooks.notifications"));
                hook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setDescription(":checkered_flag: " + event.getPlayer().getName() + " joined!")
                        .setColor(Color.WHITE)
                );

                try {
                    hook.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



           /* if (player.hasPlayedBefore()) {
                try (Connection connection = PlayerData.getConnection()) {
                    try (PreparedStatement statement = connection.prepareStatement("SELECT SUM(`price`) FROM " +
                            "`shop_log` WHERE `uuid` = ?;")) {
                        statement.setString(1, player.getUniqueId().toString());
                        try (ResultSet result = statement.executeQuery()) {
                            if (result.next()) {
                                if (result.getDouble(1) != 0) {
                                    String word = result.getDouble(1) > 0 ? "earned" : "lost";
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                                            "&6&lShops: &7You %s &6$%s&7 while you were away- &6/shoplog&7 for details.",
                                            word, NumberFormat.getNumberInstance(Locale.US).format(result.getDouble(1))
                                    )));
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }*/
    }
}
