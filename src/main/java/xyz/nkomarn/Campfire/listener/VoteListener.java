package xyz.nkomarn.Campfire.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent event) {
        OfflinePlayer playerVoted = Bukkit.getOfflinePlayer(event.getVote().getUsername());

        if (playerVoted.isOnline()) {
            Player player = (Player) playerVoted;
            // TODO handle the rest of the vote flow
        }
    }
}
