package com.jerry.mekanism_extras.common.recipes.lookup.cache.type;

import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;

public class ChemicalGroupedInputCache<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>,
        INGREDIENT extends FeaturedMultiChemicalStackIngredient<CHEMICAL, STACK, ?>, RECIPE extends MekanismRecipe>
        extends GroupedInputCache<CHEMICAL, STACK, INGREDIENT, RECIPE> {

    @Override
    protected CHEMICAL createSingleKey(STACK stack) {
        return stack.getType();
    }

    @Override
    public boolean isEmpty(STACK stack) {
        return stack.isEmpty();
    }
}
