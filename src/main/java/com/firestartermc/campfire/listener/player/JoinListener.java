package com.firestartermc.campfire.listener.player;

import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.util.webhook.DiscordWebhook;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import com.firestartermc.campfire.Campfire;

import java.awt.*;
import java.io.IOException;
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

                    TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lShops: &7You " +
                            (earnings > 0 ? "earned" : "lost") + " &6$" + FORMAT.format(earnings) + "&7 while you were away (/shoplog)."));
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shoplog"));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Click to view details.")));
                    player.spigot().sendMessage(message);
                });
            } else {
                var embed = DiscordWebhook.Embed.builder()
                        .description(":checkered_flag: " + event.getPlayer().getName() + " joined!")
                        .color(Color.WHITE)
                        .build();

                DiscordWebhook.create(campfire.getConfig().getString("webhooks.notifications"))
                        .embed(embed)
                        .queue();
            }
        });
    }

}
