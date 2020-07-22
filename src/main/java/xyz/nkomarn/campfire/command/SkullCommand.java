package xyz.nkomarn.campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.kerosene.Kerosene;
import xyz.nkomarn.kerosene.data.Cooldown;
import xyz.nkomarn.kerosene.util.item.SkullBuilder;

import java.util.Collections;
import java.util.List;

public class SkullCommand implements TabExecutor {
    private static final String prefix = "&5&lSkull: &7";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Kerosene.getPool().submit(() -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sYou must be a player to use this command.", prefix
                )));
                return;
            }

            Player player = (Player) sender;

            if (Cooldown.cooldownToMinutes(Cooldown.getCooldown(player.getUniqueId(), "skull")) < 720) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sYou can only get a skull every 12 hours.", prefix // TODO add remaining minutes/hours cooldown
                )));
            } else {
                if (args.length < 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "%sGet another player's skull using /skull <name>.", prefix
                    )));
                } else { // TODO check for valid usernames with a pattern (borrow from Phase)
                    OfflinePlayer skullPlayer = Bukkit.getOfflinePlayer(args[0]);
                    ItemStack skull = new SkullBuilder()
                            .name(String.format("&d&l%s's &5&lSkull", skullPlayer.getName()))
                            .player(skullPlayer)
                            .build();

                    Bukkit.getScheduler().runTask(Campfire.getCampfire(), () ->
                            player.getInventory().addItem(skull).forEach((integer, itemStack) ->
                                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack)));
                    Cooldown.resetCooldown(player.getUniqueId(), "skull");
                }
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return null;
        }
        return Collections.emptyList();
    }
}
