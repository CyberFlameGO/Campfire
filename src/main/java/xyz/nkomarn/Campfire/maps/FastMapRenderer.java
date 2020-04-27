package xyz.nkomarn.Campfire.maps;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import xyz.nkomarn.Campfire.Campfire;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Custom implementation of the Bukkit MapRenderer that caches which players
 * the image was rendered for to reduce map rendering lag.
 */
public class FastMapRenderer extends MapRenderer {
    private final BufferedImage image;
    private final Set<UUID> renderedPlayers = new HashSet<>();

    public FastMapRenderer(final BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if (!renderedPlayers.contains(player.getUniqueId())) {
            renderedPlayers.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskAsynchronously(Campfire.getCampfire(), () -> canvas.drawImage(0, 0, image));
        }
    }
}
