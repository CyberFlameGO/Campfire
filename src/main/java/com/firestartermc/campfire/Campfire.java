package com.firestartermc.campfire;

import com.firestartermc.campfire.command.*;
import com.firestartermc.campfire.listener.*;
import com.firestartermc.campfire.listener.player.*;
import com.firestartermc.campfire.task.DataExporter;
import com.firestartermc.campfire.util.TextFilter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Campfire extends JavaPlugin {

    private DataExporter exporter;
    private TextFilter textFilter;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        exporter = new DataExporter();
        textFilter = new TextFilter(this);

        Arrays.asList(
                new AdvancementCriterionListener(),
                new ChatListener(this),
                new CommandPreProcessListener(),
                new JoinListener(this),
                new RespawnListener(this),

                // TODO
                new EditListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        getCommand("loop").setExecutor(new LoopCommand(this));
        getCommand("safeshutdown").setExecutor(new SafeShutdownCommand(this));
        getCommand("setslots").setExecutor(new SetSlotsCommand());

        getCommand("colorcodes").setExecutor(new ColorCodesCommand());
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("ride").setExecutor(new RideCommand(this));

        if (getServer().getPluginManager().isPluginEnabled("PrometheusExporter")) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, exporter, 0L, 6000L);
        }
    }

    @Override
    public void onDisable() {
        exporter.shutdown();
    }

    @NotNull
    public TextFilter getTextFilter() {
        return textFilter;
    }
}
