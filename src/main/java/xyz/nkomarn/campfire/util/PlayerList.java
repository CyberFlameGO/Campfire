package xyz.nkomarn.campfire.util;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.kerosene.Kerosene;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to manage the player list and ranks using scoreboard teams.
 */
public class PlayerList {

    private static final String HEADER = ChatColor.translateAlternateColorCodes('&', Config.getString("tablist.header"));
    private static final Scoreboard SCOREBOARD = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    private static final List<String> TEAM_PRIORITY = Config.getConfig().getStringList("tablist.priority");

    /**
     * Get the scoreboard team that the player should be part of based on permissions.
     *
     * @param player The player to get the team for.
     * @return The team name that the player should be a part of.
     */
    private static String getTeam(@NotNull Player player) {
        return getHighestPriority(player.getEffectivePermissions().parallelStream()
                .filter(permission -> permission.getPermission().startsWith("group."))
                .map(PermissionAttachmentInfo::getPermission)
                .collect(Collectors.toSet()));
    }

    /**
     * Returns the highest priority team out of a set of teams.
     *
     * @param teams A set of teams that a player is a part of.
     * @return The highest priority group out of the set.
     */
    private static String getHighestPriority(@NotNull Set<String> teams) {
        return TEAM_PRIORITY.stream()
                .filter(team -> teams.contains("group." + team))
                .findFirst().orElse("default");
    }

    /**
     * Adds a player to their correct scoreboard team based on permissions.
     *
     * @param player The player for which to update teams.
     */
    public static void updateTeams(@NotNull Player player) {
        String teamName = getTeam(player);
        String configName = TEAM_PRIORITY.indexOf(teamName) + teamName;
        Team team = SCOREBOARD.getTeam(configName);

        if (team == null) {
            team = SCOREBOARD.registerNewTeam(configName);
            team.setColor(ChatColor.valueOf(Config.getString("tablist.teams." + teamName + ".color")));
            team.setPrefix(ChatColor.translateAlternateColorCodes('&', Config.getString("tablist.teams." + teamName + ".prefix")));
        }

        team.addEntry(player.getName());
    }

    /**
     * Returns the current online player count, adjusted for vanished players.
     *
     * @return Current adjusted online player count.
     */
    public static int getAdjustedPlayerCount() {
        return Bukkit.getOnlinePlayers().size() - VanishAPI.getInvisiblePlayers().size();
    }

    /**
     * Updates the header with the amount of currently online players.
     */
    public static void updateHeader() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Campfire.getCampfire(), () -> {
            String header = String.format(HEADER, getAdjustedPlayerCount());
            Bukkit.getOnlinePlayers().forEach(player -> player.setPlayerListHeader(header));
        }, 5L);
    }
}
