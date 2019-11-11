package xyz.nkomarn.Wildfire.event;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.nkomarn.Wildfire.Wildfire;
import xyz.nkomarn.Wildfire.util.Config;
import xyz.nkomarn.Wildfire.util.Ranks;
import xyz.nkomarn.Wildfire.util.Webhooks;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class PlayerEvent implements Listener {

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {

        // Claim command override
        if (e.getMessage().split("\\s+")[0].equalsIgnoreCase("/claim")) {
            e.setCancelled(true);
            e.getPlayer().chat("/kit claim");
        }

        // Alternate syntax blocking (/minecraft:whatever)
        if (!e.getPlayer().isOp() && e.getMessage().split("\\s+")[0].contains(":")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8&l(&c&l!&8&l) &cInvalid syntax."));
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
    public void onDeath(PlayerDeathEvent e) {
        Wildfire.playerData.sync().updateOne(Filters.eq("uuid", e.getEntity().getUniqueId().toString()),
                new Document("$inc", new BasicDBObject().append("deaths", 1)));
    }

}
