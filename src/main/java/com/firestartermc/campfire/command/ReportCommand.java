package com.firestartermc.campfire.command;

import com.firestartermc.campfire.Campfire;
import com.firestartermc.kerosene.util.webhook.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ReportCommand implements TabExecutor {

    private final Campfire campfire;

    public ReportCommand(Campfire campfire) {
        this.campfire = campfire;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lReport: &7Report a player using /report <player to report> <reason>."));
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(campfire, () -> {
                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }

                var embed = DiscordWebhook.Embed.builder()
                        .title("🚩 Player Report")
                        .color(Color.decode("0xff5c5c"))
                        .addField("Reporter", sender.getName(), false)
                        .addField("Reported Player", args[0], false)
                        .addField("Reason", reason.toString().trim(), false)
                        .build();
                DiscordWebhook.create(campfire.getConfig().getString("webhooks.moderation"))
                        .embed(embed)
                        .queue();

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lReport: &7Your report has been sent to the staff team."));

                Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.hasPermission("firestarter.staff"))
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
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return null;
        }
        return Collections.emptyList();
    }
}
