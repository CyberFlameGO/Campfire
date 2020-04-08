package xyz.nkomarn.Campfire;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Campfire.command.CampfireCommand;
import xyz.nkomarn.Campfire.command.WildCommand;
import xyz.nkomarn.Campfire.listener.*;
import xyz.nkomarn.Campfire.maps.Maps;

public class Campfire extends JavaPlugin {
    private static Campfire campfire;

    public void onEnable() {
        campfire = this;
        saveDefaultConfig();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EntityPickupItemListener(), this);
        pluginManager.registerEvents(new PlayerInteractEntityListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerRespawnListener(), this);
        pluginManager.registerEvents(new PlayerCommandPreProcessListener(), this);
        pluginManager.registerEvents(new AdvancementCriteriaListener(), this);

        getCommand("campfire").setExecutor(new CampfireCommand());
        getCommand("wild").setExecutor(new WildCommand());

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
