package xyz.nkomarn.Campfire.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to manage the player list ranks
 * using scoreboard teams.
 */
public class Teams {
    private static final Scoreboard SCOREBOARD = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    private static final List<String> TEAM_PRIORITY = Config.getConfig().getStringList("tablist.priority");

    /**
     * Get the scoreboard team that the player should be part of based on permissions.
     * @param player The player to get the team for.
     * @return The team name that the player should be a part of.
     */
    private static String getTeam(Player player) {
        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();
        return getHighestPriority(permissions.parallelStream()
                .filter(permission -> permission.getPermission().startsWith("group."))
                .map(PermissionAttachmentInfo::getPermission)
                .collect(Collectors.toSet()));
    }

    /**
     * Returns the highest priority team out of a set of teams.
     * @param teams A set of teams that a player is a part of.
     * @return The highest priority group out of the set.
     */
    private static String getHighestPriority(Set<String> teams) {
        return TEAM_PRIORITY.stream()
                .filter(team -> teams.contains("group." + team))
                .findFirst().orElse("default");
    }

    /**
     * Adds a player to their correct scoreboard team based on permissions.
     * @param player The player for which to update teams.
     */
    public static void updateTeams(Player player) {
        String team = getTeam(player);
        String teamName = TEAM_PRIORITY.indexOf(team) + team;
        Team scoreboardTeam = SCOREBOARD.getTeam(teamName);

        if (scoreboardTeam == null) {
            scoreboardTeam = SCOREBOARD.registerNewTeam(teamName);
            scoreboardTeam.setColor(ChatColor.valueOf(Config.getString(String.format("tablist.teams.%s.color", team))));
            scoreboardTeam.setPrefix(ChatColor.translateAlternateColorCodes('&',
                    String.format("tablist.teams.%s.prefix", team)));
        }
        scoreboardTeam.addEntry(player.getName());
    }
}