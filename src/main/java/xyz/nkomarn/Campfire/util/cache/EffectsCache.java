package xyz.nkomarn.Campfire.util.cache;

import xyz.nkomarn.Campfire.util.Config;
import xyz.nkomarn.Kerosene.data.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Potion effect cache for players' potion effect slots.
 */
public final class EffectsCache {
    public static final Map<UUID, String[]> CACHE = new HashMap<>();
    private static final Map<String, Integer> EFFECT_LEVEL = new HashMap<>();

    private EffectsCache() { }

    /**
     * Cache a player's potion effect slots.
     *
     * @param uuid The player for which to cache effect slots.
     */
    public static void cache(UUID uuid) {
        try (Connection connection = PlayerData.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `slot1`, `slot2`, " +
                    "`slot3` FROM `effects` WHERE `uuid` = ?;")) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        return;
                    }

                    CACHE.put(uuid, new String[]{
                            result.getString(1),
                            result.getString(2),
                            result.getString(3),
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return effect slots from cache. If not cached, read from database and cache.
     *
     * @param uuid The player for which to cache effect slots.
     * @return The player's effect slots.
     */
    public static String[] get(UUID uuid) {
        if (!CACHE.containsKey(uuid)) {
            try (Connection connection = PlayerData.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO " +
                        "`effects`(`uuid`) VALUES (?);")) {
                    statement.setString(1, uuid.toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            cache(uuid);
        }
        return CACHE.get(uuid);
    }

    /**
     * Returns the level for a specific potion effect, as defined in the config.
     *
     * @param effect The effect for which to return the level.
     * @return The level of the specified effect.
     */
    public static int getEffectLevel(String effect) {
        if (!EFFECT_LEVEL.containsKey(effect)) {
            EFFECT_LEVEL.put(effect, Config.getInteger(String.format("perks.potions.%s.level", effect), 1) - 1);
        }
        return EFFECT_LEVEL.get(effect);
    }

    /**
     * Invalidate cached slots for a player.
     */
    public static void invalidate(UUID uuid) {
        CACHE.remove(uuid);
    }
}
