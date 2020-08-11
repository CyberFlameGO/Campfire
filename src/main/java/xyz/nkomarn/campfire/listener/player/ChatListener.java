package xyz.nkomarn.campfire.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.util.Config;
import xyz.nkomarn.campfire.util.text.Filter;
import xyz.nkomarn.kerosene.Kerosene;
import xyz.nkomarn.kerosene.util.webhook.DiscordWebhook;

import java.awt.*;
import java.io.IOException;
import java.util.Set;

public class ChatListener implements Listener {

    private static final String MUTED_PREFIX = ChatColor.translateAlternateColorCodes('&', "&4&lSoft Muted: &7");

    @EventHandler(ignoreCancelled = true)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        if (Filter.checkString(event.getMessage())) {
            Set<Player> recipients = event.getRecipients();
            recipients.clear();
            recipients.add(event.getPlayer());
            notify(event.getPlayer().getName(), event.getMessage());
        }
    }

    private void notify(@NotNull String player, @NotNull String text) {
        Kerosene.getPool().submit(() -> {
            Bukkit.getOnlinePlayers().stream()
                    .filter(online -> online.hasPermission("campfire.staff"))
                    .forEach(staff -> staff.sendMessage(MUTED_PREFIX + "(" + player + ") " + text));

            DiscordWebhook hook = new DiscordWebhook(Config.getString("webhooks.moderation"));
            hook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setDescription("⚡ Filter alert: `" + player + "`")
                    .addField("Message", "`" + text + "`", true)
                    .setColor(Color.ORANGE)
            );

            try {
                hook.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
