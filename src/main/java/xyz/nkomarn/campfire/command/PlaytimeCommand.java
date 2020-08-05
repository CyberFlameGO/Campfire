package xyz.nkomarn.campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.kerosene.Kerosene;
import xyz.nkomarn.kerosene.data.db.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class PlaytimeCommand implements TabExecutor {

    private static final String ERROR_PREFIX = ChatColor.translateAlternateColorCodes('&', "&c&lError: &7");
    private static final DateFormat FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player) {
                sendPlaytime((Player) sender, (Player) sender);
            }
        } else {
            sendPlaytime((Player) sender, Bukkit.getOfflinePlayer(args[0]));
        }

        return true;
    }

    private void sendPlaytime(@NotNull Player sender, @NotNull OfflinePlayer player) {
        Kerosene.getPool().submit(() -> {
            try (Connection connection = PlayerData.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT `joined` FROM `playerdata` WHERE `uuid` = ?;")) {
                    statement.setString(1, player.getUniqueId().toString());

                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            int playtime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60;

                            String message = "&r \n&f&l" + (sender.getUniqueId().equals(player.getUniqueId()) ? "Your" : player.getName() + "'s") +
                                    " Playtime: &a" + getTimeString(playtime) + "\n" +
                                    "&f&lJoin date: &a" + FORMAT.format(result.getLong(1)) + " UTC\n&r ";

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        } else {
                            sender.sendMessage(ERROR_PREFIX + "Couldn't fetch playtime data for that player.");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sender.sendMessage(ERROR_PREFIX + "Couldn't fetch playtime data for that player.");
            }
        });
    }

    private String getTimeString(long time) {
        return (time / 24 / 60) + " days, " + (time / 60 % 24) + " hours, and " + (time % 60) + " minutes";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        return null;
    }
}
