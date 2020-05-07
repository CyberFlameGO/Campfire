package xyz.nkomarn.Campfire.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.util.VanishUtil;

public class VanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        if (VanishUtil.isVanished(player)) {
            VanishUtil.showPlayer(player);
            Campfire.updateTablistHeader();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lVanish: &7You are no longer vanished."));
        } else {
            VanishUtil.hidePlayer(player);
            Campfire.updateTablistHeader();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lVanish: &7You are now vanished."));
        }
        return true;
    }
}
