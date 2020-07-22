package xyz.nkomarn.campfire.util;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Utility class used to modify and send the world border
 * to online players using NMS and packets.
 */
public class BorderUtil {
    /**
     * Checks whether a location is outside of the set border for a world.
     *
     * @param location The location to border check.
     * @return Whether the given location would land outside of the border.
     */
    public static boolean isLocationOutsideBorder(Location location) {
        return isLocationOutsideBorder(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    /**
     * Checks whether a location is outside of the set border for a world.
     *
     * @param world The world to compare border size with.
     * @param x The x-coordinate of the block to check.
     * @param z The z-coordinate of the block to check.
     * @return Whether the given location would land outside of the border.
     */
    public static boolean isLocationOutsideBorder(World world, double x, double z) {
        double border = world.getWorldBorder().getSize() / 2;
        return x > border || x < -border || z > border || z < -border;
    }
}
