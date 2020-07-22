package xyz.nkomarn.campfire.command;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.campfire.util.Claims;
import xyz.nkomarn.campfire.util.Config;
import xyz.nkomarn.kerosene.paperlib.PaperLib;
import xyz.nkomarn.kerosene.util.Advancement;
import xyz.nkomarn.kerosene.util.world.Teleport;;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Asynchronous random teleportation command.
 * Uses PaperLib to load chunks and teleport asynchronously.
 */
public class WildCommand implements TabExecutor {

    private static final HashMap<UUID, Long> COOLDOWN = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Make sure the player isn't on cooldown- operators are excluded.
            if (!player.isOp() && COOLDOWN.containsKey(player.getUniqueId())) {
                long minutes = (System.currentTimeMillis() - COOLDOWN.get(player.getUniqueId())) / 60000;

                if (minutes >= Config.getInteger("world.wild.cooldown")) {
                    COOLDOWN.put(player.getUniqueId(), System.currentTimeMillis());
                } else {
                    long remaining = Config.getInteger("world.wild.cooldown") - minutes;
                    player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("world.wild.title.cooldown.top")),
                            ChatColor.translateAlternateColorCodes('&', Config.getString("world.wild.title.cooldown.bottom")
                                    .replace("[minutes]", String.valueOf(remaining))), 10, 70, 20);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return true;
                }
            }

            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("world.wild.title.finding.top")),
                    ChatColor.translateAlternateColorCodes('&', Config.getString("world.wild.title.finding.bottom")), 10, 70, 20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 254));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 254));
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
            attemptWildTeleport(player, Bukkit.getWorld(Config.getString("world.wild.world")));
        } else {
            sender.sendMessage("Wild teleport can only be used by players.");
        }
        return true;
    }

    /**
     * Try to find and teleport a player to a random location asynchronously.
     *
     * @param player The player to teleport.
     * @param world  The world to teleport inside of.
     */
    private static void attemptWildTeleport(@NotNull Player player, @NotNull World world) {
        int chunkX = ThreadLocalRandom.current().nextInt(Config.getInteger("world.wild.min.x"), Config.getInteger("world.wild.max.x"));
        int chunkZ = ThreadLocalRandom.current().nextInt(Config.getInteger("world.wild.min.z"), Config.getInteger("world.wild.max.z"));

        PaperLib.getChunkAtAsync(world, chunkX, chunkZ, true).thenAccept(chunk -> {
            Block block = chunk.getBlock(8, 62, 8);

            if (block.getBiome() == Biome.OCEAN) { // Ignore ocean biomes
                attemptWildTeleport(player, world);
                return;
            }

            if (Claims.checkForeignClaims(player, block.getLocation())) { // Check for claims in the area
                attemptWildTeleport(player, world);
                return;
            }

            int min = Config.getInteger("world.wild.min.y");
            int max = Config.getInteger("world.wild.max.y");

            for (int y = min; y < max; y++) { // Find the highest suitable y-value
                Block randomBlock = chunk.getBlock(8, y, 8);
                if (Teleport.checkSafetySync(randomBlock)) {
                    Teleport.teleportPlayer(player, randomBlock.getLocation().add(0.5, 1, 0.5));
                    Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                        player.sendTitle(ChatColor.translateAlternateColorCodes('&',
                                Config.getString("world.wild.title.teleported.top")), ChatColor.translateAlternateColorCodes('&',
                                Config.getString("world.wild.title.teleported.bottom")));
                        COOLDOWN.put(player.getUniqueId(), System.currentTimeMillis());
                        player.playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);

                        Advancement.grantAdvancement(player, "rtp");
                    });
                    return;
                }
            }

            attemptWildTeleport(player, world); // If all else fails, try again :")
        });
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
