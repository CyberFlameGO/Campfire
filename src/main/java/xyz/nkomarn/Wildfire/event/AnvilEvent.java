package xyz.nkomarn.Wildfire.event;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.*;

public class AnvilEvent implements Listener {

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent e) {

        Inventory inventory = e.getInventory();

        if (inventory.getViewers().isEmpty()) return;
        Player player = (Player) inventory.getViewers().get(0);

        ItemStack result = e.getResult();
        if (!hasEnchantments(result)) return;

        ItemStack primary = inventory.getItem(0);
        ItemStack secondary = inventory.getItem(1);

        assert secondary != null;
        if (!secondary.getType().equals(Material.ENCHANTED_BOOK)) return;

        // Check if enchanted book is correct for the item
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) secondary.getItemMeta();
        for (Map.Entry<Enchantment,Integer> entry : bookMeta.getStoredEnchants().entrySet()) {
            if (!(entry.getKey().getItemTarget().includes(primary.getType()))) return;
        }

        Map<Enchantment, Integer> primaryEnchants = getEnchantments(primary);
        Map<Enchantment, Integer> secondaryEnchants = getEnchantments(secondary);
        ItemMeta meta = resetCost(result);

        Map<Enchantment, Integer> finalEnchants = combineEnchants(primaryEnchants, secondaryEnchants);

        // Do stuff and things I guess
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)meta;
            for (Enchantment ench: emeta.getStoredEnchants().keySet()) {
                emeta.removeStoredEnchant(ench);
            }

            for (Map.Entry<Enchantment,Integer> entry : finalEnchants.entrySet()) {
                emeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        else {
            //Remove old enchantments
            for (Enchantment ench: meta.getEnchants().keySet()) {
                meta.removeEnchant(ench);
            }

            //Add new enchantments
            for (Map.Entry<Enchantment,Integer> entry : finalEnchants.entrySet()) {
                // Add only if fitting for item
                if (!entry.getKey().getItemTarget().includes(result.getType())) continue;
                if (meta.hasConflictingEnchant(entry.getKey())) continue;
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        result.setItemMeta(meta);
        e.setResult(result);
    }

    private boolean hasEnchantments(ItemStack item) {
        if (!isAir(item) && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta instanceof EnchantmentStorageMeta) {
                return ((EnchantmentStorageMeta) meta).hasStoredEnchants();
            }
            else return meta.hasEnchants();
        }
        else return false;
    }

    private Map<Enchantment,Integer> getEnchantments(ItemStack item) {
        Preconditions.checkArgument(!isAir(item), "Air cannot have enchantments");

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                return ((EnchantmentStorageMeta) meta).getStoredEnchants();
            }
            else return meta.getEnchants();
        }
        else return new HashMap<>();
    }

    private Map<Enchantment,Integer> combineEnchants(Map<Enchantment,Integer> itemOne, Map<Enchantment,Integer> itemTwo) {
        Preconditions.checkNotNull(itemOne);
        Preconditions.checkNotNull(itemTwo);

        Set<Enchantment> avaliable = new LinkedHashSet<>(itemOne.keySet());
        avaliable.addAll(itemTwo.keySet());

        Map<Enchantment, Integer> used = new LinkedHashMap<>();

        for (Enchantment ench : avaliable) {
            Integer one = itemOne.get(ench);
            Integer two = itemTwo.get(ench);
            int result = merge(one,two);

            if (result <= 0) continue;

            used.put(ench, result);
        }
        return used;
    }

    private ItemMeta resetCost(ItemStack stack){
        Preconditions.checkNotNull(stack, "Item cannot be null");

        ItemMeta meta =  stack.getItemMeta();
        if(stack.hasItemMeta()) {
            if (!(meta instanceof Repairable)) return meta;

            ((Repairable) meta).setRepairCost(0);
            stack.setItemMeta(meta);
        }
        return meta;
    }

    private int merge(Integer one, Integer two) {
        int val1 = one == null ? 0 : one;
        int val2 = two == null? 0 : two;
        return merge(val1, val2);
    }

    private int merge(int one, int two){
        if (one <= 0 && two <= 0) return 0;
        return one == two ? one+1 : Math.max(one,two);
    }

    private boolean isAir(ItemStack item){
        return item == null || item.getType() == Material.AIR;
    }

}
