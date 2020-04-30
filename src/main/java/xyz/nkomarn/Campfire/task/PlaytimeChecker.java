package xyz.nkomarn.Campfire.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;

import java.util.Set;
import java.util.stream.Collectors;

public class PlaytimeChecker implements Runnable {
    @Override
    public void run() {
        Set<Integer> times = Config.getConfig().getConfigurationSection("playtime").getKeys(false)
                .stream().map(Integer::parseInt).collect(Collectors.toSet());

        Bukkit.getOnlinePlayers().forEach(player -> {
            int playtime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60;
            times.forEach(time -> {
                String rankName = Config.getString(String.format("playtime.%s.name", time));
                if (playtime >= time && !player.hasPermission(String.format("group.%s", rankName))) {
                    Config.getConfig().getStringList(String.format("playtime.%s.commands", time)).forEach(command ->
                            Bukkit.getScheduler().callSyncMethod(Campfire.getCampfire(), () -> Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(), command.replace("[player]", player.getName()
                    ))));

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "&3&lPlaytime: &7You're now a %s! Check /ranks to see perks.",
                            rankName.substring(0, 1).toUpperCase() + rankName.substring(1)
                    )));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                }
            });
        });
    }
}