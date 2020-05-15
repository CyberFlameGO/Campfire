package xyz.nkomarn.Campfire.listener;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.data.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionListener implements Listener {
    private final String query = "INSERT INTO `shop_log` (`uuid`, `type`, `amount`, `price`) VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE `amount` = `amount` + ?, `price` = `price` + ?;";

    @EventHandler(ignoreCancelled = true)
    public void onTransaction(TransactionEvent event) {
        OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(event.getOwnerAccount().getUuid());
        if (!shopOwner.isOnline()) {
            Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
                try (Connection connection = PlayerData.getConnection()) {
                    for (ItemStack item : event.getStock()) {
                        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
    }
}
