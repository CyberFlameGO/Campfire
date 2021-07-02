package com.firestartermc.campfire.command;

import com.firestartermc.campfire.Campfire;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

public class NyoomCommand implements CommandExecutor, Listener {

    private final String NYOOM_METADATA = "nyoomolotl";
    // private final MetadataValue NYOOM_METADATA = new FixedMetadataValue(JavaPlugin.getPlugin(Campfire.class), "nyoomolotl");
    private final Campfire campfire;
    private final ItemStack frostBoots;

    public NyoomCommand(@NotNull Campfire campfire) {
        this.campfire = campfire;
        campfire.getServer().getPluginManager().registerEvents(this, campfire);

        this.frostBoots = ItemBuilder.of(Material.LEATHER_BOOTS)
                .enchant(Enchantment.FROST_WALKER, 2)
                .build();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        var player = (Player) sender;

        if (player.isInsideVehicle()) {
            player.sendMessage("Can't go nyoom whilst riding an entity.");
            return true;
        }

        var axolotl = (Axolotl) player.getWorld().spawnEntity(player.getLocation(), EntityType.AXOLOTL);
        axolotl.addPotionEffect(PotionEffectType.SPEED.createEffect(99999, 50));
        axolotl.getEquipment().setBoots(frostBoots.clone());
        axolotl.addPassenger(player);
        axolotl.setMetadata(NYOOM_METADATA, new FixedMetadataValue(campfire, true));

        player.sendMessage("Nyoom!");
        return true;
    }

    @EventHandler
    public void onBucket(PlayerInteractEntityEvent event) {
        var item = event.getPlayer().getEquipment().getItem(event.getHand());

        if (item.getType() != Material.WATER_BUCKET) {
            return;
        }

        var entity = event.getRightClicked();

        if (entity.getType() == EntityType.AXOLOTL && entity.hasMetadata(NYOOM_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        var entity = event.getDismounted();

        if (entity.getType() == EntityType.AXOLOTL && entity.hasMetadata(NYOOM_METADATA)) {
            entity.remove();
        }
    }
}

