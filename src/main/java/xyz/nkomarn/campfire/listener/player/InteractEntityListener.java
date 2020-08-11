package xyz.nkomarn.campfire.listener.player;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.campfire.task.PortalTask;
import xyz.nkomarn.kerosene.util.player.PlayerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InteractEntityListener implements Listener {

    private final HashMap<UUID, Long> cooldowns;

    public InteractEntityListener() {
        cooldowns = new HashMap<>();
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

        /*long cooldown = cooldowns.get(interacted.getUniqueId());
        if (cooldown != 0 && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cooldown) < 5) {
            return;
        }*/

        switch (interacted.getUniqueId().toString()) {
            case "9b6edc28-2ca3-4ede-82b6-777116812905": // iriscow
                PlayerInventory playerInventory = player.getInventory();

                if (playerInventory.getItemInMainHand().getType() != Material.BUCKET) {
                    return;
                }

                playerInventory.getItemInMainHand().subtract();
                PlayerUtil.giveOrDropItem(player, new ItemStack(Material.MILK_BUCKET, 1));
                break;
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
                world.playSound(location, Sound.ITEM_AXE_STRIP, 1.0f, 1.0f);
                world.dropItemNaturally(location, new ItemStack(Material.STICK, 1));
                break;
            case "db7bfc04-7967-42ea-a543-0876fdd85758": // AwfulEagle
                world.playSound(location, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                world.dropItemNaturally(location, new ItemStack(Material.EGG, 1));
        }
    }

    @EventHandler // TODO remove once 1.16 villager crash has been fixed
    public void onInventory(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            if (event.getInventory().getHolder() instanceof Villager) {
                Villager villager = (Villager) event.getInventory().getHolder();

                if (villager.getProfession() == Villager.Profession.CARTOGRAPHER) {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&c&lError: &7Cartographers are currently disabled as trading with them causes server crashes."
                    ));
                    event.setCancelled(true);
                    event.getPlayer().closeInventory();

                    if (event.getPlayer() instanceof Player) {
                        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    }
                }
            }
        }
    }
}
