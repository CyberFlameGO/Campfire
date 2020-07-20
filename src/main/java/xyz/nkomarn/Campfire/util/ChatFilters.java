package xyz.nkomarn.Campfire.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.nkomarn.Kerosene.chat.Chat;

import java.awt.*;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * TODO
 */
public class ChatFilters {
    public static final Pattern nWord = Pattern.compile("([n5]+(\\W|\\d|_)*[i1!l¡]+(\\W|\\d|_)*[g96q]+(\\W|\\d|_)*[eë3äa]+(\\W|\\d|_)*[r®]+)");
    public static final Pattern nWordTwo = Pattern.compile("([n5]+(\\W|\\d|_)*[i1!l¡]+(\\W|\\d|_)*[g96q]+(\\W|\\d|_)*[aåä4]+)");

    public static boolean stringMatchesFilters(String text) {
        text = text.toLowerCase().replace(" ", "").replace("_", "");
        return nWord.matcher(text).find() || nWordTwo.matcher(text).find();
    }

    public static void notifyStaff(String playerName, String text) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("campfire.staff"))
                .forEach(player -> {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&4&lSoft Muted: &7(" + playerName + "&7) " + text));
                });

        Webhooks hook = new Webhooks(Config.getString("webhooks.moderation"));
        hook.addEmbed(new Webhooks.EmbedObject()
                .setDescription("⚡ Filter alert: `" + playerName + "`")
                .addField("Message", "`" + text + "`", true)
                .setColor(Color.ORANGE));
        try {
            hook.execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
