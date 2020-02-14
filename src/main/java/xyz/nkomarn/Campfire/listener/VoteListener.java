package xyz.nkomarn.Campfire.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent event) {
        OfflinePlayer playerVoted = Bukkit.getOfflinePlayer(event.getVote().getUsername());

        if (playerVoted.isOnline()) {
            Player player = (Player) playerVoted;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("vote.title.top")),
                    ChatColor.translateAlternateColorCodes('&', Config.getString("vote.title.bottom")));
            Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    Config.getString("vote.command").replace("[player]", player.getName())));
        }
    }
}
