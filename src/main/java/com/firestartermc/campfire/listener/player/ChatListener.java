package com.firestartermc.campfire.listener.player;

import com.firestartermc.campfire.Campfire;
import com.firestartermc.kerosene.util.MessageUtils;
import com.firestartermc.kerosene.util.webhook.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ChatListener implements Listener {

    private static final String MUTED_PREFIX = MessageUtils.formatColors("&#ff9a8f&lSoft Muted: &#e8e8e8", true);
    private final Campfire campfire;

    public ChatListener(@NotNull Campfire campfire) {
        this.campfire = campfire;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        if (!campfire.getTextFilter().checkText(event.getMessage())) {
            return;
        }

        var player = event.getPlayer();
        var recipients = event.getRecipients();

        recipients.clear();
        recipients.add(player);
        notify(player.getName(), event.getMessage());
    }

    private void notify(@NotNull String player, @NotNull String text) {
        Bukkit.getOnlinePlayers().stream()
                .filter(online -> online.hasPermission("firestarter.staff"))
                .forEach(staff -> staff.sendMessage(MUTED_PREFIX + "(" + player + ") " + text));

        var embed = DiscordWebhook.Embed.builder()
                .description("⚡ Filter alert: `" + player + "`")
                .addField("Message", "`" + text + "`", true)
                .color(Color.ORANGE)
                .build();
        DiscordWebhook.create(campfire.getConfig().getString("webhooks.moderation"))
                .embed(embed)
                .queue();
    }
}
