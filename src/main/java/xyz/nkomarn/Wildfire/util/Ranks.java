package xyz.nkomarn.Wildfire.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Ranks {

    private static Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

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

    private static String getRank(Player player) {

        // Get all of a player's permissions
        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();
        Iterator<PermissionAttachmentInfo> iterator = permissions.iterator();
        ArrayList<String> groups = new ArrayList<>();

        // Get all groups that the player is part of
        while (iterator.hasNext()) {
            PermissionAttachmentInfo permissionAttachmentInfo = iterator.next();
            if (!permissionAttachmentInfo.getPermission().startsWith("group.")) continue;
            groups.add(permissionAttachmentInfo.getPermission());
        }

        // Get the highest priority group
        return getHighestPriority(groups);
    }

    private static String getHighestPriority(ArrayList<String> groups) {
        List<String> priority = Config.getList("tablist.priority");
        for (String s : priority) {
            if (groups.contains("group." + s)) return s;
        }
        return "default"; // Otherwise, set player's group as default
    }

    private static String getColor(String rank) {
        return Config.getString("tablist.groups." + rank + ".color");
    }

    private static String getPrefix(String rank) {
        return Config.getString("tablist.groups." + rank + ".prefix");
    }
}
