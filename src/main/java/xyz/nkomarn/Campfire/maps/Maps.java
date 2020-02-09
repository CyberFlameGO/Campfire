package xyz.nkomarn.Campfire.maps;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Kerosene.database.subscribers.SimpleSubscriber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to manage custom server maps.
 */
public class Maps {
    private static Logger logger = Campfire.getCampfire().getLogger();

    // Load maps from database into the server
    public static void loadMaps() {
        Campfire.getMaps().async().find().subscribe(new SimpleSubscriber<Document>() {
            @Override
            public void onNext(Document document) {
                Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> {
                    MapView mapView = Bukkit.getMap(document.getInteger("id"));

                    try {
                        for (MapRenderer renderer : mapView.getRenderers()) {
                            mapView.removeRenderer(renderer);
                        }
                    } catch (NullPointerException e) {
                        logger.log(Level.WARNING,
                                "Map renders could not be fetched for MapView while loading custom map.");
                        return;
                    }

                    // Fetch the image
                    BufferedImage image = null;
                    ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder()
                            .decode(document.getString("image").getBytes()));
                    try {
                        image = ImageIO.read(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Apply new custom renderer
                    mapView.addRenderer(new CustomMapRenderer(image));
                    logger.log(Level.INFO, "Loaded map " + document.getInteger("id") + ".");
                });
            }
        });
    }
}
