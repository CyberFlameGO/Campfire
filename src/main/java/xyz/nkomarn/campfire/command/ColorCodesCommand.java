package xyz.nkomarn.campfire.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public class ColorCodesCommand implements TabExecutor {
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

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
