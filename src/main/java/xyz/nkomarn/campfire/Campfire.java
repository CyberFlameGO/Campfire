package xyz.nkomarn.campfire;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.campfire.command.*;
import xyz.nkomarn.campfire.listener.*;
import xyz.nkomarn.campfire.listener.entity.PickupItemListener;
import xyz.nkomarn.campfire.listener.entity.SpawnListener;
import xyz.nkomarn.campfire.listener.player.*;
import xyz.nkomarn.campfire.listener.shop.TransactionListener;
import xyz.nkomarn.campfire.listener.world.ChunkLoadListener;
import xyz.nkomarn.campfire.log.ShopLog;
import xyz.nkomarn.campfire.map.Maps;
import xyz.nkomarn.campfire.task.*;
import xyz.nkomarn.campfire.util.Copyright;
import xyz.nkomarn.campfire.util.cache.EffectsCache;
import xyz.nkomarn.kerosene.data.db.LocalStorage;

import java.util.Arrays;

public class Campfire extends JavaPlugin {

    private static Campfire CAMPFIRE;
    private static LocalStorage STORAGE;
    private static DataExporter EXPORTER;

    @Override
    public void onEnable() {
        CAMPFIRE = this;
        saveDefaultConfig();

        Arrays.asList(
                new AdvancementCriterionListener(),
                new AccrueClaimBlocksListener(),
                new ChatListener(),
                new ChunkLoadListener(),
                new CommandPreProcessListener(),
                new Copyright(),
                new CustomEnchantmentListener(),
                new InteractEntityListener(),
                new JoinListener(),
                new PickupItemListener(),
                new PvPListener(),
                new QuitListener(),
                new RespawnListener(),
                new SpawnListener(),
                new SpawnerListener(),
                new TransactionListener(),
                new VanishListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        getCommand("campfire").setExecutor(new CampfireCommand());
        getCommand("colorcodes").setExecutor(new ColorCodesCommand());
        getCommand("copyright").setExecutor(new CopyrightCommand());
        // getCommand("findshop").setExecutor(new FindShopCommand());
        getCommand("wild").setExecutor(new WildCommand());
        getCommand("perks").setExecutor(new PerksCommand());
        getCommand("playtime").setExecutor(new PlaytimeCommand());
        getCommand("pvp").setExecutor(new PvPCommand());
        getCommand("ranks").setExecutor(new RanksCommand());
        getCommand("report").setExecutor(new ReportCommand());
        getCommand("toggle").setExecutor(new TogglesCommand());
        getCommand("setslots").setExecutor(new SetSlotsCommand());
        getCommand("shoplog").setExecutor(new ShopLogCommand());
        getCommand("skull").setExecutor(new SkullCommand());

        getServer().getScheduler().runTaskTimer(this, new EffectsTask(), 0L, 200L);
        getServer().getScheduler().runTaskTimer(this, new PhantomTask(getServer()), 0L, 40L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new PlaytimeCheck(getServer()), 0L, 6000L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new PortalTask(getServer()), 0L, 10L);

        STORAGE = new LocalStorage("campfire");
        Maps.loadMaps();
        ShopLog.load();
        Copyright.load();

        for (Player player : getServer().getOnlinePlayers()) { // If hot-loaded, re-cache potion effects
            EffectsCache.cache(player.getUniqueId());
        }

        if (getServer().getPluginManager().isPluginEnabled("PrometheusExporter")) {
            EXPORTER = new DataExporter();
            getServer().getScheduler().runTaskTimerAsynchronously(this, EXPORTER, 0L, 6000L);
        }
    }

    @Override
    public void onDisable() {
        if (EXPORTER != null) {
            EXPORTER.shutdown();
        }
    }

    public static Campfire getCampfire() {
        return CAMPFIRE;
    }

    public static LocalStorage getStorage() {
        return STORAGE;
    }
}
