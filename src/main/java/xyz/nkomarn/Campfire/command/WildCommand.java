package xyz.nkomarn.Campfire.command;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.paperlib.PaperLib;
import xyz.nkomarn.Kerosene.util.AdvancementUtil;
import xyz.nkomarn.Kerosene.util.ClaimUtil;
import xyz.nkomarn.Kerosene.util.LocationUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Asynchronous random teleportation command.
 * Uses PaperLib to load chunks and teleport asynchronously.
 */
public class WildCommand implements CommandExecutor {
    private static HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            // Make sure the player isn't on cooldown- operators are excluded.
            if (!player.isOp() && cooldown.containsKey(player.getUniqueId())) {
                long minutes = (System.currentTimeMillis() - cooldown.get(player.getUniqueId())) / 60000;

                if (minutes >= Config.getInteger("wild.cooldown")) {
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                } else {
                    long remaining = Config.getInteger("wild.cooldown") - minutes;

                    player.sendTitle(ChatColor.translateAlternateColorCodes('&',
                            Config.getString("wild.title.cooldown.top")),
                            ChatColor.translateAlternateColorCodes('&',
                                    Config.getString("wild.title.cooldown.bottom")
                                    .replace("[minutes]", String.valueOf(remaining))));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return true;
                }
            }

            player.sendTitle(ChatColor.translateAlternateColorCodes('&',
                    Config.getString("wild.title.finding.top")),
                    ChatColor.translateAlternateColorCodes('&',
                            Config.getString("wild.title.finding.bottom")));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 254));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 254));
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);

            attemptWildTeleport(player, Bukkit.getWorld(Config.getString("wild.world")));
        } else {
            sender.sendMessage("Wild teleport can only be used by players.");
        }
        return true;
    }

    private static void attemptWildTeleport(Player player, World world) {
        int chunkX = ThreadLocalRandom.current().nextInt(Config.getInteger("wild.min.x"),
                Config.getInteger("wild.max.x"));
        int chunkZ = ThreadLocalRandom.current().nextInt(Config.getInteger("wild.min.z"),
                Config.getInteger("wild.max.z"));

        PaperLib.getChunkAtAsync(world, chunkX, chunkZ, true).thenAccept(chunk -> {
            Block block = chunk.getBlock(8, 62, 8);

            if (block.getBiome() == Biome.OCEAN) { // Ignore ocean biomes
                attemptWildTeleport(player, world);
                return;
            }

            if (ClaimUtil.doesLocationHaveForeignClaims(player, block.getLocation())) { // Check for claims in the area
                attemptWildTeleport(player, world);
                return;
            }

            int min = Config.getInteger("wild.min.y");
            int max = Config.getInteger("wild.max.y");

            for (int y = min; y < max; y++) { // Find the highest suitable y-value
                Block randomBlock = chunk.getBlock(8, y, 8);
                if (LocationUtil.isLocationSafe(randomBlock)) {
                    LocationUtil.teleportPlayer(player, randomBlock.getLocation().add(0.5,1,0.5));
                    Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                        player.sendTitle(ChatColor.translateAlternateColorCodes('&',
                                Config.getString("wild.title.teleported.top")),
                                ChatColor.translateAlternateColorCodes('&',
                                        Config.getString("wild.title.teleported.bottom")));
                        cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                        player.playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);

                        AdvancementUtil.grantAdvancement(player, "rtp");
                    });
                    return;
                }
            }

            attemptWildTeleport(player, world); // If all else fails, try again :")
        });
    }
}
