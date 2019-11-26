package xyz.nkomarn.Wildfire.command;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import xyz.nkomarn.Wildfire.Wildfire;
import xyz.nkomarn.Wildfire.util.CustomMapRenderer;
import xyz.nkomarn.Wildfire.util.Ranks;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class WildfireCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lWildfire &7- Firestarter Administration Utility by TechToolbox."));
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8&l(&c&l!&8&l) &cInsufficient permissions."));
            return true;
        }

        if (args[0].equalsIgnoreCase("givemap")) {

            if (args.length < 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8&l(&c&l!&8&l) &cUsage: /wildfire givemap <URL>."));
                return true;
            }

            Player player = (Player) sender;

            // Fetch the image
            BufferedImage image = null;
            try {
                image = ImageIO.read(new URL(args[1]));
            } catch (IOException e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8&l(&c&l!&8&l) &cFailed to create map."));
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
                        "&8&l(&c&l!&8&l) &cFailed to create map."));
                return true;
            }

            System.out.println("Saving map to db.");

            // Insert record
            Document mapDoc = new Document("id", mapView.getId())
                    .append("image", Base64.getEncoder().encodeToString(os.toByteArray()));
            Wildfire.maps.sync().insertOne(mapDoc);

            // Give map item
            ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
            MapMeta mapMeta = (MapMeta) map.getItemMeta();
            mapMeta.setMapView(mapView);
            map.setItemMeta(mapMeta);
            player.getInventory().addItem(map);
        }
        else if (args[0].equalsIgnoreCase("updatelist")) {
            Bukkit.getScheduler().runTaskAsynchronously(Wildfire.instance, () -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Ranks.addToTeam(player);
                });
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8&l(&a&l!&8&l) &aUpdated ranks in tablist."));
            });
        }
        else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8&l(&c&l!&8&l) &cInvalid operation."));
        }
        return true;
    }

}
