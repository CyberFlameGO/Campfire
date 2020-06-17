package xyz.nkomarn.Campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.util.StringUtil;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.maps.FastMapRenderer;
import xyz.nkomarn.Campfire.util.PlayerList;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class CampfireCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1 || !sender.isOp()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCampfire: &7A package of Firestarter's custom features."));
        } else if (args[0].equalsIgnoreCase("createmap")) {
            executeCreateMap(sender, args);
        } else if (args[0].equalsIgnoreCase("setdonor")) {
            executeSetDonor(sender, args);
        } else if (args[0].equalsIgnoreCase("updatelist")) {
            executeUpdateList(sender);
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lError: &7Invalid operation specified."));
        }
        return true;
    }

    private void executeCreateMap(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCampfire: &7Use '/campfire createmap [image url]' to create a custom map."));
           return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try {
                BufferedImage image = ImageIO.read(new URL(args[1]));
                Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                    MapView view = Bukkit.createMap(player.getWorld());
                    view.setLocked(true);
                    view.getRenderers().forEach(view::removeRenderer);
                    view.addRenderer(new FastMapRenderer(image));

                    try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", outputStream);

                        try (Connection connection = LocalStorage.getConnection()) {
                            try (PreparedStatement statement = connection
                                    .prepareStatement("INSERT INTO maps (map_id, image) VALUES (?, ?)")) {
                                statement.setInt(1, view.getId());
                                statement.setString(2, Base64.getEncoder().encodeToString(outputStream.toByteArray()));
                                statement.execute();
                            }
                        } finally {
                            ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
                            MapMeta mapMeta = (MapMeta) map.getItemMeta();
                            mapMeta.setMapView(view);
                            map.setItemMeta(mapMeta);
                            player.getInventory().addItem(map);
                        }
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&c&lError: &7Failed to create the map. Check console for errors."));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lError: &7There was an error making the map. Check console for errors."));
            }
        });

    }

    private void executeSetDonor(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lUsage: &7Provide a username to mark as donor."));
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            try (Connection connection = PlayerData.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE `playerdata` SET " +
                        "`donor` = TRUE WHERE `uuid` = ?;")) {
                    statement.setString(1, offlinePlayer.getUniqueId().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if (offlinePlayer.isOnline()) {
            AdvancementUtil.grantAdvancement((Player) offlinePlayer, "spark");
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&a&lSuccess: &7Marked the player as a donor."));
    }

    private void executeUpdateList(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            Bukkit.getOnlinePlayers().forEach(PlayerList::updateTeams);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&a&lSuccess: &7Updated the ranks in player list."));
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) return null;

        if (args.length == 1) {
            final List<String> subCommands = Arrays.asList("createmap", "setdonor", "updatelist");
            Collections.sort(subCommands);
            return subCommands;
        } else if(args.length == 2) {
            if (args[0].equalsIgnoreCase("createmap")) {
                return Collections.singletonList("<image_url>");
            }

            if(args[0].equalsIgnoreCase("setdonor")) {
                return null;
            }
        }
        return Collections.emptyList();
    }
}
