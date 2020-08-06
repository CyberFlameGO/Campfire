package xyz.nkomarn.campfire.task;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.campfire.Campfire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PhantomTask implements Runnable {

    private final Server server;
    private final World overworld;

    private final List<FireworkEffect> effects;

    public PhantomTask(@NotNull Server server) {
        this.server = server;
        this.overworld = server.getWorld("world");
        this.effects = new ArrayList<>();

        this.effects.add(FireworkEffect.builder()
                .withColor(Color.LIME)
                .flicker(true)
                .with(FireworkEffect.Type.BURST)
                .build());

        this.effects.add(FireworkEffect.builder()
                .withColor(Color.TEAL)
                .flicker(true)
                .with(FireworkEffect.Type.BURST)
                .build());

        this.effects.add(FireworkEffect.builder()
                .withColor(Color.BLUE)
                .flicker(true)
                .with(FireworkEffect.Type.BURST)
                .build());
    }

    @Override
    public void run() {
        for (Player player : overworld.getPlayers()) {
            if (!player.hasPermission("campfire.perks.phantom-protection")) {
                continue;
            }

            protectPlayer(player);
        }
    }

    private void protectPlayer(@NotNull Player player) {
        for (Entity entity : player.getNearbyEntities(25, 25, 25)) {
            if (entity.getType() != EntityType.PHANTOM) {
                continue;
            }

            if (entity.getCustomName() != null) {
                continue;
            }

            Phantom phantom = (Phantom) entity;
            phantom.setAI(false);

            Collections.shuffle(effects);
            Firework firework = (Firework) phantom.getWorld().spawnEntity(phantom.getLocation().add(0.5, 0, 0.5), EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffect(FireworkEffect.builder()
                    .withColor(Color.WHITE)
                    .with(FireworkEffect.Type.BURST)
                    .withFade(Color.GRAY)
                    .build()
            );
            fireworkMeta.addEffect(effects.get(0));
            fireworkMeta.setPower(ThreadLocalRandom.current().nextInt(1, 4));
            firework.setFireworkMeta(fireworkMeta);

            firework.detonate();
            overworld.spawnParticle(Particle.TOTEM, phantom.getLocation(), 3);
            overworld.playSound(phantom.getLocation(), Sound.ITEM_TRIDENT_HIT_GROUND, 1.0f, 1.0f);
            phantom.damage(20);
        }
    }
}
