package xyz.nkomarn.Campfire.util;

import net.minecraft.server.v1_15_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_15_R1.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.nkomarn.Campfire.Campfire;

/**
 * Utility class used to modify and send the world border
 * to online players using NMS and packets.
 */
public class Border {
    /**
     * Send a fake world border to the client based on the world they are in.
     * @param player The player to send the world border to.
     */
    public static void sendBorder(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Campfire.getCampfire(), () -> {
            WorldBorder border = new WorldBorder();
            border.setCenter(0.5, 0.5);
            border.setSize(getWorldBorder(player.getWorld()) * 2);
            border.world = ((CraftWorld) player.getWorld()).getHandle();
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(
                    border, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE
            ));
        }, 40);
    }

    /**
     * Checks whether a location is outside of the set border for a world.
     * @param location The location to border check.
     * @return Whether the given location would land outside of the border.
     */
    public static boolean isLocationOutsideBorder(Location location) {
        int border = getWorldBorder(location.getWorld()); // Assume this is 50,000
        return location.getBlockX() > border ||
                location.getBlockX() < -border ||
                location.getBlockZ() > border ||
                location.getBlockZ() < -border;
    }

    /**
     * Returns the set border in blocks radius for a world.
     * @param world The world to fetch the border radius for.
     * @return The border, in blocks radius, for the given world.
     */
    private static int getWorldBorder(World world) {
        return Config.getInteger(String.format("world.border.worlds.%s", world.getName()));
    }
}
