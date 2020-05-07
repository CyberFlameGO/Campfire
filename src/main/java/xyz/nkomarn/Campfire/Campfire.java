package xyz.nkomarn.Campfire;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Campfire.command.*;
import xyz.nkomarn.Campfire.listener.*;
import xyz.nkomarn.Campfire.maps.Maps;
import xyz.nkomarn.Campfire.task.EffectsTask;
import xyz.nkomarn.Campfire.task.PlaytimeChecker;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.data.LocalStorage;
import xyz.nkomarn.Kerosene.util.VanishUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Campfire extends JavaPlugin {
    private static Campfire campfire;

    public void onEnable() {
        campfire = this;
        saveDefaultConfig();

        // TODO reenable createTables();
        Maps.loadMaps();

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
        //pluginManager.registerEvents(new VanishListener(), this);

        getCommand("campfire").setExecutor(new CampfireCommand());
        getCommand("colorcodes").setExecutor(new ColorCodesCommand());
        getCommand("wild").setExecutor(new WildCommand());
        //getCommand("perks").setExecutor(new PerksCommand());
        getCommand("playtime").setExecutor(new PlaytimeCommand());
        getCommand("pvp").setExecutor(new PvPCommand());
        getCommand("ranks").setExecutor(new RanksCommand());
        getCommand("toggle").setExecutor(new TogglesCommand());
        getCommand("setslots").setExecutor(new SetSlotsCommand());
        //getCommand("vanish").setExecutor(new VanishCommand());

        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new PlaytimeChecker(), 0L, 1200L);
        /*getServer().getScheduler().runTaskTimerAsynchronously(this,
                new EffectsTask(), 0L, 200L);*/
    }

    /**
     * Fetches an instance of the Campfire plugin.
     * @return Campfire plugin instance.
     */
    public static Campfire getCampfire() {
        return campfire;
    }

    /**
     * Updates the header with the amount of currently online players.
     */
    public static void updateTablistHeader() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(getCampfire(), () -> Bukkit.getOnlinePlayers()
                .forEach(player -> player.setPlayerListHeader(ChatColor.translateAlternateColorCodes('&',
                        String.format(Config.getString("tablist.header"), Bukkit.getOnlinePlayers().size()
                                - VanishUtil.getOnlineVanishedPlayers().size()))
        )), 2L);
    }

    /**
     * Creates the database tables used by Campfire's features.
     */
    private void createTables() {
        try (Connection connection = LocalStorage.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS potions" +
                    "(uuid TEXT PRIMARY KEY NOT NULL, slot1 TEXT NOT NULL, slot2 TEXT NOT NULL, slot3 TEXT NOT NULL)")) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
