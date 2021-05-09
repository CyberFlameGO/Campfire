package com.firestartermc.campfire.command;

import com.firestartermc.campfire.Campfire;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SafeShutdownCommand implements CommandExecutor, Runnable {

    private final Campfire campfire;
    private int countdown;

    public SafeShutdownCommand(Campfire campfire) {
        this.campfire = campfire;
        this.countdown = 5;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskTimer(campfire, this, 20L, 20L);
        return true;
    }

    @Override
    public void run() {
        Bukkit.getServer().setWhitelist(true);

        if (countdown > 0) {
            var message = "Server is rebooting in " + countdown + (countdown == 1 ? " second." : " seconds.");

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendTitle("", message, 0, 70, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f);
            });

            --countdown;
        } else {
            Bukkit.shutdown();
        }
    }
}
