package xyz.nkomarn.Campfire.command;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Advancements;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Campfire.util.RandomLocation;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class WildCommand implements CommandExecutor {
    private static HashMap<UUID, Long> cooldown = new HashMap<>();
    private static int queue = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            // Server-wide wild teleport cap check
            if (queue > 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lError: &7Hit server-wide wild teleport queue- please wait and try again."));
                Bukkit.getLogger().log(Level.WARNING, "Hit server-wide wild teleport limit.");
                return true;
            }

            // Make sure the player isn't in the cooldown (operators are excluded from cooldown)
            if (!player.isOp() && cooldown.containsKey(player.getUniqueId())) {
                long minutes = (System.currentTimeMillis() - cooldown.get(player.getUniqueId())) / 60000;

                if (minutes >= Config.getInteger("wild.cooldown")) {
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                } else {
                    long remaining = Config.getInteger("wild.cooldown") - minutes;

                    player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("wild.title.cooldown.top")),
                            ChatColor.translateAlternateColorCodes('&', Config.getString("wild.title.cooldown.bottom")
                                    .replace("[minutes]", String.valueOf(remaining))));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return true;
                }
            }

            // Find a random location and teleport
            queue += 1;
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("wild.title.finding.top")),
                    ChatColor.translateAlternateColorCodes('&', Config.getString("wild.title.finding.bottom")));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 254));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 254));
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);

            Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> {
                Location location = RandomLocation.getRandomSafeLocation(Objects.requireNonNull(Bukkit.getWorld(Config.getString("wild.world"))));
                Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                    player.teleport(location);
                    player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("wild.title.teleported.top")),
                            ChatColor.translateAlternateColorCodes('&', Config.getString("wild.title.teleported.bottom")));
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                    player.playSound(location, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
                    player.removePotionEffect(PotionEffectType.CONFUSION);
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    queue -= 1;

                    if (Advancements.isComplete(player, "rtp")) return;
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:rtp",
                            player.getName()));
                });
            });
        } else {
            sender.sendMessage("Wild teleport can only be used by players.");
        }
        return true;
    }
}
