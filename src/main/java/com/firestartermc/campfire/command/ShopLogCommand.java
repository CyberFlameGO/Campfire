package com.firestartermc.campfire.command;

import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.util.ConcurrentUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.firestartermc.campfire.Campfire;
import com.firestartermc.campfire.log.ShopLog;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class ShopLogCommand implements TabExecutor {

    private static final String SQL = "SELECT type, amount, price FROM shop_log WHERE uuid = ?";

    private Campfire campfire;

    public ShopLogCommand(Campfire campfire) {
        this.campfire = campfire;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        ConcurrentUtils.callAsync(() -> {
            Connection connection = campfire.getStorage().getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL);
            statement.setString(1, player.getUniqueId().toString());

            ResultSet result = statement.executeQuery();

            try (connection; statement; result) {
                ShopLog.Builder builder = new ShopLog.Builder(player);

                while (result.next()) {
                    builder.append(result.getInt(2), result.getString(1), result.getDouble(3));
                }

                builder.build(campfire.getShopLog()).thenAccept(player::sendMessage);
            }

            return null;
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return Collections.emptyList();
    }
}
