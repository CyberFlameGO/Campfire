package xyz.nkomarn.Wildfire.util;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.concurrent.ThreadLocalRandom;

public class RandomTeleport {

    public static Location getRandomSafeLocation(World world) {
        while (true) {

            // Get a random x- and z- coordinate within range
            int x = ThreadLocalRandom.current().nextInt(Config.getInteger("rtp.min.x"),
                    Config.getInteger("rtp.max.x"));
            int z = ThreadLocalRandom.current().nextInt(Config.getInteger("rtp.min.z"),
                    Config.getInteger("rtp.max.z"));

            Block block = world.getBlockAt(x, 62, z);
            Chunk chunk = world.getChunkAt(block);

            // Skip ocean biomes - those are unsuitable
            if (block.getBiome() == Biome.OCEAN) continue;

            // Check for nearby claims
            int claims = GriefPrevention.instance.dataStore.getClaims(chunk.getX(), chunk.getZ()).size();
            if (claims > 0) continue;

            // Figure out the highest safe y-value
            int min = Config.getInteger("rtp.min.y");
            int max = Config.getInteger("rtp.max.y");

            for (int y = min; y < max; y++) {
                Block randomBlock = world.getBlockAt(x, y, z);
                if (isLocationSafe(randomBlock)) {
                    return randomBlock.getLocation().add(0.5,1,0.5);
                }
            }

        }
    }

    private static boolean isLocationSafe(Block floor) {
        Block legs = floor.getLocation().add(0, 1,0).getBlock();
        Block head = floor.getLocation().add(0, 2,0).getBlock();

        return (floor.getType().isSolid() && legs.getType() == Material.AIR
            && head.getType() == Material.AIR);
    }

}
