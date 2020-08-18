package xyz.nkomarn.campfire.util;

import org.bukkit.*;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.kerosene.Kerosene;
import xyz.nkomarn.kerosene.util.item.ItemBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Copyright extends BukkitRunnable implements Listener {

    private static final Int2ObjectMap<CopyrightInfo> COPYRIGHT_CACHE = new Int2ObjectOpenHashMap<>();

    public Copyright() {
        runTaskTimer(Campfire.getCampfire(), 0L, 420 * 20L);
    }

    /**
     * Creates the necessary local database table and caches currently copyrighted maps.
     */
    public static void load() {
        Kerosene.getPool().submit(() -> {
            try (Connection connection = Campfire.getStorage().getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS map_copyright (map_id INTEGER PRIMARY KEY NOT NULL, owner TEXT NOT NULL, expires REAL NOT NULL);").execute();

                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM map_copyright;")) {
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            CopyrightInfo info = new CopyrightInfo(
                                    result.getInt(1),
                                    UUID.fromString(result.getString(2)),
                                    result.getLong(3)
                            );

                            COPYRIGHT_CACHE.put(info.getMapId(), info);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Returns the available copyright information for a given map.
     *
     * @param mapId The map's MapView id.
     * @return Future of available copyright information.
     */
    public static Optional<CopyrightInfo> getCopyright(int mapId) {
        return Optional.ofNullable(COPYRIGHT_CACHE.get(mapId));
    }

    /**
     * Checks if a map ItemStack is copyrighted.
     *
     * @param map The map ItemStack.
     * @return Whether the map ItemStack is copyrighted.
     */
    public static boolean isCopyrighted(@NotNull ItemStack map) {
        if (map.getType() != Material.FILLED_MAP) {
            return false;
        }

        MapView mapView = ((MapMeta) map.getItemMeta()).getMapView();

        if (mapView == null) {
            return false;
        }

        return COPYRIGHT_CACHE.get(mapView.getId()) != null;
    }

    /**
     * Checks if a map ItemStack is copyrighted with player context.
     *
     * @param player The player context.
     * @param map    The map ItemStack.
     * @return Whether the map ItemStack is copyrighted.
     */
    public static boolean isCopyrighted(@NotNull OfflinePlayer player, @NotNull ItemStack map) {
        if (map.getType() != Material.FILLED_MAP) {
            return false;
        }

        MapView mapView = ((MapMeta) map.getItemMeta()).getMapView();

        if (mapView == null) {
            return false;
        }

        CopyrightInfo copyright = COPYRIGHT_CACHE.get(mapView.getId());

        if (copyright == null) {
            return false;
        }

        return !copyright.getMapOwner().equals(player.getUniqueId());
    }

    /**
     * Copyrights a map for 60 days.
     *
     * @param mapId The map to copyright.
     * @param owner The copyright holder.
     * @return Future of copyright result.
     */
    public static CompletableFuture<Boolean> copyrightMap(int mapId, @NotNull OfflinePlayer owner) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Kerosene.getPool().submit(() -> {
            try (Connection connection = Campfire.getStorage().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO map_copyright (map_id, owner, expires) VALUES (?, ?, ?) ON CONFLICT(map_id) DO UPDATE SET expires = expires + 5184000000;")) {
                    CopyrightInfo info = COPYRIGHT_CACHE.get(mapId);

                    if (info != null) {
                        info.setExpiration(info.getExpiration() + 5184000000L);
                    } else {
                        info = new CopyrightInfo(
                                mapId,
                                owner.getUniqueId(),
                                System.currentTimeMillis() + 5184000000L
                        );
                    }

                    statement.setInt(1, info.getMapId());
                    statement.setString(2, info.getMapOwner().toString());
                    statement.setLong(3, info.getExpiration());
                    statement.executeUpdate();

                    COPYRIGHT_CACHE.put(mapId, info);
                    future.complete(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                future.complete(false);
            }
        });

        return future;
    }

    @Override
    public void run() { // TODO rewrite this to only use the database when deleting
        for (CopyrightInfo copyright : COPYRIGHT_CACHE.values()) {
            if (copyright.getExpiration() > System.currentTimeMillis()) {
                continue;
            }

            Kerosene.getPool().submit(() -> {
                try {
                    Connection connection = Campfire.getStorage().getConnection();
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM map_copyright WHERE map_id = ?;");
                    statement.setInt(1, copyright.getMapId());

                    try (connection; statement) {
                        statement.executeUpdate();
                        COPYRIGHT_CACHE.remove(copyright.getMapId());
                        Campfire.getCampfire().getLogger().info("Copyright for map " + copyright.getMapId() + " expired.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        InventoryType inventoryType = event.getClickedInventory().getType();

        if (inventoryType == InventoryType.CARTOGRAPHY || inventoryType == InventoryType.CRAFTING) {
            if (Copyright.isCopyrighted(player, event.getCurrentItem())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lError: &7This map is copyrighted."));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }

        for (HumanEntity viewer : event.getViewers()) {
            if (!(viewer instanceof Player)) {
                continue;
            }

            Player player = (Player) viewer;

            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item == null) {
                    continue;
                }

                if (item.getType() != Material.FILLED_MAP) {
                    continue;
                }

                if (!Copyright.isCopyrighted(player, item)) {
                    return;
                }

                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    /**
     * Represents a map copyright.
     */
    public static final class CopyrightInfo {

        private final int mapId;
        private final UUID mapOwner;
        private long expiration;

        public CopyrightInfo(int mapId, @NotNull UUID mapOwner, long expiration) {
            this.mapId = mapId;
            this.mapOwner = mapOwner;
            this.expiration = expiration;
        }

        public int getMapId() {
            return mapId;
        }

        public @NotNull UUID getMapOwner() {
            return mapOwner;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }

        // TODO figure this out later
        private @NotNull String getTimeString(long time) {
            return (time / (1000 * 60 * 60 * 24)) + " days, " +
                    (time / 60 % 24) + " hours, and " +
                    (time % 60) + " minutes";
        }

        @Override
        public String toString() {
            return "&7Map ID: &e" + mapId + "\n" +
                    "&7Copyright holder: &e" + Bukkit.getOfflinePlayer(mapOwner).getName() + "\n" +
                    "&7Expiration: &e" + ((expiration - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)) + " days\n" +
                    "&7Status: &aCopyrighted";
        }
    }
}
