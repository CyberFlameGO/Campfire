package xyz.nkomarn.Campfire.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import xyz.nkomarn.Campfire.Campfire;

public class Recipes {
    public static void loadRecipes() {
        // Bell recipe
        ItemStack bell = new ItemStack(Material.BELL, 1);
        NamespacedKey key = new NamespacedKey(Campfire.getCampfire(), "bell");
        ShapedRecipe recipe = new ShapedRecipe(key, bell);
        recipe.shape(" S ", "NIN");
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('N', Material.GOLD_NUGGET);
        recipe.setIngredient('I', Material.GOLD_INGOT);
        Bukkit.addRecipe(recipe);
    }
}
