package xyz.nkomarn.campfire.task;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.campfire.util.Config;

public class PortalTask implements Runnable {

    private final World spawnWorld;
    private final Location minorCorner;
    private final Location majorCorner;
    private final Location particleLocation;

    public PortalTask(@NotNull Server server) {
        this.spawnWorld = server.getWorld("world");

        this.minorCorner = new Location(
                spawnWorld,
                Config.getInteger("world.wild.portal.min.x"),
                Config.getInteger("world.wild.portal.min.y"),
                Config.getInteger("world.wild.portal.min.z")
        );
        this.majorCorner = new Location(
                spawnWorld,
                Config.getInteger("world.wild.portal.max.x"),
                Config.getInteger("world.wild.portal.max.y"),
                Config.getInteger("world.wild.portal.max.z")
        );
        this.particleLocation = new Location(
                spawnWorld,
                Config.getInteger("world.wild.portal.particle.x") + 0.5,
                Config.getInteger("world.wild.portal.particle.y") + 0.5,
                Config.getInteger("world.wild.portal.particle.z") + 0.5
        );
    }

    @Override
    public void run() {
        spawnWorld.spawnParticle(Particle.CRIMSON_SPORE, particleLocation, 5);
        spawnWorld.spawnParticle(Particle.WARPED_SPORE, particleLocation, 5);

        for (Player player : spawnWorld.getPlayers()) {
            if (!isBetweenLocation(player, minorCorner, majorCorner)) {
                continue;
            }

            Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                player.chat("/wild");
                knockback(player, 2, 2);
            });
        }
    }

    private boolean isBetweenLocation(@NotNull Player player, @NotNull Location minor, @NotNull Location major) {
        Location location = player.getLocation();
        return (location.getBlockX() >= minor.getBlockX() && location.getBlockX() <= major.getBlockX())
                && (location.getBlockY() >= minor.getBlockY() && location.getBlockY() <= major.getBlockY())
                && (location.getBlockZ() >= minor.getBlockZ() && location.getBlockZ() <= major.getBlockZ());
    }

    public static void knockback(Player player, int multiplier, int y) {
        Vector locationVector = player.getLocation().toVector();
        Vector vector = locationVector.normalize().multiply(multiplier).setY(y);

        if (player.isInsideVehicle()) {
            player.getVehicle().setVelocity(vector);
        } else {
            player.setVelocity(vector);
        }
    }
}
