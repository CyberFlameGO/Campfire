package xyz.nkomarn.Campfire.maps;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;
import xyz.nkomarn.Campfire.Campfire;
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
    private static final Logger LOGGER = Campfire.getCampfire().getLogger();

    public static void loadMaps() {
        int loadedMaps = 0;

        try (Connection connection = LocalStorage.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS maps (id INTEGER PRIMARY KEY, map_id INTEGER" +
                    " NOT NULL, image TEXT NOT NULL);").execute();

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM maps;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        MapView view = Bukkit.getMap(result.getInt(2));
                        if (view == null) {
                            LOGGER.warning(String.format("Failed to load map #%s.", result.getInt(2)));
                        } else {
                            view.getRenderers().forEach(view::removeRenderer);

                            try {
                                BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder()
                                        .decode(result.getString(3).getBytes())));
                                view.addRenderer(new FastMapRenderer(image));
                                loadedMaps++;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LOGGER.info(String.format("Loaded %s maps.", loadedMaps));
    }
}