package com.firestartermc.campfire.listener.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class CommandPreProcessListener implements Listener {

    private static final String SYNTAX = ChatColor.translateAlternateColorCodes('&', "&c&lError: &7Invalid syntax.");
    private static final String DISABLED = ChatColor.translateAlternateColorCodes('&', "&c&lError: &7This command is disabled for security.");
    private static final String OPEN_SOURCE = "We're proudly open-source! Check out our GitHub at https://github.com/firestarter.";

    @EventHandler(ignoreCancelled = true)
    public void onCommandPreProcess(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] args = event.getMessage().split("\\s+");

        if (!player.isOp() && args[0].contains(":")) { 
            event.setCancelled(true);
            event.getPlayer().sendMessage(SYNTAX);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "/claim":
                event.setCancelled(true);
                player.chat("/kit claim");
                break;
            case "/op":
                event.setCancelled(true);
                event.getPlayer().sendMessage(DISABLED);
                break;
        }

        /*if (args[0].equalsIgnoreCase("/ah")) {
            if (args.length < 2) {
                AdvancementUtil.grantAdvancement(player, "ah-menu");
            }
        } else if (args[0].equalsIgnoreCase("/hdb") || args[0].equalsIgnoreCase("/heads")) {
            if (args.length < 2) {
                AdvancementUtil.grantAdvancement(player, "heads");
            }
        } else if (args[0].equalsIgnoreCase("/claim")) {
            event.setCancelled(true);
            player.chat("/kit claim");
        } else if (args[0].equalsIgnoreCase("/op")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lError: &7This command is disabled for security."));
        } else if (args[0].equalsIgnoreCase("/pl") || args[0].equalsIgnoreCase("/plugins")) {
            player.sendMessage();
        }*/
    }
}
