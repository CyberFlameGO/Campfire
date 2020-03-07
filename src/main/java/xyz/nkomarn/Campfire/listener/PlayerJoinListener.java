package xyz.nkomarn.Campfire.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.Ranks;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Greet the player with a title
        player.sendTitle(ChatColor.translateAlternateColorCodes('&',
                Config.getString("messages.join.top")), ChatColor.translateAlternateColorCodes('&',
                    Config.getString("messages.join.bottom"))
        );

        // Update player count in player list
        new BukkitRunnable() {
            @Override
            public void run() {
                int players = Bukkit.getOnlinePlayers().size() - Ranks.getVanishedPlayers();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.setPlayerListHeader(ChatColor.translateAlternateColorCodes('&',
                            Config.getString("tablist.header").replace("[online]", String.valueOf(players)))
                    );
                }

                // Set the player list footer
                player.setPlayerListFooter(ChatColor.translateAlternateColorCodes('&',
                        Config.getString("tablist.footer")));
            }
        }.runTaskAsynchronously(Campfire.getCampfire());

        // Update player's player list team
        new BukkitRunnable() {
            @Override
            public void run() {
                Ranks.addToTeam(player);
            }
        }.runTask(Campfire.getCampfire());
    }
}
