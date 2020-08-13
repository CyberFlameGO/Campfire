package xyz.nkomarn.campfire.map;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;
import xyz.nkomarn.campfire.Campfire;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;;

/**
 * Utility class to manage custom server maps.
 */
public class Maps {

    public static void loadMaps() {
        AtomicInteger loadedMaps = new AtomicInteger();

        try (Connection connection = Campfire.getStorage().getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS maps (id INTEGER PRIMARY KEY, map_id INTEGER NOT NULL, image TEXT NOT NULL);").execute();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM maps");
            ResultSet result = statement.executeQuery();

            try (statement; result) {
                while (result.next()) {
                    MapView mapView = Bukkit.getMap(result.getInt(2));

                    if (mapView == null) {
                        continue;
                    }

                    BufferedImage mapImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(result.getString(3).getBytes())));
                    mapView.addRenderer(new FastMapRenderer(mapImage));
                    loadedMaps.incrementAndGet();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            Campfire.getCampfire().getLogger().info("Loaded " + loadedMaps.get() + " maps.");
        }
    }
}