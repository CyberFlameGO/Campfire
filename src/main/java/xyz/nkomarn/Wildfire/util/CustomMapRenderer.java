package xyz.nkomarn.Wildfire.util;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

public class CustomMapRenderer extends MapRenderer {

    ArrayList<UUID> rendered = new ArrayList<>();

    BufferedImage image;

    public CustomMapRenderer(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(MapView mapView, MapCanvas canvas, Player player) {
        if (rendered.contains(player.getUniqueId())) return;
        rendered.add(player.getUniqueId());
        canvas.drawImage(0, 0, image);
    }

}
