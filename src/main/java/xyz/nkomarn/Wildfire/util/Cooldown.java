package xyz.nkomarn.Wildfire.util;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import xyz.nkomarn.Wildfire.Wildfire;

public class Cooldown {
    public static long getCooldown(String uuid, String type) {
        Bson filter = Filters.eq("uuid", uuid);
        Document player = Wildfire.playerData.sync().find(filter).first();
        if (!player.containsKey("cooldowns")) {
            resetCooldown(uuid, type);
            return 0;
        }
        Document cooldowns = (Document) player.get("cooldowns");
        return (!cooldowns.containsKey(type)) ? 0 : cooldowns.getLong(type);
    }

    public static void resetCooldown(String uuid, String type) {
        Bson filter = Filters.eq("uuid", uuid);
        Bson update = new Document("$set", new Document().append("cooldowns",
                new Document().append(type, System.currentTimeMillis())));
        UpdateOptions options = new UpdateOptions().upsert(true);
        Wildfire.playerData.sync().updateOne(filter, update, options);
    }
}
