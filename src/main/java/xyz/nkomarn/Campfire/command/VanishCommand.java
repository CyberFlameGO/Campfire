package xyz.nkomarn.Campfire.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Kerosene.util.VanishUtil;

public class VanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        // TODO update tablist

        if (VanishUtil.isVanished(player)) {
            VanishUtil.showPlayer(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lVanish: &7You are no longer vanished."));
        } else {
            VanishUtil.hidePlayer(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lVanish: &7You are now vanished."));
        }
        return true;
    }
}
