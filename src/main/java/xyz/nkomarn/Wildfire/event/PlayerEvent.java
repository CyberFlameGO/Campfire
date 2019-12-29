package xyz.nkomarn.Wildfire.event;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import net.minecraft.server.v1_15_R1.TileEntity;
import net.minecraft.server.v1_15_R1.TileEntityBeehive;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Wildfire.Wildfire;
import xyz.nkomarn.Wildfire.util.Config;
import xyz.nkomarn.Wildfire.util.Ranks;
import xyz.nkomarn.Wildfire.util.Webhooks;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class PlayerEvent implements Listener {

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {

        Player player = e.getPlayer();
        String[] args = e.getMessage().split("\\s+");

        // Claim command override
        if (args[0].equalsIgnoreCase("/claim")) {
            e.setCancelled(true);
            player.chat("/kit claim");
        }

        // Custom enchant command (overpowered e-books)
        if (args[0].equalsIgnoreCase("/enchant")) {
            if (player.isOp()) return;
            e.setCancelled(true);

            // Get items in hands

        }

        // Alternate syntax blocking (/minecraft:whatever)
        if (!e.getPlayer().isOp() && e.getMessage().split("\\s+")[0].contains(":")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lError: &7Invalid syntax."));
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // Greet the player with a title message
        if (player.hasPlayedBefore()) {
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Config.getString("title.top")),
                    ChatColor.translateAlternateColorCodes('&', Config.getString("title.bottom")));
        }

        // Create player data document if the player is new
        if (!player.hasPlayedBefore()) {
            Document playerData = new Document("uuid", player.getUniqueId().toString())
                    .append("joined", System.currentTimeMillis())
                    .append("playtime", 0)
                    .append("deaths", 0)
                    .append("votes", 0)
                    .append("backpack", "");
            Wildfire.playerData.sync().insertOne(playerData);

            // Send a webhook in the Discord channel
            Bukkit.getScheduler().runTaskAsynchronously(Wildfire.instance, () -> {
                Webhooks hook = new Webhooks(Config.getString("webhook.notifications"));
                hook.addEmbed(new Webhooks.EmbedObject()
                        .setDescription(":checkered_flag: " + player.getName() + " joined!")
                        .setColor(Color.WHITE));
                try {
                    hook.execute();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }

        // Update tablist header for everyone online
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setPlayerListHeader(ChatColor.translateAlternateColorCodes('&', Config.getString("tablist.header")
                    .replace("[online]", String.valueOf(Bukkit.getOnlinePlayers().size()))));
        }

        // Set tablist footer
        player.setPlayerListFooter(ChatColor.translateAlternateColorCodes('&',
                Config.getString("tablist.footer")));

        // Update tablist teams
        Ranks.addToTeam(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

        // Update tablist header for everyone online
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setPlayerListHeader(ChatColor.translateAlternateColorCodes('&', Config.getString("tablist.header")
                    .replace("[online]", String.valueOf(Bukkit.getOnlinePlayers().size() - 1))));
        }

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lF"),
                ChatColor.GOLD + "/back to go back to your death location.");
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final ItemStack pickedUpItem = event.getItem().getItemStack();
        final Material itemType = pickedUpItem.getType();
        if (itemType != Material.BEE_NEST && itemType != Material.BEEHIVE) return;

        final net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(pickedUpItem);
        int beeCount = 0;

        try {
            final String nmsContent = nmsItemStack.getTag().toString();
            final String query = "HasStung:";
            for (int i = 0; i < nmsContent.length(); i++) {
                if (nmsContent.substring(i).startsWith(query)) beeCount++;
            }
        } catch (Exception exception) {
            beeCount = 0;
        }

        ItemMeta itemMeta = pickedUpItem.getItemMeta();
        itemMeta.setLore(Collections.singletonList(ChatColor.GOLD + String.format("%s bees", beeCount)));
        pickedUpItem.setItemMeta(itemMeta);
        event.getItem().setItemStack(pickedUpItem);
    }
}
