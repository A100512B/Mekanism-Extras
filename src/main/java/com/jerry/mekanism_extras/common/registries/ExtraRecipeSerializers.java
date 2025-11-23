package com.jerry.mekanism_extras.common.registries;

import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe;
import com.jerry.mekanism_extras.common.recipes.serializer.ChemicalReactionRecipeSerializer;
import mekanism.common.registration.impl.RecipeSerializerDeferredRegister;
import mekanism.common.registration.impl.RecipeSerializerRegistryObject;

public class ExtraRecipeSerializers {

    private ExtraRecipeSerializers() {}

    public static final RecipeSerializerDeferredRegister EXTRA_RECIPE_SERIALIZERS
            = new RecipeSerializerDeferredRegister(MekanismExtras.MODID);

    public static final RecipeSerializerRegistryObject<ChemicalReactionRecipe> CHEMICAL_REACTION
            = EXTRA_RECIPE_SERIALIZERS.register("chemical_reaction", () -> new ChemicalReactionRecipeSerializer(ChemicalReactionRecipe::new));
}
