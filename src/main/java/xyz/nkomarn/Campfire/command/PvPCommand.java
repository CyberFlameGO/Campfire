package xyz.nkomarn.Campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.nkomarn.Campfire.listener.PvPListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PvPCommand implements TabExecutor {
    private static final String PREFIX = "&c&lPVP: &7";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player) && args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: 'pvp <on/off> <player>'");
            return true;
        }

        if (args.length < 1) {
            Player player = (Player) sender;

            if (PvPListener.ENABLED_PLAYERS.contains(player.getUniqueId())) {
                PvPListener.ENABLED_PLAYERS.remove(player.getUniqueId());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sDisabled your PvP.", PREFIX
                )));
            } else {
                PvPListener.ENABLED_PLAYERS.add(player.getUniqueId());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sEnabled your PvP.", PREFIX
                )));
            }
            return true;
        }

        boolean newState;
        if(args[0].equalsIgnoreCase("on")) {
            newState = true;
        } else if(args[0].equalsIgnoreCase("off")) {
            newState = false;
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sToggle your PvP using /pvp [on/off].", PREFIX
            )));
            return true;
        }

        if (args.length == 1) {
            Player player = (Player) sender;
            if (newState) {
                PvPListener.ENABLED_PLAYERS.add(player.getUniqueId());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sEnabled your PvP.", PREFIX
                )));
            } else {
                PvPListener.ENABLED_PLAYERS.remove(player.getUniqueId());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sDisabled your PvP.", PREFIX
                )));
            }
            return true;
        }

        if (args.length == 2 && sender.hasPermission("campfire.admin")) {
            Player player = Bukkit.getPlayer(args[1]);

            if (player == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%s%s is not online.", PREFIX, args[1]
                )));
                return true;
            }

            if (newState) {
                PvPListener.ENABLED_PLAYERS.add(player.getUniqueId());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sEnabled %s's PvP.", PREFIX, player.getName()
                )));
            } else {
                PvPListener.ENABLED_PLAYERS.remove(player.getUniqueId());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sDisabled %s's PvP.", PREFIX, player.getName()
                )));
            }

            return true;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "%sToggle your PvP using /pvp [on/off].", PREFIX
        )));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("on", "off");
        }

        if(args.length == 2 && sender.hasPermission("campfire.admin")) {
            return null;
        }

        return Collections.emptyList();
    }
}
