package xyz.nkomarn.Campfire.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Utility class to manage the player list ranks
 * using scoreboard teams.
 */
public class Ranks {
    private static Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

    // Get the scoreboard team the player should be in based on permission groups
    private static String getRank(Player player) {
        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();
        Iterator<PermissionAttachmentInfo> iterator = permissions.iterator();
        ArrayList<String> groups = new ArrayList<>();

        while (iterator.hasNext()) {
            PermissionAttachmentInfo permissionAttachmentInfo = iterator.next();
            if (!permissionAttachmentInfo.getPermission().startsWith("group.")) continue;
            groups.add(permissionAttachmentInfo.getPermission());
        }

        return getHighestPriority(groups);
    }

    // Return the highest priority team out of a list of permission groups
    private static String getHighestPriority(ArrayList<String> groups) {
        List<String> priority = Config.getList("tablist.priority");
        for (String s : priority) {
            if (groups.contains("group." + s)) return s;
        }
        return "default"; // Otherwise, set player's group as the default group
    }

    // Add a player to a scoreboard team
    public static void addToTeam(Player player) {
        String rank = getRank(player);
        List<String> priority = Config.getList("tablist.priority");
        String teamName = priority.indexOf(rank) + rank;
        Team team = scoreboard.getTeam(teamName);

        // Create team if it doesn't exist and add the player to the team
        if (team == null) {
            ChatColor chatColor = ChatColor.valueOf(getColor(rank));
            team = scoreboard.registerNewTeam(teamName);
            team.setColor(chatColor);
            team.setPrefix(ChatColor.translateAlternateColorCodes('&', getPrefix(rank)));
        }
        team.addPlayer(player);
    }

    // Returns the amount of vanished players currently online
    public static int getVanishedPlayers() {
        int vanished = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (MetadataValue value : player.getMetadata("vanished")) {
                if (value.asBoolean()) vanished++;
            }
        }
        return vanished;
    }

    // Return the color of a tablist team
    private static String getColor(String group) {
        return Config.getString("tablist.groups." + group + ".color");
    }

    // Return the prefix of a tablist team
    private static String getPrefix(String group) {
        return Config.getString("tablist.groups." + group + ".prefix");
    }
}
