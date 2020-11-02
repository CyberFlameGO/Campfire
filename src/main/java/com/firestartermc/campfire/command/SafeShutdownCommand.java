package com.firestartermc.campfire.command;

import com.firestartermc.campfire.Campfire;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SafeShutdownCommand implements TabExecutor {

    private final Campfire campfire;

    public SafeShutdownCommand(Campfire campfire) {
        this.campfire = campfire;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Bukkit.getServer().setWhitelist(true);
        ImmutableList.copyOf(Bukkit.getOnlinePlayers()).stream()
                .filter(player -> !player.hasPermission("firestarter.administrator"))
                .forEach(player -> player.kickPlayer("The server is undergoing a reboot. Hang tight."));
        Bukkit.getScheduler().runTaskLater(campfire, Bukkit::shutdown, 20 * 10L);
        sender.sendMessage(ChatColor.GOLD + "Shutting down in 10 seconds.");
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
