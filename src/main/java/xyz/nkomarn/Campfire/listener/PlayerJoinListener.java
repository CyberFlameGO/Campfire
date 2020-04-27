package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.Ranks;
import xyz.nkomarn.Campfire.util.Webhooks;

import java.awt.*;
import java.io.IOException;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // Greet the player with a title
        player.sendTitle(ChatColor.translateAlternateColorCodes('&',
                Config.getString("messages.join.top")), ChatColor.translateAlternateColorCodes('&',
                    Config.getString("messages.join.bottom"))
        );

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.setPlayerListHeader(ChatColor
                    .translateAlternateColorCodes('&', Config.getString("tablist.header")
                            .replace("[online]", String.valueOf(Bukkit.getOnlinePlayers().size() - Ranks.getVanishedPlayers())))
            ));

            player.setPlayerListFooter(ChatColor.translateAlternateColorCodes('&',
                    Config.getString("tablist.footer")));

            if (!player.hasPlayedBefore()) {
                final Webhooks hook = new Webhooks(Config.getString("webhooks.notifications"));
                hook.addEmbed(new Webhooks.EmbedObject()
                        .setDescription(":checkered_flag: " + event.getPlayer().getName() + " joined!")
                        .setColor(Color.WHITE));
                try {
                    hook.execute();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Update player's playerlist team
        Ranks.addToTeam(player);
    }
}
