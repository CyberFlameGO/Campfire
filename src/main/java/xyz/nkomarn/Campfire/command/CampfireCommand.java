package xyz.nkomarn.Campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.maps.FastMapRenderer;
import xyz.nkomarn.Campfire.util.Ranks;
import xyz.nkomarn.Kerosene.data.LocalStorage;
import xyz.nkomarn.Kerosene.data.PlayerData;
import xyz.nkomarn.Kerosene.util.AdvancementUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class CampfireCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1 || !sender.isOp()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCampfire: &7A package of Firestarter's custom features."));
        } else if (args[0].equalsIgnoreCase("createmap")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6&lUsage: &7/campfire createmap [PNG image direct URL]"));
                return true;
            }

            Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                Player player = (Player) sender;
                BufferedImage image;
                try {
                    image = ImageIO.read(new URL(args[1])); // TODO move this off-thread
                } catch (IOException e) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&c&lError: &7Failed to create the map. Check console for errors."));
                    return;
                }

                MapView mapView = Bukkit.createMap(player.getWorld());
                mapView.setLocked(true);
                mapView.getRenderers().forEach(mapView::removeRenderer);
                mapView.addRenderer(new FastMapRenderer(image));

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, "png", os);
                } catch (IOException e) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&c&lError: &7Failed to create the map. Check console for errors."));
                    return;
                }

                Connection connection = null;

                try {
                    connection = LocalStorage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO maps (map_id, image) VALUES (?, ?)");
                    statement.setInt(1, mapView.getId());
                    statement.setString(2, Base64.getEncoder().encodeToString(os.toByteArray()));
                    statement.execute();
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

                ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
                MapMeta mapMeta = (MapMeta) map.getItemMeta();
                mapMeta.setMapView(mapView);
                map.setItemMeta(mapMeta);
                player.getInventory().addItem(map);
            });
        } else if (args[0].equalsIgnoreCase("setdonor")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6&lUsage: &7Provide a username to mark as donor."));
                return true;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            Connection connection = null;

            try {
                connection = PlayerData.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE `playerdata` SET `donor` = TRUE " +
                        "WHERE `uuid` = ?;");
                statement.setString(1, offlinePlayer.getUniqueId().toString());
                statement.execute();
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

            if (offlinePlayer.isOnline()) {
                final Player player = (Player) offlinePlayer;
                AdvancementUtil.grantAdvancement(player, "spark");
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&a&lSuccess: &7Marked the player as a donor."));
        } else if (args[0].equalsIgnoreCase("updatelist")) {
            Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
                Bukkit.getOnlinePlayers().forEach(Ranks::addToTeam);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&a&lSuccess: &7Updated the ranks in player list."));
            });
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lError: &7Invalid operation specified."));
        }
        return true;
    }
}
