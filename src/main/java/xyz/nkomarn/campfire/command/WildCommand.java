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

    private final World world;
    private final int cooldown;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private final int minZ;
    private final int maxZ;

    public static final HashMap<UUID, Long> COOLDOWNS = new HashMap<>();

    public WildCommand() {
        this.world = Bukkit.getWorld(Config.getString("world.wild.world"));
        this.cooldown = Config.getInteger("world.wild.cooldown");
        this.minX = Config.getInteger("world.wild.min.x");
        this.maxX = Config.getInteger("world.wild.max.x");
        this.minY = Config.getInteger("world.wild.min.y");
        this.maxY = Config.getInteger("world.wild.max.y");
        this.minZ = Config.getInteger("world.wild.min.z");
        this.maxZ = Config.getInteger("world.wild.max.z");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp() && COOLDOWNS.containsKey(player.getUniqueId())) {
            long minutes = (System.currentTimeMillis() - COOLDOWNS.get(player.getUniqueId())) / 60000;

            if (minutes <= cooldown) {
                long remaining = cooldown - minutes;
                player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "UNDER COOLDOWN",
                        "Wait " + remaining + " more minute" + (remaining == 1 ? "." : "s."), 10, 70, 10);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }

            COOLDOWNS.put(player.getUniqueId(), System.currentTimeMillis());
        }

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
        player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "SEEKING LOCATION", "Hang tight for a bit", 10, 140, 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 254));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 254));
        attemptWildTeleport(player, world);
        return true;
    }

    /**
     * Try to find and teleport a player to a random location asynchronously.
     *
     * @param player The player to teleport.
     * @param world  The world to teleport inside of.
     */
    private void attemptWildTeleport(@NotNull Player player, @NotNull World world) {
        int chunkX = ThreadLocalRandom.current().nextInt(minX, maxX);
        int chunkZ = ThreadLocalRandom.current().nextInt(minZ, maxZ);

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

            for (int y = minY; y < maxY; y++) { // Find the highest suitable y-value
                Block randomBlock = chunk.getBlock(8, y, 8);

                if (!Teleport.checkSafetySync(randomBlock)) {
                    continue;
                }

                Teleport.teleportPlayer(player, randomBlock.getLocation().add(0.5, 1, 0.5)).thenAccept(result -> {
                    if (!result) {
                        player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "FAILURE",
                                "Try using /wild again", 10, 90, 20);
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                        COOLDOWNS.remove(player.getUniqueId());
                        return;
                    }

                    COOLDOWNS.put(player.getUniqueId(), System.currentTimeMillis());
                    Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                        player.playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "WELCOME... HOME?",
                                "You've arrived at a random place", 10, 90, 20
                        );

                        Advancement.grantAdvancement(player, "rtp");
                    });
                });

                return;
            }

            attemptWildTeleport(player, world); // If all else fails, try again :")
        });
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
