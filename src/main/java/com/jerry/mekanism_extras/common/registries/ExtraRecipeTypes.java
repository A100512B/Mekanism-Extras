package com.jerry.mekanism_extras.common.registries;

import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe;
import com.jerry.mekanism_extras.common.recipes.lookup.cache.DummyInputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeDeferredRegister;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

public class ExtraRecipeTypes {

    private ExtraRecipeTypes() {}

    public static final RecipeTypeDeferredRegister EXTRA_RECIPE_TYPES = new RecipeTypeDeferredRegister(MekanismExtras.MODID);

    // Do not modify these fields as they will be assigned by mixin
    // Too complex! The cache system cannot handle this.
    public static RecipeTypeRegistryObject<ChemicalReactionRecipe, DummyInputRecipeCache<ChemicalReactionRecipe>> CHEMICAL_REACTION;
}
