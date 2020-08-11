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
import java.util.concurrent.CompletableFuture;

public class PlaytimeCommand implements TabExecutor {

    private static final String ERROR_PREFIX = ChatColor.translateAlternateColorCodes('&', "&c&lError: &7");
    private static final DateFormat FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    private static final String QUERY = "SELECT `joined` FROM `playerdata` WHERE `uuid` = ?;";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            getPlaytimeMessage(player, player).thenAccept(player::sendMessage);
            return true;
        }

        getPlaytimeMessage(player, Bukkit.getOfflinePlayer(args[0])).thenAccept(player::sendMessage);
        return true;
    }

    private CompletableFuture<String> getPlaytimeMessage(@NotNull OfflinePlayer sender, @NotNull OfflinePlayer player) {
        CompletableFuture<String> future = new CompletableFuture<>();

        Kerosene.getPool().submit(() -> {
            try {
                Connection connection = PlayerData.getConnection();
                PreparedStatement statement = connection.prepareStatement(QUERY);
                statement.setString(1, player.getUniqueId().toString());

                ResultSet result = statement.executeQuery();

                try (connection; statement; result) {
                    if (!result.next()) {
                        future.complete(ERROR_PREFIX + "Couldn't fetch playtime data for that player.");
                        return;
                    }

                    int playtime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60;
                    String message = "&r \n&f&l" + (sender.getUniqueId().equals(player.getUniqueId()) ? "Your" : player.getName() + "'s") +
                            " Playtime: &a" + getTimeString(playtime) + "\n" + "&f&lJoin date: &a" + FORMAT.format(result.getLong(1)) + " UTC\n&r ";
                    future.complete(ChatColor.translateAlternateColorCodes('&', message));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                future.complete(ERROR_PREFIX + "Couldn't fetch playtime data for that player.");
            }
        });

        return future;
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
