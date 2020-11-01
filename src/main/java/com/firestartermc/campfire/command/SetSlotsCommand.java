package com.firestartermc.campfire.command;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class SetSlotsCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        final String prefix = "&5&lSlots: &7";

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sSet the maximum player slots using /setslots [# of players].", prefix
            )));
        } else if (!NumberUtils.isDigits(args[0])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sAmount of slots must be a number.", prefix
            )));
        } else {
            int slots = Integer.parseInt(args[0]);

            // Set the maximum player slots while the server is running
            // Code borrowed from https://github.com/MrMicky-FR/ChangeSlots
            try {
                Method serverGetHandle = Bukkit.getServer().getClass().getDeclaredMethod("getHandle");
                Object playerList = serverGetHandle.invoke(Bukkit.getServer());
                Field maxPlayersField = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");

                maxPlayersField.setAccessible(true);
                maxPlayersField.set(playerList, slots);

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sSet the maximum player slots to %s.", prefix, slots
                )));
            } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sAn error occurred while trying to set the maximum slots.", prefix
                )));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
