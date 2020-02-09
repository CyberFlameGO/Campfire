package xyz.nkomarn.Campfire.command;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
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
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.maps.CustomMapRenderer;
import xyz.nkomarn.Campfire.util.Ranks;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class CampfireCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCampfire: &7A package of Firestarter's custom features."));
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lPermission: &7Insufficient permissions."));
            return true;
        }

        if (args[0].equalsIgnoreCase("createmap")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6&lUsage: &7/campfire givemap <direct url to .png image>."));
                return true;
            }

            Player player = (Player) sender;

            // Fetch the image
            BufferedImage image;
            try {
                image = ImageIO.read(new URL(args[1]));
            } catch (IOException e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lError: &7Failed to create the map. Check console for errors."));
                return true;
            }

            // Create a new map
            MapView mapView = Bukkit.createMap(player.getWorld());
            mapView.setLocked(true);

            for (MapRenderer renderer : mapView.getRenderers()) {
                mapView.removeRenderer(renderer);
            }
            mapView.addRenderer(new CustomMapRenderer(image));

            // Record map in database (base64 image)
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", os);
            } catch (IOException e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lError: &7Failed to create the map. Check console for errors."));
                return true;
            }

            // Insert record
            Document mapDoc = new Document("id", mapView.getId())
                    .append("image", Base64.getEncoder().encodeToString(os.toByteArray()));
            Campfire.getMaps().sync().insertOne(mapDoc);

            // Give map item
            ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
            MapMeta mapMeta = (MapMeta) map.getItemMeta();
            mapMeta.setMapView(mapView);
            map.setItemMeta(mapMeta);
            player.getInventory().addItem(map);
        }
        else if (args[0].equalsIgnoreCase("setdonor")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6&lUsage: &7Provide a username to mark as donor."));
                return true;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            Bson filter = Filters.eq("_id", player.getUniqueId().toString());
            Bson update = new Document("$set", new Document().append("donor", true));
            UpdateOptions options = new UpdateOptions().upsert(true);
            Campfire.getPlayerData().sync().updateOne(filter, update, options);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&a&lSuccess: &7Marked the player as a donor."));
        }
        else if (args[0].equalsIgnoreCase("updatelist")) {
            Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
                Bukkit.getOnlinePlayers().forEach(Ranks::addToTeam);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&a&lSuccess: &7Updated the ranks in player list."));
            });
        }
        else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lError: &7Invalid operation specified."));
        }
        return true;
    }
}
