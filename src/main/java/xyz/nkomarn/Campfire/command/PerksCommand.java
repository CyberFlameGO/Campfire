package xyz.nkomarn.Campfire.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Campfire.gui.menu.PotionSlotsMenu;

public class PerksCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        new PotionSlotsMenu((Player) sender);
        return true;
    }
}
