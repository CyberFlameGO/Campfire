package com.firestartermc.campfire.listener.player;

import com.firestartermc.kerosene.util.ConcurrentUtils;
import com.firestartermc.kerosene.util.webhook.DiscordWebhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import com.firestartermc.campfire.Campfire;

import java.awt.*;

public class JoinListener implements Listener {

    private final Campfire campfire;

    public JoinListener(Campfire campfire) {
        this.campfire = campfire;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        var player = event.getPlayer();

        if (player.hasPlayedBefore()) {
            return;
        }

        ConcurrentUtils.callAsync(() -> {
            var embed = DiscordWebhook.Embed.builder()
                    .description(":checkered_flag: `" + player.getName() + "` joined!")
                    .color(Color.WHITE)
                    .build();

            DiscordWebhook.create(campfire.getConfig().getString("webhooks.notifications"))
                    .embed(embed)
                    .queue();
        });
    }
}
