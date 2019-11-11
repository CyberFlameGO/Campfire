package xyz.nkomarn.Wildfire.event;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Wildfire.Wildfire;
import xyz.nkomarn.Wildfire.util.Config;

public class VoteEvent implements Listener {

    @EventHandler
    public void onVoteReceived(VotifierEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Wildfire.instance, () -> {

            // Get offline player from vote data
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(e.getVote().getUsername());

            // Increment vote count of the player
            Wildfire.playerData.sync().updateOne(Filters.eq("uuid", offlinePlayer.getUniqueId().toString()),
                    new Document("$inc", new BasicDBObject().append("votes", 1)));

            // Give rewards if the player is online
            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();

                if (player == null) return;

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("vote.title.top")),
                        ChatColor.translateAlternateColorCodes('&', Config.getString("vote.title.bottom")));
                Bukkit.getScheduler().runTask(Wildfire.instance, () -> {
                   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.getString("vote.command")
                           .replace("[player]", player.getName()));
                });
            }

        });
    }

}
