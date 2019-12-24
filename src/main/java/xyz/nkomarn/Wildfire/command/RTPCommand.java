package xyz.nkomarn.Wildfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nkomarn.Wildfire.Wildfire;
import xyz.nkomarn.Wildfire.util.Config;
import xyz.nkomarn.Wildfire.util.RandomTeleport;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class RTPCommand implements CommandExecutor {

    private static HashMap<UUID, Long> cooldown = new HashMap<>();
    private static int queue = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            final Player player = (Player) sender;

            // Queue check
            if (queue > 2) {
                player.sendMessage(ChatColor.RED + "Hit server-wide RTP queue. Please wait and try again.");
                Bukkit.getLogger().log(Level.WARNING, "Hit RTP limit.");
                return true;
            }

            // Make sure the player isn't in the cooldown (operators are excluded from cooldown)
            if (!player.isOp() && cooldown.containsKey(player.getUniqueId())) {
                long minutes = (System.currentTimeMillis() - cooldown.get(player.getUniqueId())) / 60000;

                if (minutes >= Config.getInteger("rtp.cooldown")) {
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                } else {
                    long remaining = Config.getInteger("rtp.cooldown") - minutes;

                    player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.cooldown.top")),
                            ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.cooldown.bottom")
                            .replace("[minutes]", String.valueOf(remaining))));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return true;
                }
            }

            // Find a random location and teleport
            queue += 1;
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.finding.top")),
                    ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.finding.bottom")));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 254));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 254));
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);

            Bukkit.getScheduler().runTaskAsynchronously(Wildfire.instance, () -> {
                Location location = RandomTeleport.getRandomSafeLocation(Bukkit.getWorld(Config.getString("rtp.world")));
                /*player.teleportAsync(location).thenAccept(status -> {
                    if (status) {
                        player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.teleported.top")),
                                ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.teleported.bottom")));
                        cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                    } else {
                        player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.error.top")),
                                ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.error.bottom")));
                    }
                });*/

                // Teleport, remove potion effects, and play sound effect
                Bukkit.getScheduler().runTask(Wildfire.instance, () -> {
                    player.teleport(location);
                    player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.teleported.top")),
                            ChatColor.translateAlternateColorCodes('&', Config.getString("rtp.title.teleported.bottom")));
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                    player.playSound(location, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
                    player.removePotionEffect(PotionEffectType.CONFUSION);
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    queue -= 1;
                });
            });
        } else {
            sender.sendMessage("RTP can only be used by players.");
        }
        return true;
    }

}
