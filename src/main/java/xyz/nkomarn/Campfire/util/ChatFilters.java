package xyz.nkomarn.Campfire.util;

import java.awt.*;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * TODO
 */
public class ChatFilters {
    public static final Pattern nWord = Pattern.compile("([n5]+(\\W|\\d|_)*[i1!l¡]+(\\W|\\d|_)*[g96q]+(\\W|\\d|_)*[e3]+(\\W|\\d|_)*[r®]+)");

    public static boolean stringMatchesFilters(String text) {
        return nWord.matcher(text.replace(" ", "")).find();
    }

    public static void notifyStaff(String playerName, String text) {
        Webhooks hook = new Webhooks(Config.getString("webhooks.moderation"));
        hook.addEmbed(new Webhooks.EmbedObject()
                .setTitle(":cloud_lightning: Chat filter alert")
                .addField("Player", playerName, false)
                .addField("Message", text, false)
                .setColor(Color.ORANGE));
        try {
            hook.execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
