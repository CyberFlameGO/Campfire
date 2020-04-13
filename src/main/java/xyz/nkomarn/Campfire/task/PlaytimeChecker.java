package xyz.nkomarn.Campfire.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;

import java.util.Set;

public class PlaytimeChecker implements Runnable {
    @Override
    public void run() {
        final ConfigurationSection playtimeSection = Campfire.getCampfire().getConfig()
                .getConfigurationSection("playtime");
        final Set<String> timeKeys = playtimeSection.getKeys(false);

        Bukkit.getOnlinePlayers().forEach(player -> {
            final int playtime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60;

            for (String time : timeKeys) {
                if (playtime >= Integer.parseInt(time) && !player.hasPermission(String.format("group.%s",
                        Config.getString(String.format("playtime.%s.name", time))))) {
                    Config.getList(String.format("playtime.%s.commands", time)).forEach(command -> Bukkit.getScheduler()
                            .callSyncMethod(Campfire.getCampfire(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    command.replace("[player]", player.getName())
                    )));

                    final String rankName = Config.getString(String.format("playtime.%s.name", time));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "&3&lPlaytime: &7You're now a %s!", rankName.substring(0, 1).toUpperCase()
                                    + rankName.substring(1))
                    ));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                }
            }
        });
    }
}
