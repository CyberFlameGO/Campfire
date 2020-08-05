package xyz.nkomarn.campfire;

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
import xyz.nkomarn.campfire.task.EffectsTask;
import xyz.nkomarn.campfire.task.PhantomTask;
import xyz.nkomarn.campfire.task.PortalTask;
import xyz.nkomarn.campfire.task.PlaytimeCheck;
import xyz.nkomarn.campfire.util.Copyright;
import xyz.nkomarn.kerosene.data.db.LocalStorage;

import java.util.Arrays;

public class Campfire extends JavaPlugin {

    private static Campfire CAMPFIRE;
    private static LocalStorage STORAGE;

    public void onEnable() {
        CAMPFIRE = this;
        saveDefaultConfig();

        Arrays.asList(
                new AdvancementCriterionListener(),
                new AccrueClaimBlocksListener(),
                new Copyright(),
                new SpawnerListener(),
                new PickupItemListener(),
                new SpawnListener(),
                new ChatListener(),
                new CommandPreProcessListener(),
                new InteractEntityListener(),
                new JoinListener(),
                new QuitListener(),
                new RespawnListener(),
                new PvPListener(),
                new TransactionListener(),
                new VanishListener(),
                new ChunkLoadListener(),
                new CustomEnchantmentListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        getCommand("campfire").setExecutor(new CampfireCommand());
        getCommand("colorcodes").setExecutor(new ColorCodesCommand());
        getCommand("copyright").setExecutor(new CopyrightCommand());
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

        getServer().getScheduler().runTaskTimerAsynchronously(this, new PlaytimeCheck(), 0L, 6000L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new EffectsTask(), 0L, 200L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new PortalTask(getServer()), 0L, 10L);
        getServer().getScheduler().runTaskTimer(this, new PhantomTask(getServer()), 0L, 40L);

        STORAGE = new LocalStorage("campfire");
        Maps.loadMaps();
        ShopLog.load();
        Copyright.load();

        /*if (getServer().getPluginManager().isPluginEnabled("PrometheusExporter")) {
            getServer().getScheduler().runTaskTimerAsynchronously(this,
                    new DataExporter(), 0L, 6000L);
        }*/
    }

    public static Campfire getCampfire() {
        return CAMPFIRE;
    }

    public static LocalStorage getStorage() {
        return STORAGE;
    }
}
