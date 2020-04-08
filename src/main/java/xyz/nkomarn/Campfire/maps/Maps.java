package xyz.nkomarn.Campfire.maps;

import org.bukkit.Bukkit;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import xyz.nkomarn.Kerosene.Kerosene;
import xyz.nkomarn.Kerosene.data.LocalStorage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Utility class to manage custom server maps.
 */
public class Maps {
    /**
     * Loads maps from local storage into the server.
     */
    public static void loadMaps() {
        final Logger logger = Kerosene.getKerosene().getLogger();
        Connection connection = null;

        try {
            connection = LocalStorage.getConnection();
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS maps " +
                    "(id INTEGER PRIMARY KEY, map_id INTEGER NOT NULL, image TEXT NOT NULL);");
            statement.execute();
            statement = connection.prepareStatement("SELECT * FROM maps;");
            ResultSet mapsResult = statement.executeQuery();

            while (mapsResult.next()) {
                final MapView mapView = Bukkit.getMap(mapsResult.getInt(2));

                try {
                    for (final MapRenderer renderer : mapView.getRenderers()) {
                        mapView.removeRenderer(renderer);
                    }
                } catch (NullPointerException e) {
                    logger.warning(String.format("Failed to load map %s.", mapView.getId()));
                    continue;
                }

                BufferedImage image = null;
                ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(mapsResult
                        .getString(3).getBytes()));
                try {
                    image = ImageIO.read(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mapView.addRenderer(new FastMapRenderer(image));
                logger.info(String.format("Loaded map %s.", mapsResult.getInt(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
