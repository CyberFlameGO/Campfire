package com.firestartermc.campfire.command;

import com.firestartermc.campfire.Campfire;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Collections;
import java.util.List;

public class LoopCommand implements TabExecutor {

    private final Campfire campfire;
    private final PeriodFormatter formatter;

    public LoopCommand(Campfire campfire) {
        this.campfire = campfire;
        this.formatter = new PeriodFormatterBuilder()
                .appendMillis().appendSuffix("ms")
                .appendSeconds().appendSuffix("s")
                .appendMinutes().appendSuffix("m")
                .toFormatter();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 3 || !NumberUtils.isDigits(args[0])) {
            return false;
        }

        var amount = Integer.parseInt(args[0]);
        if (amount < 1) {
            return false;
        }

        Period period;
        try {
            period = formatter.parsePeriod(args[1]);
        } catch (Exception e) {
            return false;
        }

        var builder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        runIn(sender, builder.toString().trim(), amount, period.getSeconds() * 20);
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private void runIn(CommandSender sender, String action, int amount, int interval) {
        campfire.getServer().getScheduler().runTaskLater(campfire, () -> {
            campfire.getServer().dispatchCommand(sender, action.startsWith("/") ? action.substring(1) : action);

            if (amount - 1 > 0) {
                runIn(sender, action, amount - 1, interval);
            } else {
                sender.sendMessage(ChatColor.GREEN + "Finished loop execution.");
            }
        }, interval);
    }
}
