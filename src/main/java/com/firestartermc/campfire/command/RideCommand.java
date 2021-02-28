package com.firestartermc.campfire.command;

import com.firestartermc.campfire.Campfire;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

public class RideCommand implements CommandExecutor, Listener {

    private static final String PREFIX = MessageUtils.formatColors("&#fffa69&lRIDE: &f", true);
    private static final String RIDING_SPACER = "ride_stack_spacer";

    public RideCommand(@NotNull Campfire campfire) {
        campfire.getServer().getPluginManager().registerEvents(this, campfire);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        var player = (Player) sender;
        var target = player.getTargetEntity(4, false);

        if (!(target instanceof Player)) {
            sender.sendMessage(PREFIX + "Look at a player to ride them!");
            return true;
        }

        if (target.getPassengers().size() > 0) {
            sender.sendMessage(PREFIX + target.getName() + " is already being ridden.");
            return true;
        }

        var spacer = (Silverfish) player.getWorld().spawnEntity(player.getLocation(), EntityType.SILVERFISH);
        spacer.setInvisible(true);
        spacer.setSilent(true);
        spacer.setAI(false);

        target.addPassenger(spacer);
        spacer.addPassenger(player);
        return true;
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getDismounted().getType() == EntityType.SILVERFISH) {
            event.getDismounted().remove();
        }
    }
}

