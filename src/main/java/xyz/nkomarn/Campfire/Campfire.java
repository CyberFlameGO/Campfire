package xyz.nkomarn.Campfire;

import net.milkbowl.vault.economy.Economy;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Server;
import xyz.nkomarn.Campfire.command.CampfireCommand;
import xyz.nkomarn.Campfire.command.TogglesCommand;
import xyz.nkomarn.Campfire.command.WildCommand;
import xyz.nkomarn.Campfire.listener.*;
import xyz.nkomarn.Campfire.listener.auction.AuctionBuyListener;
import xyz.nkomarn.Campfire.listener.auction.AuctionListListener;
import xyz.nkomarn.Campfire.listener.chestshop.ShopCreatedListener;
import xyz.nkomarn.Campfire.listener.chestshop.TransactionListener;
import xyz.nkomarn.Campfire.listener.claim.AccrueClaimBlocksListener;
import xyz.nkomarn.Campfire.listener.claim.ClaimCreatedListener;
import xyz.nkomarn.Campfire.listener.crate.PlayerPrizeListener;
import xyz.nkomarn.Campfire.listener.jobs.JobsJoinListener;
import xyz.nkomarn.Campfire.listener.pets.PetCreatedListener;
import xyz.nkomarn.Campfire.listener.tree.TreeFallListener;
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
    private static Economy economy = null;
    private Server metricsServer;

    public void onEnable() {
        campfire = this;
        saveDefaultConfig();
        if (!initializeEconomy()) {
            return;
        }

        final String database = getConfig().getString("database");
        playerData = MongoDatabase.getFlexibleCollection(database, "players");
        maps = MongoDatabase.getFlexibleCollection(database, "maps");

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EntityPickupItemListener(), this);
        pluginManager.registerEvents(new PlayerInteractEntityListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerRespawnListener(), this);
        pluginManager.registerEvents(new PlayerInventoryClickListener(), this);
        pluginManager.registerEvents(new PlayerCommandPreProcessListener(), this);
        pluginManager.registerEvents(new VoteListener(), this);
        pluginManager.registerEvents(new ShopCreatedListener(), this);
        pluginManager.registerEvents(new TransactionListener(), this);
        pluginManager.registerEvents(new AuctionBuyListener(), this);
        pluginManager.registerEvents(new AuctionListListener(), this);
        pluginManager.registerEvents(new PlayerPrizeListener(), this);
        pluginManager.registerEvents(new AccrueClaimBlocksListener(), this);
        pluginManager.registerEvents(new ClaimCreatedListener(), this);
        pluginManager.registerEvents(new TreeFallListener(), this);
        pluginManager.registerEvents(new JobsJoinListener(), this);
        pluginManager.registerEvents(new PetCreatedListener(), this);
        //pluginManager.registerEvents(new PetActiveSkillListener(), this);
        //pluginManager.registerEvents(new TrustChangedListener(), this);

        getCommand("campfire").setExecutor(new CampfireCommand());
        getCommand("wild").setExecutor(new WildCommand());
        getCommand("toggle").setExecutor(new TogglesCommand());

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

    private boolean initializeEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Phase requires Vault to operate.");
            getServer().getPluginManager().disablePlugin(this);
        }
        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (provider == null) return false;
        economy = provider.getProvider();
        return true;
    }

    public static Campfire getCampfire() {
        return campfire;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static FlexibleCollection<Document> getPlayerData() {
        return playerData;
    }

    public static FlexibleCollection<Document> getMaps() {
        return maps;
    }
}
