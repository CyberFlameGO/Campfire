package xyz.nkomarn.Campfire.command;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.text.NumberFormat;
import java.util.Locale;

public class ShopLogCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
                try (Connection connection = PlayerData.getConnection()) {
                    try (PreparedStatement statement =  connection.prepareStatement("SELECT `type`, `amount`, `price` " +
                            "FROM `shop_log` WHERE `uuid` = ?")) {
                        statement.setString(1, player.getUniqueId().toString());
                        try (ResultSet result = statement.executeQuery()) {
                            StringBuilder log = new StringBuilder("&f&m&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
                                    + "&6&lSHOP LOG:\n");
                            while (result.next()) {
                                log.append(String.format("&6%sx &7%s (&6$%s&7)\n",
                                        result.getInt(2),
                                        WordUtils.capitalize(result.getString(1).toLowerCase().replace("_", " ")),
                                        NumberFormat.getNumberInstance(Locale.US).format(result.getDouble(3))
                                ));
                            }
                            log.append("&f&m&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', log.toString()));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        return true;
    }
}
