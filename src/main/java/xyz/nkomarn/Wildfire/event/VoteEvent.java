package xyz.nkomarn.Wildfire.event;

import com.vexsoftware.votifier.model.VotifierEvent;
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
    public void onVote(VotifierEvent e) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(e.getVote().getUsername());
        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("vote.title.top")),
                    ChatColor.translateAlternateColorCodes('&', Config.getString("vote.title.bottom")));
            Bukkit.getScheduler().runTask(Wildfire.instance, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.getString("vote.command")
                        .replace("[player]", player.getName()));
            });
        }
    }
}
