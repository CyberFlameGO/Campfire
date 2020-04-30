package xyz.nkomarn.Campfire;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Campfire.command.*;
import xyz.nkomarn.Campfire.listener.*;
import xyz.nkomarn.Campfire.maps.Maps;
import xyz.nkomarn.Campfire.task.PlaytimeChecker;

public class Campfire extends JavaPlugin {
    private static Campfire campfire;

    public void onEnable() {
        campfire = this;
        saveDefaultConfig();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EntityPickupItemListener(), this);
        pluginManager.registerEvents(new PlayerChatListener(), this);
        pluginManager.registerEvents(new PlayerInteractEntityListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerRespawnListener(), this);
        pluginManager.registerEvents(new PlayerCommandPreProcessListener(), this);
        pluginManager.registerEvents(new AdvancementCriteriaListener(), this);
        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new EntitySpawnListener(), this);
        pluginManager.registerEvents(new PvPListener(), this);
        pluginManager.registerEvents(new VanishListener(), this);

        getCommand("campfire").setExecutor(new CampfireCommand());
        getCommand("wild").setExecutor(new WildCommand());
        getCommand("playtime").setExecutor(new PlaytimeCommand());
        getCommand("pvp").setExecutor(new PvPCommand());
        getCommand("ranks").setExecutor(new RanksCommand());
        getCommand("toggle").setExecutor(new TogglesCommand());
        getCommand("setslots").setExecutor(new SetSlotsCommand());
        getCommand("vanish").setExecutor(new VanishCommand());

        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new PlaytimeChecker(), 0L, 1200L);

        Maps.loadMaps();
    }

    /**
     * Fetches an instance of the Campfire plugin.
     * @return Campfire plugin instance.
     */
    public static Campfire getCampfire() {
        return campfire;
    }
}
