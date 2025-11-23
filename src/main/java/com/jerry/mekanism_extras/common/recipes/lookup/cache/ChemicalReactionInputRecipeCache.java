package com.jerry.mekanism_extras.common.recipes.lookup.cache;

import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe;
import com.jerry.mekanism_extras.common.recipes.lookup.cache.type.ChemicalGroupedInputCache.GasGroupedInputCache;
import com.jerry.mekanism_extras.common.recipes.lookup.cache.type.ChemicalGroupedInputCache.InfuseTypeGroupedInputCache;
import com.jerry.mekanism_extras.common.recipes.lookup.cache.type.ChemicalGroupedInputCache.PigmentGroupedInputCache;
import com.jerry.mekanism_extras.common.recipes.lookup.cache.type.ChemicalGroupedInputCache.SlurryGroupedInputCache;
import com.jerry.mekanism_extras.common.recipes.lookup.cache.type.FluidGroupedInputCache;
import com.jerry.mekanism_extras.common.recipes.lookup.cache.type.ItemGroupedInputCache;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.AbstractInputRecipeCache;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ChemicalReactionInputRecipeCache extends AbstractInputRecipeCache<ChemicalReactionRecipe> {

    private final ItemGroupedInputCache<ChemicalReactionRecipe> itemCache = new ItemGroupedInputCache<>();
    private final FluidGroupedInputCache<ChemicalReactionRecipe> fluidCache = new FluidGroupedInputCache<>();
    private final GasGroupedInputCache<ChemicalReactionRecipe> gasCache = new GasGroupedInputCache<>();
    private final InfuseTypeGroupedInputCache<ChemicalReactionRecipe> infuseTypeCache = new InfuseTypeGroupedInputCache<>();
    private final PigmentGroupedInputCache<ChemicalReactionRecipe> pigmentCache = new PigmentGroupedInputCache<>();
    private final SlurryGroupedInputCache<ChemicalReactionRecipe> slurryCache = new SlurryGroupedInputCache<>();
    // For fallback, though I don't know why Mekanism did so, since the cache
    // should have contained all the recipes
    private final Set<ChemicalReactionRecipe> complexRecipes = new HashSet<>();

    public ChemicalReactionInputRecipeCache(MekanismRecipeType<ChemicalReactionRecipe, ?> recipeType) {
        super(recipeType);
    }

    @Override
    public void clear() {
        super.clear();
        itemCache.clear();
        fluidCache.clear();
        gasCache.clear();
        infuseTypeCache.clear();
        pigmentCache.clear();
        slurryCache.clear();
    }

    @Override
    protected void initCache(List<ChemicalReactionRecipe> chemicalReactionRecipes) {
        chemicalReactionRecipes.forEach(recipe -> {
            itemCache.mapGroupedInputs(recipe, recipe.getInputItems());
            fluidCache.mapGroupedInputs(recipe, recipe.getInputFluids());
            gasCache.mapGroupedInputs(recipe, recipe.getInputGases());
            infuseTypeCache.mapGroupedInputs(recipe, recipe.getInputInfusions());
            pigmentCache.mapGroupedInputs(recipe, recipe.getInputPigments());
            slurryCache.mapGroupedInputs(recipe, recipe.getInputSlurries());
            complexRecipes.add(recipe);
        });
    }

    /**
     * Checks if there exists a recipe that contains (may not contain all) the
     * given inputs.
     *
     * @apiNote No null values are accepted for the inputs, use empty sets
     * instead.
     */
    public boolean containsInput(@Nullable Level world, Set<ItemStack> inputItems, Set<FluidStack> inputFluids,
                                 Set<GasStack> inputGases, Set<InfusionStack> inputInfusions, Set<PigmentStack> inputPigments,
                                 Set<SlurryStack> inputSlurries) {
        // No empty inputs are allowed
        if (inputItems.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputFluids.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputGases.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputInfusions.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputPigments.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputSlurries.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty))
            return false;

        initCacheIfNeeded(world);
        return (itemCache.containsForPartial(inputItems) && fluidCache.containsForPartial(inputFluids)
                && gasCache.containsForPartial(inputGases) && infuseTypeCache.containsForPartial(inputInfusions)
                && pigmentCache.containsForPartial(inputPigments) && slurryCache.containsForPartial(inputSlurries))
                || complexRecipes.stream().anyMatch(
                recipe -> recipe.test(inputItems, inputFluids, inputGases, inputInfusions, inputPigments, inputSlurries)
        );
    }

    /**
     * Checks if there exists a recipe that contains (may not contain all) the
     * given inputs and matches the given criteria.
     *
     * @apiNote No null values are accepted for the inputs, use empty sets
     * instead.
     */
    public boolean containsInput(@Nullable Level world, Set<ItemStack> inputItems, Set<FluidStack> inputFluids,
                                 Set<GasStack> inputGases, Set<InfusionStack> inputInfusions, Set<PigmentStack> inputPigments,
                                 Set<SlurryStack> inputSlurries, Predicate<ChemicalReactionRecipe> criteria) {
        // No empty inputs are allowed
        if (inputItems.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputFluids.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputGases.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputInfusions.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputPigments.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputSlurries.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty))
            return false;

        initCacheIfNeeded(world);
        return (itemCache.containsForPartial(inputItems, criteria) &&
                fluidCache.containsForPartial(inputFluids, criteria) &&
                gasCache.containsForPartial(inputGases, criteria) &&
                infuseTypeCache.containsForPartial(inputInfusions, criteria) &&
                pigmentCache.containsForPartial(inputPigments, criteria) &&
                slurryCache.containsForPartial(inputSlurries, criteria)) || complexRecipes.stream()
                .filter(criteria)
                .anyMatch(
                        recipe -> recipe.test(inputItems, inputFluids, inputGases, inputInfusions, inputPigments, inputSlurries)
                );
    }

    @Nullable
    public ChemicalReactionRecipe findFirstRecipe(@Nullable Level world, Set<ItemStack> inputItems, Set<FluidStack> inputFluids,
                                                  Set<GasStack> inputGases, Set<InfusionStack> inputInfusions, Set<PigmentStack> inputPigments,
                                                  Set<SlurryStack> inputSlurries) {
        // No empty inputs are allowed
        if (inputItems.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputFluids.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputGases.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputInfusions.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputPigments.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty) ||
                inputSlurries.stream().anyMatch(ChemicalReactionInputRecipeCache::checkEmpty))
            return null;

        initCacheIfNeeded(world);
        Predicate<ChemicalReactionRecipe> criteria = recipe0 -> recipe0.test(inputItems, inputFluids, inputGases, inputInfusions, inputPigments, inputSlurries);
        ChemicalReactionRecipe recipe = itemCache.findFirstRecipeForPartial(inputItems, criteria);
        return recipe != null ? recipe : complexRecipes.stream()
                .filter(criteria)
                .findFirst()
                .orElse(null);
    }

    private static boolean checkEmpty(ItemStack stack) {
        return stack.isEmpty();
    }

    private static boolean checkEmpty(FluidStack stack) {
        return stack.isEmpty();
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>> boolean checkEmpty(ChemicalStack<CHEMICAL> stack) {
        return stack.isEmpty();
    }
}
