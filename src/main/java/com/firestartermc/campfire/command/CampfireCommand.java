package com.firestartermc.campfire.command;

import com.firestartermc.kerosene.util.AdvancementUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import com.firestartermc.campfire.Campfire;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class CampfireCommand implements TabExecutor {

    private final Campfire campfire;

    public CampfireCommand(Campfire campfire) {
        this.campfire = campfire;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1 || !sender.isOp()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCampfire: &7A package of Firestarter's custom features."));
        } else if (args[0].equalsIgnoreCase("shutdown")) {
            shutdown();
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lError: &7Invalid operation specified."));
        }
        return true;
    }

    private void shutdown() {
        Bukkit.getServer().setWhitelist(true);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.isOp())
                .forEach(player -> player.kickPlayer("The server is undergoing a reboot. Hang tight."));
        Bukkit.getScheduler().runTaskLater(campfire, Bukkit::shutdown,20 * 10L);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) return null;

        if (args.length == 1) {
            final List<String> subCommands = Arrays.asList("shutdown", "updatelist");
            Collections.sort(subCommands);
            return subCommands;
        } else if(args.length == 2) {
            if (args[0].equalsIgnoreCase("createmap")) {
                return Collections.singletonList("<image_url>");
            }

            if(args[0].equalsIgnoreCase("setdonor")) {
                return null;
            }
        }
        return Collections.emptyList();
    }
}
