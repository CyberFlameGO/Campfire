package xyz.nkomarn.Campfire;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Server;
import xyz.nkomarn.Campfire.command.CampfireCommand;
import xyz.nkomarn.Campfire.command.SkullCommand;
import xyz.nkomarn.Campfire.command.WildCommand;
import xyz.nkomarn.Campfire.listener.*;
import xyz.nkomarn.Campfire.maps.Maps;
import xyz.nkomarn.Campfire.util.Metrics;
import xyz.nkomarn.Campfire.util.Recipes;
import xyz.nkomarn.Kerosene.database.FlexibleCollection;
import xyz.nkomarn.Kerosene.database.MongoDatabase;

import java.net.InetSocketAddress;

public class Campfire extends JavaPlugin {
    private static Campfire campfire;
    private static FlexibleCollection<Document> playerData;
    private static FlexibleCollection<Document> maps;
    private Server metricsServer;

    public void onEnable() {
        campfire = this;
        saveDefaultConfig();

        final String database = getConfig().getString("database");
        playerData = MongoDatabase.getFlexibleCollection(database, "players");
        maps = MongoDatabase.getFlexibleCollection(database, "maps");

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EntityPickupItemListener(), this);
        pluginManager.registerEvents(new PlayerInteractEntityListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerRespawnListener(), this);
        pluginManager.registerEvents(new VoteListener(), this);

        getCommand("campfire").setExecutor(new CampfireCommand());
        getCommand("wild").setExecutor(new WildCommand());
        getCommand("skull").setExecutor(new SkullCommand());

        Maps.loadMaps();
        Recipes.loadRecipes();

        getLogger().info("Initializing metrics exporter.");
        InetSocketAddress address = new InetSocketAddress("0.0.0.0",
                getConfig().getInt("metrics.port"));
        metricsServer = new Server(address);
        metricsServer.setHandler(new Metrics());
        try {
            metricsServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        try {
            metricsServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Campfire getCampfire() {
        return campfire;
    }

    public static FlexibleCollection<Document> getPlayerData() {
        return playerData;
    }

    public static FlexibleCollection<Document> getMaps() {
        return maps;
    }
}
