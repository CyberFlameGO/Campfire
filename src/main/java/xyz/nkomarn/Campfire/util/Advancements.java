package xyz.nkomarn.Campfire.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

public class Advancements {
    public static boolean isComplete(Player player, String key) {
        NamespacedKey advancementKey = new NamespacedKey("firestarter", key);
        Advancement advancement = Bukkit.getAdvancement(advancementKey);
        if (advancement == null) return true;
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        return progress.isDone();
    }
}
