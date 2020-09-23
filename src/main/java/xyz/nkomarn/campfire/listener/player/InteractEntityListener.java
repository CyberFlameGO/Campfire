package xyz.nkomarn.campfire.listener.player;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.kerosene.util.player.PlayerUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InteractEntityListener implements Listener {

    private final HashMap<UUID, Long> cooldowns;

    public InteractEntityListener() {
        this.cooldowns = new HashMap<>();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(@NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        Player interacted = (Player) event.getRightClicked();
        World world = interacted.getWorld();
        Location location = interacted.getLocation();

        long cooldown = cooldowns.get(interacted.getUniqueId());
        if (cooldown != 0 && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cooldown) < 5) {
            return;
        }

        switch (interacted.getUniqueId().toString()) {
            case "dd96e851-b3e8-4b0f-8618-29ec734251c6": // goofydev
                world.playSound(location, Sound.ENTITY_DOLPHIN_AMBIENT_WATER, 1.0f, 1.0f);
                world.spawnParticle(Particle.WATER_SPLASH, location, 3);
                break;
            case "ea416375-37d0-4344-b1ea-f6acb370d21b": // Th3SkullR34p3r
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 15, true, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 15, true, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 15, true, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 15, true, false));

                player.playSound(location, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f, 1.0f);
                PlayerUtil.knockback(player, 1, 1);
                break;
            case "375cd155-01a0-4382-8e25-0d6a6e62d1b6": // Meorah
                world.playSound(location, Sound.ENTITY_CAT_AMBIENT, 1.0f, 1.0f);
                break;
            case "d7043da0-13dd-491d-b4b4-3a5a8ac01b59": // DasBaumJunge
                cooldowns.put(interacted.getUniqueId(), System.currentTimeMillis());
                world.playSound(location, Sound.ITEM_AXE_STRIP, 1.0f, 1.0f);
                world.dropItemNaturally(location, new ItemStack(Material.STICK, 1));
                break;
            case "db7bfc04-7967-42ea-a543-0876fdd85758": // AwfulEagle
                cooldowns.put(interacted.getUniqueId(), System.currentTimeMillis());
                world.playSound(location, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                world.dropItemNaturally(location, new ItemStack(Material.EGG, 1));
                break;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }

}
