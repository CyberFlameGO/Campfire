package xyz.nkomarn.Campfire.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.Border;
import xyz.nkomarn.Campfire.util.Config;

/**
 * A repeating task that makes sure everyone online
 * is within the artificial border set for each world.
 */
public class BorderCheck implements Runnable {
    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.hasPermission("campfire.admin") && Border.isLocationOutsideBorder(player.getLocation())) {
                if (player.isInsideVehicle()) {
                    player.leaveVehicle();
                }

                Bukkit.getScheduler().callSyncMethod(Campfire.getCampfire(), () -> Bukkit.getServer()
                        .dispatchCommand(Bukkit.getConsoleSender(), String.format("spawn %s", player.getName())));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.getString("messages.border")));
            }
        });
    }
}
