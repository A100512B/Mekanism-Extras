package com.jerry.mekanism_extras.common.recipes.lookup.cache.type;

import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiFluidStackIngredient;
import mekanism.api.recipes.MekanismRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidGroupedInputCache<RECIPE extends MekanismRecipe>
        extends GroupedInputCache<Fluid, FluidStack, FeaturedMultiFluidStackIngredient, RECIPE> {

    @Override
    protected Fluid createSingleKey(FluidStack fluidStack) {
        return fluidStack.getFluid();
    }

    @Override
    public boolean isEmpty(FluidStack fluidStack) {
        return fluidStack.isEmpty();
    }
}
