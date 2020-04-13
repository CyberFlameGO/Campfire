package xyz.nkomarn.Campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private final DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        final Player player = (Player) sender;

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            Connection connection = null;

            try {
                connection = PlayerData.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT `joined` FROM `playerdata` " +
                        "WHERE `uuid` = ?;");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    final int playtime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60;

                    final StringBuilder builder = new StringBuilder();
                    builder.append("&f&m&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    builder.append(String.format("&7Playtime: &a%s\n", intToTimeString(playtime)));
                    builder.append(String.format("&7Join date: &a%s\n", dateFormat.format(result.getLong(1))));
                    builder.append("&f&m&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', builder.toString()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return true;
    }

    private String intToTimeString(final int time) {
        return time / 24 / 60 + " days, " + time / 60 % 24 + " hours, and " + time % 60 + " minutes";
    }
}
