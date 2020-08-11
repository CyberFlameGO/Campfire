package xyz.nkomarn.campfire.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.campfire.util.Copyright;
import xyz.nkomarn.kerosene.gui.predefined.ConfirmationGui;;
import xyz.nkomarn.kerosene.util.Economy;

import java.util.*;

public class CopyrightCommand implements TabExecutor {

    private static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&e&lCopyright: &7");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            ItemStack mapItem = player.getInventory().getItemInMainHand();

            if (mapItem.getType() != Material.FILLED_MAP) {
                player.sendMessage(PREFIX + "Hold a map in your hand to view its copyright information."); // TODO purchase info
                return true;
            }

            MapView mapView = ((MapMeta) mapItem.getItemMeta()).getMapView();

            if (!Copyright.isCopyrighted(mapItem) || mapView == null) {
                player.sendMessage(PREFIX + "The map in your hand is not copyrighted.");
                return true;
            }

            Copyright.getCopyright(mapView.getId()).ifPresent(copyright -> {
                String message = "&r \n&e&lCopyright Information:\n&r \n" + copyright.toString() + "\n&r ";
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            });
        } else if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("purchase")) {
            ItemStack mapItem = player.getInventory().getItemInMainHand();

            if (mapItem.getType() != Material.FILLED_MAP) {
                player.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&',
                        "Hold a map in your hand to purchase &e+ 60 days&7 of copyright for &e$6,000&7."
                ));
                return true;
            }

            if (Copyright.isCopyrighted(player, mapItem)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lError: &7This map is already copyrighted."));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }

            if (Economy.getBalance(player) < 6000) {
                player.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', "You need &e$6,000&7 to purchase copyright."));
                return true;
            }

            ConfirmationGui confirm = new ConfirmationGui("Purchase 60 days of map copyright for $6,000.",
                    (event) -> {
                        int mapId = ((MapMeta) mapItem.getItemMeta()).getMapView().getId();

                        Copyright.copyrightMap(mapId, player).thenAccept(result -> {
                            if (!result) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lError: &7Failed to copyright your map."));
                                return;
                            }

                            Economy.withdraw(player, 6000);
                            player.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', "Purchased copyright for &e+ 60 days&7 for this map."));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1.0f, 1.0f);
                            Campfire.getCampfire().getLogger().info(player.getName() + " copyrighed map #" + mapId + ".");
                        });

                        event.getGui().close();
                    },
                    (event) -> event.getGui().close());

            confirm.open(player);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("buy", "purchase");
        }

        return new ArrayList<>();
    }
}
