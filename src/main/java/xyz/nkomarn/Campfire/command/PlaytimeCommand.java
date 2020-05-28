package xyz.nkomarn.Campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.data.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PlaytimeCommand implements CommandExecutor {
    private final DateFormat FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player) {
                sendPlaytime(sender, (Player) sender);
            }
        } else {
            sendPlaytime(sender, Bukkit.getOfflinePlayer(args[0]));
        }
        return true;
    }

    private void sendPlaytime(CommandSender sender, OfflinePlayer player) {
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try (Connection connection = PlayerData.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT `joined` FROM `playerdata` " +
                        "WHERE `uuid` = ?;")) {
                    statement.setString(1, player.getUniqueId().toString());
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            int playtime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60;
                            String message = "&r \n" +
                                    String.format("&f&lPlaytime: &a%s\n", intToTimeString(playtime)) +
                                    String.format("&f&lJoin date: &a%s UTC\n", FORMAT.format(result.getLong(1))) +
                                    "&r ";
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&c&lError: &7Couldn't fetch playtime data for that player."
                            ));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private String intToTimeString(final int time) {
        return (time / 24 / 60) + " days, " + (time / 60 % 24) + " hours, and " + (time % 60) + " minutes";
    }
}
