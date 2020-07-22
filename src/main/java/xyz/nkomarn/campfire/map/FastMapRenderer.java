package xyz.nkomarn.campfire.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.kerosene.Kerosene;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Custom implementation of the Bukkit MapRenderer that caches which players
 * the image was rendered for to reduce map rendering lag.
 * <p>
 * Also renders the map asynchronously- not sure if this is totally safe yet.
 */
public class FastMapRenderer extends MapRenderer {

    private final BufferedImage image;
    private final Set<UUID> renderedPlayers = new HashSet<>();

    public FastMapRenderer(@NotNull BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (!renderedPlayers.contains(player.getUniqueId())) {
            renderedPlayers.add(player.getUniqueId());
            Kerosene.getPool().submit(() -> canvas.drawImage(0, 0, image));
        }
    }
}
