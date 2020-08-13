package xyz.nkomarn.campfire.task;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.campfire.util.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A running task to handle the playtime rank rewards system.
 */
public class PlaytimeCheck implements Runnable {

    private final Server server;
    private final HashMap<Integer, String> rewardTimes;

    public PlaytimeCheck(@NotNull Server server) {
        this.server = server;
        this.rewardTimes = new HashMap<>();

        ConfigurationSection section = Config.getConfig().getConfigurationSection("perks.playtime");
        for (String time : section.getKeys(false)) {
            rewardTimes.put(Integer.valueOf(time), section.getString(time + ".name"));
        }
    }

    @Override
    public void run() {
        for (Player player : server.getOnlinePlayers()) {
            int playtime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60;

            for (Map.Entry<Integer, String> rewards : rewardTimes.entrySet()) {
                if (!(playtime >= rewards.getKey() && !player.hasPermission("group." + rewards.getValue()))) {
                    continue;
                }

                runRewardCommands(player, rewards.getKey());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                player.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Playtime: " + ChatColor.WHITE +
                        "You're now a " + (rewards.getValue().substring(0, 1).toUpperCase() +
                        rewards.getValue().substring(1)) + "! Check /ranks to see perks.");
            }
        }
    }

    private void runRewardCommands(@NotNull Player player, int time) {
        for (String command : Config.getConfig().getStringList("perks.playtime." + time + ".commands")) {
            Bukkit.getScheduler().callSyncMethod(Campfire.getCampfire(), () -> Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(), command.replace("[player]", player.getName()))
            );
        }
    }
}