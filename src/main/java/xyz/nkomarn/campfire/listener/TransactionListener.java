package xyz.nkomarn.campfire.listener;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.kerosene.Kerosene;
import xyz.nkomarn.kerosene.data.db.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionListener implements Listener {

    private static final String QUERY = "INSERT INTO `shop_log` (`uuid`, `type`, `amount`, `price`) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `amount` = `amount` + ?, `price` = `price` + ?;";

    @EventHandler(ignoreCancelled = true)
    public void onTransaction(@NotNull TransactionEvent event) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(event.getOwnerAccount().getUuid());

        if (owner.isOnline()) {
            return;
        }

        Kerosene.getPool().submit(() -> {
            try (Connection connection = PlayerData.getConnection()) {
                for (ItemStack item : event.getStock()) {
                    try (PreparedStatement statement = connection.prepareStatement(QUERY)) {
                        int amount = item.getAmount();
                        double price = event.getExactPrice().doubleValue();

                        if (event.getTransactionType().equals(TransactionEvent.TransactionType.SELL)) {
                            amount = -amount;
                            price = -price;
                        }

                        statement.setString(1, owner.getUniqueId().toString());
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
}
