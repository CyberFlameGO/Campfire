package com.firestartermc.campfire.log;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.firestartermc.kerosene.Kerosene;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.firestartermc.campfire.Campfire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ShopLog {

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private static final String LOG_QUERY = "INSERT INTO shop_log (uuid, type, amount, price) VALUES (?, ?," +
            " ?, ?) ON CONFLICT(uuid, type) DO UPDATE SET amount = amount + ?, price = price + ?;";

    private final Campfire campfire;

    public ShopLog(Campfire campfire) {
        this.campfire = campfire;
    }

    /**
     * Creates the necessary local database table for shop log.
     */
    public void load() {
        Kerosene.getKerosene().getPool().submit(() -> {
            try (Connection connection = campfire.getStorage().getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS shop_log (uuid TEXT NOT NULL, type TEXT NOT " +
                        "NULL, amount INTEGER NOT NULL, price REAL NOT NULL, PRIMARY KEY(uuid, type));").execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Logs a shop transaction when the shop owner is offline.
     *
     * @param shopOwner The offline owner of the shop.
     * @param event     The ChestShop transaction event, which includes transaction info to log.
     */
    public void log(@NotNull OfflinePlayer shopOwner, @NotNull TransactionEvent event) {
        Kerosene.getKerosene().getPool().submit(() -> {
            try (Connection connection = campfire.getStorage().getConnection()) {
                for (ItemStack item : event.getStock()) {
                    try (PreparedStatement statement = connection.prepareStatement(LOG_QUERY)) {
                        int amount = item.getAmount();
                        double price = event.getExactPrice().doubleValue();

                        if (event.getTransactionType().equals(TransactionEvent.TransactionType.SELL)) {
                            amount = -amount;
                            price = -price;
                        }

                        statement.setString(1, shopOwner.getUniqueId().toString());
                        statement.setString(2, item.getType().toString());
                        statement.setInt(3, amount);
                        statement.setDouble(4, price);
                        statement.setInt(5, amount);
                        statement.setDouble(6, price);
                        statement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @NotNull
    public CompletableFuture<Double> getTotalEarnings(@NotNull OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var connection = campfire.getStorage().getConnection();
                var statement = connection.prepareStatement("SELECT SUM(price) FROM shop_log WHERE uuid = ?;");
                statement.setString(1, player.getUniqueId().toString());
                var result = statement.executeQuery();

                try (connection; statement; result) {
                    if (!result.next()) {
                        return 0D;
                    }

                    return result.getDouble(1);
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public void clear(@NotNull OfflinePlayer player) {
        Kerosene.getKerosene().getPool().submit(() -> {
            try (Connection connection = campfire.getStorage().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM shop_log WHERE uuid = ?")) {
                    statement.setString(1, player.getUniqueId().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static final class Builder {

        private final StringBuilder builder;
        private final Player player;
        private boolean modified;

        public Builder(@NotNull Player player) {
            this.player = player;
            this.builder = new StringBuilder(" \n" + ChatColor.GREEN + ChatColor.BOLD.toString() + "SHOP LOG:\n");
        }

        public void append(int amount, String rawName, double price) {
            builder.append(
                    ChatColor.YELLOW + String.valueOf(amount) + "x " +
                    WordUtils.capitalize(rawName.toLowerCase().replace("_", " ")) + " " +
                    ChatColor.GRAY + "($" + FORMAT.format(price) + ")" + "\n"
            );

            modified = true;
        }

        public CompletableFuture<String> build(ShopLog shopLog) {
            if (!modified) {
                builder.append(ChatColor.GRAY + "(No log history available)\n" + ChatColor.RESET + " ");
                return CompletableFuture.completedFuture(builder.toString());
            }

            return shopLog.getTotalEarnings(player).thenApply(earnings -> {
                builder.append(ChatColor.GRAY + "Total: " + ChatColor.AQUA + "$" + FORMAT.format(earnings) + "\n" + ChatColor.RESET + " ");
                return builder.toString();
            });
        }
    }
}
