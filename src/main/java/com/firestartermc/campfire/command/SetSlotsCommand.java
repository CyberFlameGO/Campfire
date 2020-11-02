package com.firestartermc.campfire.command;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

public class SetSlotsCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1 || !NumberUtils.isDigits(args[0])) {
            return false;
        }

        var slots = Integer.parseInt(args[0]);
        if (slots < 0) {
            return false;
        }

        try {
            var serverGetHandle = Bukkit.getServer().getClass().getDeclaredMethod("getHandle");
            var playerList = serverGetHandle.invoke(Bukkit.getServer());
            var maxPlayersField = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");

            maxPlayersField.setAccessible(true);
            maxPlayersField.set(playerList, slots);
            sender.sendMessage(ChatColor.GREEN + "Updated maximum slots to " + NumberFormat.getInstance().format(slots) + ".");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to adjust maximum slots.");
        }

        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
