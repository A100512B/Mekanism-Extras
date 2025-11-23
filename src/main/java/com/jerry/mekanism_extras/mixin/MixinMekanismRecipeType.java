package com.jerry.mekanism_extras.mixin;

import com.jerry.mekanism_extras.common.recipes.lookup.cache.ChemicalReactionInputRecipeCache;
import com.jerry.mekanism_extras.common.registries.ExtraRecipeTypes;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = MekanismRecipeType.class, remap = false)
public class MixinMekanismRecipeType {

    @Shadow
    private static <RECIPE extends MekanismRecipe, INPUT_CACHE extends IInputRecipeCache> RecipeTypeRegistryObject<RECIPE, INPUT_CACHE> register(String name, Function<MekanismRecipeType<RECIPE, INPUT_CACHE>, INPUT_CACHE> inputCacheCreator) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void meke$registerExtraRecipeTypes(CallbackInfo ci) {
        ExtraRecipeTypes.CHEMICAL_REACTION = register("chemical_reaction", ChemicalReactionInputRecipeCache::new);
    }
}
