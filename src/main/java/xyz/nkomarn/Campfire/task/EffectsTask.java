package xyz.nkomarn.Campfire.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nkomarn.Campfire.Campfire;
import xyz.nkomarn.Campfire.util.cache.EffectsCache;
import java.util.Map;
import java.util.UUID;

public class EffectsTask implements Runnable {
    @Override
    public void run() {
        for (Map.Entry<UUID, String[]> entry : EffectsCache.CACHE.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                EffectsCache.invalidate(entry.getKey());
                continue;
            }

            for (String effect : entry.getValue()) {
                if (effect != null) {
                    PotionEffectType effectType = PotionEffectType.getByName(effect);
                    if (effectType == null) continue;
                    Bukkit.getScheduler().runTask(Campfire.getCampfire(), () -> player.addPotionEffect(
                            new PotionEffect(effectType, 450, EffectsCache.getEffectLevel(effect), true, false)));
                }
            }
        }
    }
}
