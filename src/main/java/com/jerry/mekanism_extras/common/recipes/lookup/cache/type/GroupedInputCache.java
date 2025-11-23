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
     */
    public void mapGroupedInputs(RECIPE recipe, Set<INGREDIENT> inputIngredients) {
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
     * Checks if this {@link IInputCache} knows about a recipe which contains
     * the given single input.
     *
     * @param input Input to check.
     * @return {@code true} if this cache knows a recipe that contains the
     * given input, {@code false} otherwise.
     */
    @Override
    public boolean contains(INPUT input) {
        return inputCache.containsKey(createSingleKey(input));
    }

    /**
     * Checks if this {@link IInputCache} knows about a recipe which contains
     * the given single input and match the given criteria.
     *
     * @param input         Input to check.
     * @param matchCriteria Predicate to further validate recipes with.
     * @return {@code true} if this cache knows a recipe that contains the
     * given input and matches the given criteria, {@code false} otherwise.
     */
    @Override
    public boolean contains(INPUT input, Predicate<RECIPE> matchCriteria) {
        Set<RECIPE> recipes = inputCache.get(createSingleKey(input));
        return recipes != null && recipes.stream().anyMatch(matchCriteria);
    }

    /**
     * Checks if this {@link GroupedInputCache} knows about a recipe which
     * contains all the given inputs (may have other inputs).
     *
     * @param input Inputs to check. An empty input set will always result
     *              in a {@code true} return value.
     * @return {@code true} if this cache knows a recipe that contains the
     * given inputs (may have extra inputs), {@code false} otherwise.
     */
    public boolean containsForPartial(@NotNull Set<INPUT> input) {
        Set<KEY> partialKey = createGroupedKey(input);
        return preciseInputCache.entrySet().stream()
                .anyMatch(entry -> entry.getKey().containsAll(partialKey));
    }

    /**
     * Checks if this {@link GroupedInputCache} knows about a recipe which
     * contains all the given inputs (may have other inputs) and matches
     * the given criteria.
     *
     * @param input         Inputs to check. An empty input set will always
     *                      result in a {@code true} return value.
     * @param matchCriteria Predicate to further validate recipes with.
     * @return {@code true} if this cache knows a recipe that contains the
     * given inputs (may have extra inputs) and matches the given criteria,
     * {@code false} otherwise.
     */
    public boolean containsForPartial(@NotNull Set<INPUT> input, Predicate<RECIPE> matchCriteria) {
        Set<KEY> partialKey = createGroupedKey(input);
        return preciseInputCache.entrySet().stream()
                .filter(entry -> entry.getKey().containsAll(partialKey))
                .map(Map.Entry::getValue)
                .flatMap(Set::stream)
                .anyMatch(matchCriteria);
    }

    /**
     * Checks if this {@link GroupedInputCache} knows about a recipe which
     * exactly contains all the given inputs.
     *
     * @param input Inputs to check.
     * @return {@code true} if at least a recipe exactly has all the given
     * inputs, {@code false} otherwise.
     */
    public boolean containsWhole(@NotNull Set<INPUT> input) {
        return preciseInputCache.containsKey(createGroupedKey(input));
    }

    /**
     * Checks if this {@link GroupedInputCache} knows about a recipe which
     * exactly contains all the given inputs and match the given criteria.
     *
     * @param input         Inputs to check.
     * @param matchCriteria Predicate to further validate recipes with.
     * @return {@code true} if at least a recipe exactly has all the given
     * inputs and matches the given criteria, {@code false} otherwise.
     */
    public boolean containsWhole(@NotNull Set<INPUT> input, Predicate<RECIPE> matchCriteria) {
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
    @Unmodifiable
    protected Set<KEY> createGroupedKey(Set<INPUT> inputs) {
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
    @Nullable
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
     * @param partialInputs Partial inputs to check. If this is empty, all
     *                      recipes are going to be checked (as all recipes
     *                      have an empty set as the subset of their
     *                      ingredients).
     * @return Recipe for the given input that matches the given criteria, or
     * {@code null} if no recipe matches.
     * @apiNote We don't check the amount, only the type.
     */
    @Nullable
    public RECIPE findFirstRecipeForPartial(Set<INPUT> partialInputs) {
        Set<KEY> partialKey = createGroupedKey(partialInputs);
        return preciseInputCache.entrySet().stream()
                .filter(entry -> entry.getKey().containsAll(partialKey))
                .map(Map.Entry::getValue)
                .flatMap(Set::stream)
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the first recipe that contains all the given inputs (may contain
     * other inputs) and matches the given criteria.
     *
     * @param partialInputs Partial inputs to check. If this is empty, all
     *                      recipes are going to be checked (as all recipes
     *                      have an empty set as the subset of their
     *                      ingredients).
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
