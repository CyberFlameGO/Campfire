package xyz.nkomarn.Campfire.util;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class that finds a random safe location in the world.
 * Used for the RTP command.
 */
public class RandomLocation {
    public static Location getRandomSafeLocation(World world) {
        while (true) {
            // Get a random x- and z- coordinate within range
            int x = ThreadLocalRandom.current().nextInt(Config.getInteger("wild.min.x"),
                    Config.getInteger("wild.max.x"));
            int z = ThreadLocalRandom.current().nextInt(Config.getInteger("wild.min.z"),
                    Config.getInteger("wild.max.z"));

            Block block = world.getBlockAt(x, 62, z);
            Chunk chunk = world.getChunkAt(block);

            // Skip ocean biomes - those are unsuitable
            if (block.getBiome() == Biome.OCEAN) continue;

            // Check for nearby claims
            int claims = GriefPrevention.instance.dataStore.getClaims(chunk.getX(), chunk.getZ()).size();
            if (claims > 0) continue;

            // Figure out the highest safe y-value
            int min = Config.getInteger("wild.min.y");
            int max = Config.getInteger("wild.max.y");

            for (int y = min; y < max; y++) {
                Block randomBlock = world.getBlockAt(x, y, z);
                if (isLocationSafe(randomBlock)) {
                    return randomBlock.getLocation().add(0.5,1,0.5);
                }
            }
        }
    }

    // Checks if the location is safe for player teleportation
    private static boolean isLocationSafe(Block floor) {
        Block legs = floor.getLocation().add(0, 1,0).getBlock();
        Block head = floor.getLocation().add(0, 2,0).getBlock();

        return (floor.getType().isSolid() && legs.getType() == Material.AIR
                && head.getType() == Material.AIR);
    }
}
