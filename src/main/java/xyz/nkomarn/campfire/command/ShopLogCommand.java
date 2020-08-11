package xyz.nkomarn.campfire.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.campfire.log.ShopLog;
import xyz.nkomarn.kerosene.Kerosene;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class ShopLogCommand implements TabExecutor {

    private static final String SQL = "SELECT type, amount, price FROM shop_log WHERE uuid = ?";


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        Kerosene.getPool().submit(() -> {
            try {
                final PreparedStatement statement = Campfire.getStorage().getConnection().prepareStatement(SQL);
                statement.setString(1, player.getUniqueId().toString());

                ResultSet result = statement.executeQuery();

                try (statement; result) {
                    ShopLog.Builder builder = new ShopLog.Builder(player);

                    while (result.next()) {
                        builder.append(result.getInt(2), result.getString(1), result.getDouble(3));
                    }

                    builder.build().thenAccept(player::sendMessage);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return Collections.emptyList();
    }
}
