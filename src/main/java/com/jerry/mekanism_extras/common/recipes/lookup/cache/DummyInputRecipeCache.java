package com.jerry.mekanism_extras.common.recipes.lookup.cache;

import com.jerry.mekanism_extras.MekanismExtras;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.AbstractInputRecipeCache;
import mekanism.common.recipe.lookup.cache.type.IInputCache;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class DummyInputRecipeCache<RECIPE extends MekanismRecipe>
        extends AbstractInputRecipeCache<RECIPE> {

    public DummyInputRecipeCache(MekanismRecipeType<RECIPE, ?> recipeType) {
        super(recipeType);
    }

    @Override
    protected void initCache(List<RECIPE> recipes) {
        MekanismExtras.LOGGER.info("Ciallo～(∠・ω< )⌒★");
    }

    @Override
    protected @Nullable RECIPE findFirstRecipe(@Nullable Collection<RECIPE> recipes, Predicate<RECIPE> matchCriteria) {
        throw new UnsupportedOperationException("Dummy!");
    }

    @Override
    protected <INPUT, INGREDIENT extends InputIngredient<INPUT>, CACHE extends IInputCache<INPUT, INGREDIENT, RECIPE>> boolean containsInput(@Nullable Level world, INPUT input, Function<RECIPE, INGREDIENT> inputExtractor, CACHE cache, Set<RECIPE> complexRecipes) {
        throw new UnsupportedOperationException("Dummy!");
    }

    @Override
    protected <INPUT_1, INGREDIENT_1 extends InputIngredient<INPUT_1>, CACHE_1 extends IInputCache<INPUT_1, INGREDIENT_1, RECIPE>, INPUT_2, INGREDIENT_2 extends InputIngredient<INPUT_2>, CACHE_2 extends IInputCache<INPUT_2, INGREDIENT_2, RECIPE>> boolean containsPairing(@Nullable Level world, INPUT_1 input1, Function<RECIPE, INGREDIENT_1> input1Extractor, CACHE_1 cache1, Set<RECIPE> complexIngredients1, INPUT_2 input2, Function<RECIPE, INGREDIENT_2> input2Extractor, CACHE_2 cache2, Set<RECIPE> complexIngredients2) {
        throw new UnsupportedOperationException("Dummy!");
    }
}
