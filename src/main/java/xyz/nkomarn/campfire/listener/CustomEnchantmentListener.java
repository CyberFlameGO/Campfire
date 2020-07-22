package xyz.nkomarn.campfire.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public class CustomEnchantmentListener implements Listener {

    @EventHandler
    public void onAnvilPrepare(@NotNull PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack leftItem = anvil.getFirstItem();
        ItemStack rightItem = anvil.getSecondItem();

        if (leftItem == null || rightItem == null) return;
        if (leftItem.getItemMeta() instanceof EnchantmentStorageMeta && !(rightItem.getItemMeta() instanceof EnchantmentStorageMeta)) return;

        ItemStack result = event.getResult();
        Map<Enchantment, Integer> leftIllegal = getIllegalEnchantments(leftItem);
        Map<Enchantment, Integer> rightIllegal = getIllegalEnchantments(rightItem);
        Map<Enchantment, Integer> combinedIllegal = combineEnchantmentMaps(leftIllegal, rightIllegal);

        if (result.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
            combinedIllegal.keySet().forEach(meta::removeStoredEnchant);
            combinedIllegal.forEach((enchantment, integer) -> meta.addStoredEnchant(enchantment, integer, true));
            result.setItemMeta(meta);
        } else {
            combinedIllegal.forEach((enchantment, integer) -> {
                if (enchantment.canEnchantItem(result)) {
                    result.addUnsafeEnchantment(enchantment, integer);
                }
            });
        }
        event.setResult(result);
    }

    private Map<Enchantment, Integer> getIllegalEnchantments(@NotNull ItemStack itemStack) {
        Map<Enchantment, Integer> result;
        if (itemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
            result = ((EnchantmentStorageMeta) itemStack.getItemMeta()).getStoredEnchants();
        } else {
            result = itemStack.getEnchantments();
        }

        return result.entrySet().stream()
                .filter(entry -> entry.getKey().getMaxLevel() < entry.getValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Enchantment, Integer> combineEnchantmentMaps(@NotNull Map<Enchantment, Integer> left, @NotNull Map<Enchantment, Integer> right) {
        Map<Enchantment, Integer> result = left;
        right.forEach((enchantment, level) -> {
            if (!result.containsKey(enchantment)) {
                result.put(enchantment, level);
                return;
            }
            int leftLevel = result.get(enchantment);
            result.put(enchantment, Math.max(leftLevel, level));
        });
        return result;
    }
}
