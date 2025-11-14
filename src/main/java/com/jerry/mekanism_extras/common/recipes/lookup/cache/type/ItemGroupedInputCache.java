package com.jerry.mekanism_extras.common.recipes.lookup.cache.type;

import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiItemStackIngredient;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.lib.inventory.HashedItem;
import net.minecraft.world.item.ItemStack;

public class ItemGroupedInputCache<RECIPE extends MekanismRecipe>
        extends GroupedInputCache<HashedItem, ItemStack, FeaturedMultiItemStackIngredient, RECIPE> {

    @Override
    protected HashedItem createSingleKey(ItemStack stack) {
        return HashedItem.create(stack);
    }

    @Override
    public boolean isEmpty(ItemStack stack) {
        return stack.isEmpty();
    }
}
