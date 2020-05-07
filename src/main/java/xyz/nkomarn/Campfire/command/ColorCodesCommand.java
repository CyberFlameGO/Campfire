package xyz.nkomarn.Campfire.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ColorCodesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        String colorCodes = "§r \n" +
                " §f§lColor Codes:\n" +
                " §1&1 Dark Blue\n" +
                " §2&2 Dark Green\n" +
                " §3&3 Dark Aqua\n" +
                " §4&4 Dark Red\n" +
                " §5&5 Dark Purple\n" +
                " §6&6 Gold\n" +
                " §7&7 Gray\n" +
                " §8&8 Dark Gray\n" +
                " §9&9 Blue\n" +
                " §a&a Green\n" +
                " §b&b Aqua\n" +
                " §c&c Red\n" +
                " §d&d Purple\n" +
                " §e&e Yellow\n" +
                " §f&f White\n" +
                "§r ";
        sender.sendMessage(colorCodes);
        return true;
    }
}
