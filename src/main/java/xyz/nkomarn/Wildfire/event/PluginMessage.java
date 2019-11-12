package xyz.nkomarn.Wildfire.event;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import xyz.nkomarn.Wildfire.Wildfire;
import xyz.nkomarn.Wildfire.util.Config;

import java.util.concurrent.ForkJoinPool;

public class PluginMessage implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
        if (!channel.equals("firestarter")) return;

        ForkJoinPool.commonPool().submit(() -> {
            final ByteArrayDataInput in = ByteStreams.newDataInput(message);
            final String subChannel = in.readUTF();
            if (!subChannel.equals("vote")) return;

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("vote.title.top")),
                    ChatColor.translateAlternateColorCodes('&', Config.getString("vote.title.bottom")));
            Bukkit.getScheduler().runTask(Wildfire.instance, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.getString("vote.command")
                        .replace("[player]", player.getName()));
            });
        });
    }
}
