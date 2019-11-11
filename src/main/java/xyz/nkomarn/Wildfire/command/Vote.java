package xyz.nkomarn.Wildfire.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.nkomarn.Wildfire.util.Config;

public class Vote implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.getString("vote.message")));
        return true;
    }

}
