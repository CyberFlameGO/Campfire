package xyz.nkomarn.Campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.util.CooldownUtil;

import java.util.Collections;
import java.util.List;

public class SkullCommand implements TabExecutor {
    private static final String prefix = "&5&lSkull: &7";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sYou must be a player to use this command.", prefix
                )));
            } else if (CooldownUtil.cooldownToMinutes(CooldownUtil.getCooldown(((Player) sender).getUniqueId(), "skull")) < 720) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sYou can only get a skull every 12 hours.", prefix // TODO add remaining minutes/hours cooldown
                )));
            } else {
                Player player = (Player) sender;
                if (args.length < 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "%sGet another player's skull using /skull <name>.", prefix
                    )));
                } else { // TODO check for valid usernames with a pattern (borrow from Phase)
                    OfflinePlayer skullPlayer = Bukkit.getOfflinePlayer(args[0]);
                    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
                    playerHeadMeta.setOwningPlayer(skullPlayer);
                    playerHead.setItemMeta(playerHeadMeta);
                    Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> player.getInventory().addItem(playerHead)); // TODO check to make sure they have an open slot in their inventory first
                    CooldownUtil.resetCooldown(player.getUniqueId(), "skull");
                }
            }
        });
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
