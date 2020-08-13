package xyz.nkomarn.campfire.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.campfire.util.cache.EffectsCache;
import java.util.Map;
import java.util.UUID;

/**
 * A running task to apply configured custom potion effects to online donors.
 */
public class EffectsTask implements Runnable {

    @Override
    public void run() {
        for (@NotNull Map.Entry<UUID, String[]> entry : EffectsCache.CACHE.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null) {
                EffectsCache.invalidate(entry.getKey());
                continue;
            }

            for (String effect : entry.getValue()) {
                if (effect == null) {
                    continue;
                }

                PotionEffectType effectType = PotionEffectType.getByName(effect);

                if (effectType == null) {
                    continue;
                }

                player.addPotionEffect(new PotionEffect(effectType, 450, EffectsCache.getEffectLevel(effect), false, false));
            }
        }
    }
}
