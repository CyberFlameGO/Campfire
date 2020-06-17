package xyz.nkomarn.Campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.Webhooks;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ReportCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lReport: &7Report a player using /report <player to report> <reason>."));
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }

                try {
                    Webhooks hook = new Webhooks(Config.getString("webhooks.moderation"));
                    hook.addEmbed(new Webhooks.EmbedObject()
                            .setTitle("🚩 Player Report")
                            .setColor(Color.decode("0xff5c5c"))
                            .addField("Reporter", sender.getName(), false)
                            .addField("Reported Player", args[0], false)
                            .addField("Reason", reason.toString().trim(), false)
                    );
                    hook.execute();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&4&lReport: &7Your report has been sent to the staff team."));
                } catch (IOException e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&4&lReport: &cFailed to send your report.."));
                }

                Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.hasPermission("campfire.staff"))
                        .forEach(player -> {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                                    "&4&lReport: &c%s reported %s: \"%s\"", sender.getName(), args[0], reason.toString().trim()
                            )));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
                        });
            });
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length == 1) {
            return null;
        }
        return Collections.emptyList();
    }
}
