package xyz.nkomarn.campfire.listener.player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.log.ShopLog;
import xyz.nkomarn.campfire.util.Config;
import xyz.nkomarn.kerosene.Kerosene;
import xyz.nkomarn.kerosene.util.webhook.DiscordWebhook;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class JoinListener implements Listener {

    private static final String FOOTER = ChatColor.translateAlternateColorCodes('&', Config.getString("tablist.footer"));
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.US);

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Kerosene.getPool().submit(() -> {
            if (player.hasPlayedBefore()) {
                ShopLog.getTotalEarnings(player).thenAccept(earnings -> {
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
                DiscordWebhook hook = new DiscordWebhook(Config.getString("webhooks.notifications"));
                hook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setDescription(":checkered_flag: " + event.getPlayer().getName() + " joined!")
                        .setColor(Color.WHITE)
                );

                try {
                    hook.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
