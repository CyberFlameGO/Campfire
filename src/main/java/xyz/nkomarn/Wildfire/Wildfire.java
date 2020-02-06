package xyz.nkomarn.Wildfire;

import com.earth2me.essentials.Essentials;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.nkomarn.Kerosene.database.mongo.MongoDatabase;
import xyz.nkomarn.Kerosene.database.mongo.subscribers.BasicSubscriber;
import xyz.nkomarn.Kerosene.database.mongo.subscribers.SyncAsyncCollection;
import xyz.nkomarn.Wildfire.command.*;
import xyz.nkomarn.Wildfire.event.PlayerEvent;
import xyz.nkomarn.Wildfire.event.VoteEvent;
import xyz.nkomarn.Wildfire.task.Exporter;
import xyz.nkomarn.Wildfire.util.Config;
import xyz.nkomarn.Wildfire.util.CustomMapRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;

public class Wildfire extends JavaPlugin {

    public static Wildfire instance;
    public static Essentials essentials;

    public static SyncAsyncCollection<Document> playerData;
    public static SyncAsyncCollection<Document> maps;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Load Essentials dependency
        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");

        // Load database collections
        String databaseName = Config.getString("database.name");
        playerData = MongoDatabase.getSyncAsyncCollection(databaseName, "players");
        maps = MongoDatabase.getSyncAsyncCollection(databaseName, "maps");

        // Register events
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerEvent(), this);
        pluginManager.registerEvents(new VoteEvent(), this);

        // Register commands
        getCommand("rtp").setExecutor(new RTPCommand());
        getCommand("wildfire").setExecutor(new WildfireCommand());
        getCommand("skull").setExecutor(new SkullCommand());

        // Register repeating tasks
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(instance, new Exporter(), 0L, 60 * 20);

        // Load bell recipe
        ItemStack bell = new ItemStack(Material.BELL, 1);
        NamespacedKey key = new NamespacedKey(this, "bell");
        ShapedRecipe recipe = new ShapedRecipe(key, bell);
        recipe.shape(" S ", "NIN");
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('N', Material.GOLD_NUGGET);
        recipe.setIngredient('I', Material.GOLD_INGOT);
        Bukkit.addRecipe(recipe);

        // Load in custom maps
        if (!getConfig().getBoolean("maps")) return;
        maps.async().find().subscribe(new BasicSubscriber<Document>() {
            @Override
            public void onNext(Document document) {
                Bukkit.getScheduler().runTask(Wildfire.instance, () -> {
                    MapView mapView = Bukkit.getMap(document.getInteger("id"));

                    try {
                        for (MapRenderer renderer : mapView.getRenderers()) {
                            mapView.removeRenderer(renderer);
                        }
                    } catch (NullPointerException e) {
                        getLogger().log(Level.WARNING, "Map renders could not be fetched for MapView.");
                        return;
                    }

                    // Fetch the image
                    BufferedImage image = null;
                    ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder()
                            .decode(document.getString("image").getBytes()));
                    try {
                        image = ImageIO.read(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Apply new custom renderer
                    mapView.addRenderer(new CustomMapRenderer(image));
                    getLogger().log(Level.INFO, "Loaded map " + document.getInteger("id") + ".");
                });
            }
        });
    }

    public void onDisable() { }
}
