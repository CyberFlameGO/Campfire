package xyz.nkomarn.campfire.command;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nkomarn.kerosene.util.item.ItemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FindShopCommand implements TabExecutor {

    private static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&6&lShops: &7");
    private static final HashMap<String, Material> ITEM_TYPES = new HashMap<>();
    private static final HashMap<String, Enchantment> ENCHANTMENT_TYPES = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            //player.sendMessage(PREFIX + "Use " + ChatColor.GOLD + "/findshop <item>" + ChatColor.GRAY + " to locate a shop that sells your desired item.");
            return true;
        }

        String itemName = combineArgs(args);
        System.out.println(itemName);

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 1) {
            return ImmutableList.of();
        }

        List<String> results = new ArrayList<>();
        String name = combineArgs(args).toLowerCase();

        for (String typeName : ITEM_TYPES.keySet()) {
            if (typeName.toLowerCase().contains(name)) {
                results.add(typeName);
            }
        }

        for (String typeName : ENCHANTMENT_TYPES.keySet()) {
            if (typeName.toLowerCase().contains(name)) {
                results.add(typeName);
            }
        }

        return results;
    }

    private String combineArgs(@NotNull String[] args) {
        StringBuilder output = new StringBuilder();
        for (String arg : args) {
            output.append(arg).append(" ");
        }

        return output.toString().trim().toLowerCase();
    }

    static {
        /*for (Material type : Material.values()) {
            ITEM_TYPES.put(ItemUtils.getFriendlyName(type), type);
        }

        for (Enchantment type : Enchantment.values()) { // TODO friendly names
            ENCHANTMENT_TYPES.put(WordUtils.capitalize(type.getKey().getKey().toLowerCase().replace("_", " ")), type);
        }*/
    }
}
