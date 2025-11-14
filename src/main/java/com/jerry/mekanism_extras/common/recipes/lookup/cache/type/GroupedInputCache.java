package com.jerry.mekanism_extras.common.recipes.lookup.cache.type;

import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiIngredient;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.common.recipe.ingredient.IMultiIngredient;
import mekanism.common.recipe.lookup.cache.type.IInputCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Extended input cache that implements grouped inputs with a variable quantity.
 *
 * @apiNote {@code Set<INPUT>} should be an unmodifiable set so that map can
 * work properly.
 */
public abstract class GroupedInputCache<KEY, INPUT, INGREDIENT extends FeaturedMultiIngredient<INPUT, ? extends InputIngredient<@NotNull INPUT>>,
        RECIPE extends MekanismRecipe>
        implements IInputCache<INPUT, INGREDIENT, RECIPE> {

    /**
     * Map of keys representing a <b>single</b> input to all cached recipes
     * containing the key, allowing for quickly checking if a key exists,
     * as well as a quicker recipe lookup.
     */
    protected final Map<KEY, Set<RECIPE>> inputCache = new HashMap<>();
    /**
     * Map of a group of <b>whole</b> inputs to a unique recipe that exactly
     * contains all the ingredients, allowing for precisely looking up a recipe.
     */
    protected final Map<Set<KEY>, Set<RECIPE>> preciseInputCache = new HashMap<>();

    /**
     * @apiNote We don't allow a single ingredient to be mapped to avoid ambiguity,
     * as we used {@code Set<INPUT>} as keys.
     */
    @Override
    @Deprecated
    public final boolean mapInputs(RECIPE recipe, INGREDIENT inputIngredient)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("A single ingredient is not permitted to be mapped to avoid ambiguity. Use mapGroupedInputs() instead.");
    }

    /**
     * @apiNote We don't allow a single multi-ingredient to be mapped to avoid
     * ambiguity, as we used {@code Set<INPUT>} as keys.
     */
    @Override
    @Deprecated
    public final boolean mapMultiInputs(RECIPE recipe, IMultiIngredient<INPUT, ? extends INGREDIENT> multi)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("A single multi-ingredient is not permitted to be mapped to avoid ambiguity. Use mapGroupedMultiInputs() instead.");
    }

    /**
     * Maps the given ingredient set and adds it into this {@link GroupedInputCache}
     * as a quicker lookup for the given recipe.
     *
     * @param recipe           Recipe of which the given ingredients are inputs.
     * @param inputIngredients Ingredient set to be mapped and cached.
     * @return {@code true} if any ingredient of the set is complex and the
     * {@link mekanism.common.recipe.lookup.cache.IInputRecipeCache} will need
     * to do extra handling, or {@code false} if we were able to fully cache
     * the ingredient's components.
     */
    public boolean mapGroupedInputs(RECIPE recipe, Set<INGREDIENT> inputIngredients) {
        // Cache single items
        inputIngredients.forEach(ingredient -> ingredient.getRepresentations()
                .forEach(stack -> this.inputCache
                        .computeIfAbsent(createSingleKey(stack), k -> new HashSet<>()).add(recipe)
                )
        );

        // Cache grouped items
        inputIngredients.stream()
                .map(FeaturedMultiIngredient::getRepresentations)
                .map(HashSet::new)
                .forEach(set -> this.preciseInputCache.
                        computeIfAbsent(createGroupedKey(set), k -> new HashSet<>()).add(recipe));

        return !inputIngredients.stream().allMatch(FeaturedMultiIngredient::handleable);
    }

    /**
     * Clear this {@link GroupedInputCache}.
     */
    @Override
    public void clear() {
        inputCache.clear();
        preciseInputCache.clear();
    }

    /**
     * Checks if this {@link IInputCache} knows about the single given input.
     *
     * @param input Input to check.
     * @return {@code true} if this cache does have the given input,
     * {@code false} if there isn't.
     */
    @Override
    public boolean contains(INPUT input) {
        return inputCache.containsKey(createSingleKey(input));
    }

    /**
     * Checks if this {@link IInputCache} knows about the single given input.
     * If so, check if any of the recipes match the given predicate.
     * @param input Input to check.
     * @param matchCriteria Predicate to further validate recipes with.
     * @return {@code true} if this cache does have the given input with a
     * recipe matching the predicate, {@code false} if there isn't.
     */
    @Override
    public boolean contains(INPUT input, Predicate<RECIPE> matchCriteria) {
        Set<RECIPE> recipes = inputCache.get(input);
        return recipes != null && recipes.stream().anyMatch(matchCriteria);
    }

    /**
     * Checks if this {@link GroupedInputCache} knows about all the given
     * inputs.
     *
     * @param input Inputs to check.
     * @return {@code true} if this cache does have all the given inputs,
     * {@code false} if there aren't.
     */
    public boolean containsWhole(Set<INPUT> input) {
        return preciseInputCache.containsKey(createGroupedKey(input));
    }

    /**
     * Checks if this {@link GroupedInputCache} knows about all the given
     * inputs. If so,
     *
     * @param input Inputs to check.
     * @param matchCriteria Predicate to further validate recipes with.
     * @return {@code true} if this cache does have all the given inputs
     * with a recipe matching the predicate, {@code false} if there aren't.
     */
    public boolean containsWhole(Set<INPUT> input, Predicate<RECIPE> matchCriteria) {
        Set<RECIPE> recipes = preciseInputCache.get(input);
        return recipes != null && recipes.stream().anyMatch(matchCriteria);
    }

    /**
     * Creates a key for the given input for use in querying our input cache.
     *
     * @param input Input to convert into a key.
     * @return Key representing the given input.
     */
    protected abstract KEY createSingleKey(INPUT input);

    /**
     * Helper method for creating a key for a group of all inputs.
     *
     * @param inputs Inputs to convert into a key.
     * @return Key representing the given input group.
     */
    protected @Unmodifiable Set<KEY> createGroupedKey(Set<INPUT> inputs) {
        return inputs.stream()
                .map(this::createSingleKey)
                .collect(Collectors.toSet());
    }

    /**
     * Finds the first recipe that contains the given single input and matches
     * the given criteria.
     *
     * @param input         Single input to check.
     * @param matchCriteria Predicate to further validate recipes with.
     * @return Recipe for the given input that matches the given criteria, or
     * {@code null} if no recipe matches.
     * @apiNote We don't check the amount, only the type.
     */
    @Nullable
    @Override
    public RECIPE findFirstRecipe(INPUT input, Predicate<RECIPE> matchCriteria) {
        Set<RECIPE> recipes = inputCache.get(createSingleKey(input));
        if (recipes == null) return null;
        return recipes.stream()
                .filter(matchCriteria)
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the first recipe that exactly contains all the given inputs and
     * matches the given criteria.
     *
     * @param allInputs     All inputs to check.
     * @param matchCriteria Predicate to further validate recipes with.
     * @return Recipe for the given input that matches the given criteria, or
     * {@code null} if no recipe matches.
     * @apiNote We don't check the amount, only the type.
     */
    public RECIPE findFirstRecipe(Set<INPUT> allInputs, Predicate<RECIPE> matchCriteria) {
        Set<RECIPE> recipes = inputCache.get(createGroupedKey(allInputs));
        if (recipes == null) return null;
        return recipes.stream()
                .filter(matchCriteria)
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the first recipe that contains all the given inputs (may contain
     * other inputs) and matches the given criteria.
     *
     * @param partialInputs Partial inputs to check.
     * @param matchCriteria Predicate to further validate recipes with.
     * @return Recipe for the given input that matches the given criteria, or
     * {@code null} if no recipe matches.
     * @apiNote We don't check the amount, only the type.
     */
    @Nullable
    public RECIPE findFirstRecipeForPartial(Set<INPUT> partialInputs, Predicate<RECIPE> matchCriteria) {
        Set<KEY> partialKey = createGroupedKey(partialInputs);
        return preciseInputCache.entrySet().stream()
                .filter(entry -> entry.getKey().containsAll(partialKey))
                .map(Map.Entry::getValue)
                .flatMap(Set::stream)
                .filter(matchCriteria)
                .findFirst()
                .orElse(null);
    }
}
