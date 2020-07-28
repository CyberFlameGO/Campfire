package xyz.nkomarn.campfire.log;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.kerosene.Kerosene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class ShopLog {

    private static final String LOG_QUERY = "INSERT INTO shop_log (uuid, type, amount, price) VALUES (?, ?," +
            " ?, ?) ON CONFLICT(uuid, type) DO UPDATE SET amount = amount + ?, price = price + ?;";

    /**
     * Creates the necessary local database table for shop log.
     */
    public static void load() {
        Kerosene.getPool().submit(() -> {
            try (Connection connection = Campfire.getStorage().getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS shop_log (uuid TEXT NOT NULL, type TEXT NOT " +
                        "NULL, amount INTEGER NOT NULL, price REAL NOT NULL, PRIMARY KEY(uuid, type));").execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Logs a shop transaction when the shop owner is offline.
     * @param shopOwner The offline owner of the shop.
     * @param event The ChestShop transaction event, which includes transaction info to log.
     */
    public static void log(@NotNull OfflinePlayer shopOwner, @NotNull TransactionEvent event) {
        Kerosene.getPool().submit(() -> {
            try (Connection connection = Campfire.getStorage().getConnection()) {
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

    public static CompletableFuture<Double> getTotalEarnings(@NotNull OfflinePlayer player) {
        CompletableFuture<Double> future = new CompletableFuture<>();
        Kerosene.getPool().submit(() -> {
           try (Connection connection = Campfire.getStorage().getConnection()) {
               try (PreparedStatement statement = connection.prepareStatement("SELECT SUM(price) FROM shop_log WHERE uuid = ?;")) {
                   statement.setString(1, player.getUniqueId().toString());

                   try (ResultSet result = statement.executeQuery()) {
                       if (result.next()) {
                           future.complete(result.getDouble(1));
                       }
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        });
        return future;
    }

    public static void clear(@NotNull OfflinePlayer player) {
        Kerosene.getPool().submit(() -> {
            try (Connection connection = Campfire.getStorage().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM shop_log WHERE uuid = ?")) {
                    statement.setString(1, player.getUniqueId().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
