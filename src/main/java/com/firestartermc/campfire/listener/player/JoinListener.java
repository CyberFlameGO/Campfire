package com.firestartermc.campfire.listener.player;

import com.firestartermc.kerosene.util.webhook.DiscordWebhook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import com.firestartermc.campfire.Campfire;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ForkJoinPool;

public class JoinListener implements Listener {

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private final Campfire campfire;

    public JoinListener(Campfire campfire) {
        this.campfire = campfire;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ForkJoinPool.commonPool().submit(() -> {
            if (player.hasPlayedBefore()) {
                campfire.getShopLog().getTotalEarnings(player).thenAccept(earnings -> {
                    if (earnings == 0) {
                        return;
                    }

                    var action = earnings > 0 ? "earned" : "lost";
                    var money = "$" + FORMAT.format(earnings);
                    var component = Component.text("&6&lSHOPS: &eYou " + action + " " + money + " while you were away.")
                            .clickEvent(ClickEvent.runCommand("/shoplog"))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to view details")));

                    player.sendMessage(component);
                });
            } else {
                var embed = DiscordWebhook.Embed.builder()
                        .description(":checkered_flag: `" + event.getPlayer().getName() + "` joined!")
                        .color(Color.WHITE)
                        .build();

                DiscordWebhook.create(campfire.getConfig().getString("webhooks.notifications"))
                        .embed(embed)
                        .queue();
            }
        });
    }

}
