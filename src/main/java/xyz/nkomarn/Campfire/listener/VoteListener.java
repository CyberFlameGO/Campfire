package xyz.nkomarn.Campfire.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent event) {
        OfflinePlayer playerVoted = Bukkit.getOfflinePlayer(event.getVote().getUsername());

        if (playerVoted.isOnline()) {
            Player player = (Player) playerVoted;
            if (Advancements.isComplete(player, "vote")) return;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:vote",
                    player.getName()));
        }
    }
}
