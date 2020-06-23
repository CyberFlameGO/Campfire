package xyz.nkomarn.Campfire.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomEnchantmentListener implements Listener {

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();

        if (firstItem == null || secondItem == null) {
            return;
        }

        if (!(secondItem.getItemMeta() instanceof EnchantmentStorageMeta)) {
            return;
        }

        ItemStack result = firstItem.clone();
        boolean isRecipientEnchantmentStorage = (result.getItemMeta() instanceof EnchantmentStorageMeta);

        Map<Enchantment, Integer> firstItemEnchantments = firstItem.getEnchantments();
        if (isRecipientEnchantmentStorage) {
            firstItemEnchantments = ((EnchantmentStorageMeta) firstItem.getItemMeta()).getStoredEnchants();
        }

        Map<Enchantment, Integer> enchantmentsToAdd = ((EnchantmentStorageMeta) secondItem.getItemMeta()).getStoredEnchants();
        Map<Enchantment, Integer> finalEnchantments = combineEnchantments(firstItemEnchantments, enchantmentsToAdd);
        if (isRecipientEnchantmentStorage) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
            finalEnchantments.forEach((enchantment, level) -> meta.addStoredEnchant(enchantment, level, true));
            result.setItemMeta(meta);
        } else {
            result.addUnsafeEnchantments(finalEnchantments
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().canEnchantItem(result))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        event.setResult(result);
    }

    private Map<Enchantment, Integer> combineEnchantments(Map<Enchantment, Integer> first, Map<Enchantment, Integer> second) {
        Map<Enchantment, Integer> result = new HashMap<>(first);

        second.forEach((enchantment, level) -> {
            if (first.containsKey(enchantment)) {
                int firstLevel = first.get(enchantment);

                if (firstLevel < level) {
                    result.put(enchantment, level);
                } else if (firstLevel == level) {
                    int resultLevel = level + 1;
                    if(resultLevel > enchantment.getMaxLevel()) {
                        resultLevel = level;
                    }
                    result.put(enchantment, resultLevel);
                }
            } else {
                result.put(enchantment, level);
            }
        });

        return result;
    }
}
