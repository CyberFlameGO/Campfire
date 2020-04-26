package xyz.nkomarn.Campfire.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Campfire.listener.PvPListener;

public class PvPCommand implements CommandExecutor {
    private final String prefix = "&c&lPVP: &7";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "%sToggle your PvP using /pvp [on/off].", prefix
            )));
        } else if (args[0].equals("on")) {
            PvPListener.ENABLED_PLAYERS.add(player.getUniqueId());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sEnabled your PvP.", prefix
            )));
        } else if (args[0].equals("off")) {
            PvPListener.ENABLED_PLAYERS.remove(player.getUniqueId());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sDisabled your PvP.", prefix
            )));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sToggle your PvP using /pvp [on/off].", prefix
            )));
        }
        return true;
    }
}
