package xyz.nkomarn.Campfire.command;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Campfire.gui.PerksMenu;

public class PerksCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("campfire.perks") || sender instanceof Player) {
            Player player = (Player) sender;
            new PerksMenu(player);
            player.playSound(player.getLocation(), Sound.BLOCK_COMPOSTER_EMPTY, 1.0f, 0.5f);
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPerks: &7You need to be a donor to " +
                    "use perks menu- check out /ranks."));
        }
        return true;
    }
}
