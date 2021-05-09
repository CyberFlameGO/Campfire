package com.firestartermc.campfire;

import com.firestartermc.campfire.command.*;
import com.firestartermc.campfire.listener.*;
import com.firestartermc.campfire.listener.player.*;
import com.firestartermc.campfire.listener.shop.*;
import com.firestartermc.campfire.log.ShopLog;
import com.firestartermc.campfire.task.DataExporter;
import com.firestartermc.campfire.util.TextFilter;
import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.data.db.LocalStorage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Campfire extends JavaPlugin {

    private LocalStorage storage;
    private DataExporter exporter;
    private ShopLog shopLog;
    private TextFilter textFilter;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        storage = Kerosene.getKerosene().getLocalStorage("campfire");
        exporter = new DataExporter();
        shopLog = new ShopLog(this);
        textFilter = new TextFilter(this);

        Arrays.asList(
                new AdvancementCriterionListener(),
                new ChatListener(this),
                new CommandPreProcessListener(),
                new JoinListener(this),
                new QuitListener(this),
                new RespawnListener(this),
                new TransactionListener(this),

                // TODO
                new EditListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        getCommand("loop").setExecutor(new LoopCommand(this));
        getCommand("safeshutdown").setExecutor(new SafeShutdownCommand(this));
        getCommand("setslots").setExecutor(new SetSlotsCommand());

        getCommand("colorcodes").setExecutor(new ColorCodesCommand());
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("ride").setExecutor(new RideCommand(this));
        getCommand("shoplog").setExecutor(new ShopLogCommand(this));

        shopLog.load();

        if (getServer().getPluginManager().isPluginEnabled("PrometheusExporter")) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, exporter, 0L, 6000L);
        }
    }

    @Override
    public void onDisable() {
        exporter.shutdown();
    }

    @NotNull
    public LocalStorage getStorage() {
        return storage;
    }

    @NotNull
    public ShopLog getShopLog() {
        return shopLog;
    }

    @NotNull
    public TextFilter getTextFilter() {
        return textFilter;
    }
}
