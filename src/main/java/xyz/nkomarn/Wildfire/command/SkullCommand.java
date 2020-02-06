package xyz.nkomarn.Wildfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.nkomarn.Wildfire.Wildfire;
import xyz.nkomarn.Wildfire.util.Cooldown;

import java.util.concurrent.TimeUnit;

public class SkullCommand implements CommandExecutor {
    final String prefix = "&d&lSkulls: &7";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("firestarter.features.skull")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "%sSkulls is a donor feature that requires the Spark rank.", prefix
            )));
            return true;
        }

        if (!(sender instanceof Player)) return true;
        Bukkit.getScheduler().runTask(Wildfire.instance, () -> {
            Player player = (Player) sender;

            // Cooldown check
            long cooldown = Cooldown.getCooldown(player.getUniqueId().toString(), "skull");
            long difference = System.currentTimeMillis() - cooldown;
            if (difference <= 3600000L) {
                long time = 3600000L - difference;
                long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

                if (seconds <= 60) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "%sYou can get another skull in %s seconds.", prefix, seconds
                    )));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "%sYou can get another skull in %s minutes.", prefix, minutes
                    )));
                }
                return;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sObtain a player's head using &o/skull [player name]&7.", prefix
                )));
                return;
            }

            // Check for between 3-16 chars
            if (!args[0].matches("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_]+")
                    || args[0].length() < 3 || args[0].length() > 16) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "%sThe player name you entered is invalid.", prefix
                )));
                return;
            }

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(args[0]));
            skull.setItemMeta(skullMeta);
            player.getInventory().setItem(player.getInventory().firstEmpty(), skull);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sGave you the skull- enjoy!", prefix
            )));
            Cooldown.resetCooldown(player.getUniqueId().toString(), "skull");
        });
        return true;
    }
}
