package xyz.nkomarn.campfire.util;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class Claims {

    private static final DataStore DATA_STORE = GriefPrevention.instance.dataStore;

    private Claims() {
    }

    public static DataStore getDataStore() {
        return DATA_STORE;
    }

    public static boolean checkForeignClaims(@NotNull OfflinePlayer player, @NotNull Location location) {
        Collection<Claim> claims = DATA_STORE.getClaims(location.getChunk().getX(), location.getChunk().getZ());

        if (claims.size() > 0) {
            for (Claim claim : claims) {
                if (!claim.ownerID.equals(player.getUniqueId())) {
                    if (player.isOnline() && claim.allowBuild((Player) player, Material.AIR) != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
