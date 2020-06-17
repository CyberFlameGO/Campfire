package xyz.nkomarn.Campfire.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack stack;

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder withMaterial(Material material) {
        this.stack.setType(material);
        return this;
    }

    public ItemBuilder withAmount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }

    public ItemBuilder withDisplayName(String displayName) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.setDisplayName(displayName);
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withLore(List<String> lore) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.setLore(lore);
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withLore(String... lore) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withDamage(int damage) {
        Damageable damageable = (Damageable) this.stack.getItemMeta();
        damageable.setDamage(damage);
        this.stack.setItemMeta((ItemMeta) damageable);
        return this;
    }

    public ItemBuilder withEnchantment(Enchantment enchantment, int level) {
        this.stack.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder withUnsafeEnchantment(Enchantment enchantment, int level) {
        this.stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    // TODO player & texture skull

    public ItemBuilder withPlayerSkull(OfflinePlayer player) {
        SkullMeta meta = (SkullMeta) this.stack.getItemMeta();
        meta.setOwningPlayer(player);
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withSkullTexture(String texture) {
        SkullMeta meta = (SkullMeta) this.stack.getItemMeta();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", texture));

        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withColor(Color color) {
        ItemMeta meta = this.stack.getItemMeta();

        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);
        } else if(meta instanceof PotionMeta) {
            ((PotionMeta) meta).setColor(color);
        }

        this.stack.setItemMeta(meta);
        return this;
    }


    public ItemStack build() {
        return this.stack;
    }

    public static ItemBuilder of(ItemStack stack) {
        return new ItemBuilder(stack);
    }

}
