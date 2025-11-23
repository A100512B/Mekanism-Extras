package com.jerry.mekanism_extras.common.recipes.lookup.cache.type;

import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiGasStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiInfusionStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiPigmentStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiSlurryStackIngredient;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
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

    public static class GasGroupedInputCache<RECIPE extends MekanismRecipe>
            extends ChemicalGroupedInputCache<Gas, GasStack, FeaturedMultiGasStackIngredient, RECIPE> {}

    public static class InfuseTypeGroupedInputCache<RECIPE extends MekanismRecipe>
            extends ChemicalGroupedInputCache<InfuseType, InfusionStack, FeaturedMultiInfusionStackIngredient, RECIPE> {}

    public static class PigmentGroupedInputCache<RECIPE extends MekanismRecipe>
            extends ChemicalGroupedInputCache<Pigment, PigmentStack, FeaturedMultiPigmentStackIngredient, RECIPE> {}

    public static class SlurryGroupedInputCache<RECIPE extends MekanismRecipe>
            extends ChemicalGroupedInputCache<Slurry, SlurryStack, FeaturedMultiSlurryStackIngredient, RECIPE> {}
}
