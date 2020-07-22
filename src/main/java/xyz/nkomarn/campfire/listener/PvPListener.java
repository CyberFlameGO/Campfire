package xyz.nkomarn.campfire.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;
import xyz.nkomarn.kerosene.util.message.Message;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A lightweight PvP toggling solution.
 * Checks borrowed from https://github.com/aasmus/PvPToggle.
 */
public class PvPListener implements Listener {

    public static Set<UUID> ENABLED_PLAYERS = new HashSet<>();
    private static final BaseComponent[] DISABLED = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes(
            '&', "&c&lPVP: &7Your PvP is disabled."
    ));

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(@NotNull EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (isPvPDisabled((Player) event.getDamager(), (Player) event.getEntity())) {
                event.setCancelled(true);
            }
        } else if (event.getDamager() instanceof Projectile) {
            final Projectile arrow = (Projectile) event.getDamager();
            if (arrow.getShooter() instanceof Player && event.getEntity() instanceof Player) {
                if (isPvPDisabled((Player) arrow.getShooter(), (Player) event.getEntity())) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getDamager() instanceof ThrownPotion) {
            final ThrownPotion potion = (ThrownPotion) event.getDamager();
            if (potion.getShooter() instanceof Player && event.getEntity() instanceof Player) {
                if (isPvPDisabled((Player) potion.getShooter(), (Player) event.getEntity())) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getDamager() instanceof LightningStrike) {
            if (event.getDamager().getMetadata("TRIDENT").size() >= 1 && event.getEntity() instanceof Player) {
                if (!ENABLED_PLAYERS.contains(event.getEntity().getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player) {
            event.setCancelled(true);
            LivingEntity boostedEntity = ((Firework) event.getDamager()).getBoostedEntity();

            if (boostedEntity instanceof Player) {
                if (isPvPDisabled((Player) event.getEntity(), (Player) boostedEntity)) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityCombust(@NotNull EntityCombustByEntityEvent event) {
        if (event.getCombuster() instanceof Arrow) {
            final Arrow arrow = (Arrow) event.getCombuster();
            if (arrow.getShooter() instanceof Player && event.getEntity() instanceof Player) {
                if (isPvPDisabled((Player) arrow.getShooter(), (Player) event.getEntity())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(@NotNull PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    if (isPvPDisabled((Player) event.getPotion().getShooter(), (Player) entity)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCloudEffect(@NotNull AreaEffectCloudApplyEvent event) {
        if (event.getEntity().getSource() instanceof Player) {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    if (isPvPDisabled((Player) event.getEntity().getSource(), (Player) entity)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFish(@NotNull PlayerFishEvent event) {
        if (event.getCaught() instanceof Player) {
            if (isPvPDisabled(event.getPlayer(), (Player) event.getCaught())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLightningStrike(@NotNull LightningStrikeEvent event) {
        if (event.getCause() == LightningStrikeEvent.Cause.TRIDENT) {
            event.getLightning().setMetadata("TRIDENT", new FixedMetadataValue(Campfire.getCampfire(),
                    event.getLightning().getLocation()));
        }
    }

    private boolean isPvPDisabled(@NotNull Player attacker, @NotNull Player attacked) {
        if (attacker.equals(attacked)) {
            return false;
        } else if (ENABLED_PLAYERS.contains(attacker.getUniqueId()) && ENABLED_PLAYERS.contains(attacked.getUniqueId())) {
            return false;
        } else if (ENABLED_PLAYERS.contains(attacker.getUniqueId())) {
            Message.sendActionbar(attacker, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes(
                    '&', "&c&lPVP: &7" + attacked.getName() + "'s PvP is disabled."
            )));
            return true;
        } else {
            Message.sendActionbar(attacker, DISABLED);
            return true;
        }
    }
}
