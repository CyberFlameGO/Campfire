package xyz.nkomarn.Campfire;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Campfire.command.*;
import xyz.nkomarn.Campfire.listener.*;
import xyz.nkomarn.Campfire.listener.entity.SpawnListener;
import xyz.nkomarn.Campfire.listener.entity.PickupItemListener;
import xyz.nkomarn.Campfire.listener.player.*;
import xyz.nkomarn.Campfire.maps.Maps;
import xyz.nkomarn.Campfire.task.BorderCheck;
import xyz.nkomarn.Campfire.task.EffectsTask;
import xyz.nkomarn.Campfire.task.PlaytimeCheck;

import java.util.Arrays;

public class Campfire extends JavaPlugin {
    private static Campfire campfire;

    public void onEnable() {
        campfire = this;
        saveDefaultConfig();

        Arrays.asList(
                new AdvancementCriterionListener(),
                new BorderListener(),
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
                new VanishListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        getCommand("campfire").setExecutor(new CampfireCommand());
        getCommand("colorcodes").setExecutor(new ColorCodesCommand());
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

        Maps.loadMaps();

        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new PlaytimeCheck(), 0L, 1200L);
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new BorderCheck(), 0L, 10L);
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new EffectsTask(), 0L, 200L);
    }

    /**
     * Fetches an instance of the Campfire plugin.
     * @return Campfire plugin instance.
     */
    public static Campfire getCampfire() {
        return campfire;
    }
}
