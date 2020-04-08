package xyz.nkomarn.Campfire.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.nkomarn.Kerosene.util.AdvancementUtil;

public class PlayerCommandPreProcessListener implements Listener {
    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] message = event.getMessage().split("\\s+");
        final String command = message[0];

        // Alternate syntax blocking
        if (!player.isOp() && command.contains(":")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lError: &7Invalid syntax."));
            return;
        }

        if (command.equalsIgnoreCase("/ah")) {
            if (message.length < 2) {
                AdvancementUtil.grantAdvancement(event.getPlayer(), "ah-menu");
            }
        } else if (command.equalsIgnoreCase("/hdb") || command.equalsIgnoreCase("/heads")) {
            if (message.length < 2) {
                AdvancementUtil.grantAdvancement(event.getPlayer(), "heads");
            }
        } else if (command.equalsIgnoreCase("/claim")) {
            event.setCancelled(true);
            player.chat("/kit claim");
        } else if (command.equalsIgnoreCase("/op")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lError: &7This command is disabled for security."));
        } else if (command.equalsIgnoreCase("/pl") || command.equalsIgnoreCase("/plugins")) {
            player.sendMessage("We're proudly open-source! Check out our GitHub at https://github.com/firestartermc."); // TODO replace with the new URL
        }
    }
}
