package xyz.nkomarn.Campfire.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Custom implementation of the Bukkit MapRenderer that caches which players
 * the image was rendered for to eliminate map rendering lag.
 */
public class CustomMapRenderer extends MapRenderer {
    private BufferedImage image;
    private ArrayList<UUID> renderedPlayers = new ArrayList<>();

    public CustomMapRenderer(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if (renderedPlayers.contains(player.getUniqueId())) return;
        renderedPlayers.add(player.getUniqueId());
        canvas.drawImage(0, 0, image);
    }
}
