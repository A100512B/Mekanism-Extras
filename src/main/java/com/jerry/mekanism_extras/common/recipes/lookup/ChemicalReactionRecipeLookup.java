package com.jerry.mekanism_extras.common.recipes.lookup;

import com.jerry.mekanism_extras.common.config.LoadConfig;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe.ChemicalReactionRecipeInput;
import com.jerry.mekanism_extras.common.registries.ExtraRecipeTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceReferenceImmutablePair;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.InfusionStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.PigmentStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.SlurryStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ChemicalReactionRecipeLookup {

    @NotNull
    private final List<ChemicalReactionRecipe> recipes;
    //    /**
//     * @apiNote The key should be stacks with the stack's amount set to 1.
//     */
//    private final Map<Integer, Map<GenericStack, Set<ChemicalReactionRecipe>>> recipeDB = new Int2ObjectOpenHashMap<>();
    private final Map<ChemicalReactionRecipeInput, ChemicalReactionRecipe> cachedQueriedRecipes = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<ChemicalReactionRecipeInput, ChemicalReactionRecipe> eldest) {
            return size() > LoadConfig.extraConfig.chemicalReactorCacheEntries.getAsInt();
        }
    };
    private static final Map<Level, ChemicalReactionRecipeLookup> cachedLookups = new HashMap<>();

    private ChemicalReactionRecipeLookup(@NotNull Level world) {
        this.recipes = world.getRecipeManager().getAllRecipesFor(ExtraRecipeTypes.CHEMICAL_REACTION.getRecipeType());
//        setupRecipeDB();
    }

    public static ChemicalReactionRecipeLookup getInstanceForWorld(@NotNull Level world) {
        return cachedLookups.putIfAbsent(world, new ChemicalReactionRecipeLookup(world));
    }

    public List<ReferenceReferenceImmutablePair<ChemicalReactionRecipe, ChemicalReactionRecipeInput>> lookupRecipes(int circuitType, ChemicalReactionRecipeInput inputs, Predicate<ChemicalReactionRecipe> criteria) {
        return this.recipes.stream().filter(recipe -> recipe.getCircuitType() == circuitType)
                .collect(Collectors.toMap(
                        Function.identity(),
                        recipe -> testRecipe(recipe, inputs)
                )).entrySet()
                .stream()
                .filter(entry -> !entry.getValue().invalid())
                .filter(entry -> criteria.test(entry.getKey()))
                .map(entry -> new ReferenceReferenceImmutablePair<>(entry.getKey(), entry.getValue()))
                .toList();
    }

    public Optional<ReferenceReferenceImmutablePair<ChemicalReactionRecipe, ChemicalReactionRecipeInput>> lookupFirstRecipe(int circuitType, ChemicalReactionRecipeInput inputs, Predicate<ChemicalReactionRecipe> criteria) {
        if (inputs.invalid()) return Optional.empty();
        var queried = cachedQueriedRecipes.get(inputs);
        if (queried != null && criteria.and(recipe -> recipe.getCircuitType() == circuitType).test(queried)) {
            return cachedQueriedRecipes.entrySet().stream().filter(entry -> entry.getKey() == inputs)
                    .map(entry -> new ReferenceReferenceImmutablePair<>(entry.getValue(), entry.getKey()))
                    .findFirst();
        }
        return this.recipes.stream().filter(recipe -> recipe.getCircuitType() == circuitType)
                .collect(Collectors.toMap(
                        Function.identity(),
                        recipe -> testRecipe(recipe, inputs)
                )).entrySet()
                .stream()
                .filter(entry -> !entry.getValue().invalid())
                .filter(entry -> criteria.test(entry.getKey()))
                .map(entry -> new ReferenceReferenceImmutablePair<>(entry.getKey(), entry.getValue()))
                .findFirst();
    }

    public ChemicalReactionRecipeInput testRecipe(ChemicalReactionRecipe recipe, ChemicalReactionRecipeInput input) {
        if (cachedQueriedRecipes.get(input) == recipe) return input;

        List<ItemStackIngredient> inputItems = recipe.getInputItems();
        List<FluidStackIngredient> inputFluids = recipe.getInputFluids();
        List<GasStackIngredient> inputGases = recipe.getInputGases();
        List<InfusionStackIngredient> inputInfusions = recipe.getInputInfusions();
        List<PigmentStackIngredient> inputPigments = recipe.getInputPigments();
        List<SlurryStackIngredient> inputSlurries = recipe.getInputSlurries();
        List<ItemStack> itemStacks = List.of();
        List<FluidStack> fluidStacks = List.of();
        List<GasStack> gasStacks = List.of();
        List<InfusionStack> infusionStacks = List.of();
        List<PigmentStack> pigmentStacks = List.of();
        List<SlurryStack> slurryStacks = List.of();

        if (!inputItems.isEmpty()) {
            itemStacks = matchIngredientsWithInputs(inputItems, input.itemStacks());
            if (itemStacks.isEmpty())
                return ChemicalReactionRecipeInput.INVALID_INPUT;
        }
        if (!inputFluids.isEmpty()) {
            fluidStacks = matchIngredientsWithInputs(inputFluids, input.fluidStacks());
            if (fluidStacks.isEmpty())
                return ChemicalReactionRecipeInput.INVALID_INPUT;
        }
        if (!inputGases.isEmpty()) {
            gasStacks = matchIngredientsWithInputs(inputGases, input.gasStacks());
            if (gasStacks.isEmpty())
                return ChemicalReactionRecipeInput.INVALID_INPUT;
        }
        if (!inputInfusions.isEmpty()) {
            infusionStacks = matchIngredientsWithInputs(inputInfusions, input.infusionStacks());
            if (infusionStacks.isEmpty())
                return ChemicalReactionRecipeInput.INVALID_INPUT;
        }
        if (!inputPigments.isEmpty()) {
            pigmentStacks = matchIngredientsWithInputs(inputPigments, input.pigmentStacks());
            if (pigmentStacks.isEmpty())
                return ChemicalReactionRecipeInput.INVALID_INPUT;
        }
        if (!inputSlurries.isEmpty()) {
            slurryStacks = matchIngredientsWithInputs(inputSlurries, input.slurryStacks());
            if (slurryStacks.isEmpty())
                return ChemicalReactionRecipeInput.INVALID_INPUT;
        }
        return new ChemicalReactionRecipeInput(itemStacks, fluidStacks, gasStacks,
                infusionStacks, pigmentStacks, slurryStacks);
    }

    private <T> List<T> matchIngredientsWithInputs(List<? extends InputIngredient<@NotNull T>> ingredients, List<T> inputs) {
        return matchIngredientsWithInputs(ingredients, inputs, new ObjectArrayList<>());
    }

    /**
     * Recursively match the ingredients with the inputs.
     * @param <T> The input type
     * @param ingredients The ingredients of the recipe.
     * @param inputs The inputs. Should not contain stacks with the same item.
     * @param result The result to be recursively accumulated
     * @return The inputs to consume
     * @apiNote All the three parameters would get modified inside the method,
     * so the ingredients should be <b>deep</b>-copied from the recipe.
     */
    @Contract(mutates = "param1, param2, param3")
    private <T> List<T> matchIngredientsWithInputs(List<? extends InputIngredient<@NotNull T>> ingredients, List<T> inputs, List<T> result) {
        if (ingredients.isEmpty()) return result;
        if (inputs.size() < ingredients.size()) return List.of();

        InputIngredient<@NotNull T> ingredientToMatch = ingredients.get(0);
        boolean matched = false;
        for (T input : inputs) {
            if (ingredientToMatch.testType(input)) {
                // Once matched, we should set it to the correct amount
                result.add(ingredientToMatch.getMatchingInstance(input));
                matched = true;
                break;
            }
        }
        if (matched) {
            matchIngredientsWithInputs(ingredients, inputs, result);
        } else {
            result.clear();
        }
        return result;
    }

    // TODO: Use a better algorithm instead of the exhaustive method
//    @Nullable
//    public ChemicalReactionRecipe lookupFirstRecipe(int circuitType, List<GenericStack> inputs, Predicate<ChemicalReactionRecipe> criteria) {
//        // Look up in our cache first
//        ChemicalReactionRecipe recipeInCache;
//        if ((recipeInCache = cachedQueriedRecipes.get(inputs)) != null) return recipeInCache;
//
//        Map<GenericStack, Set<ChemicalReactionRecipe>> map = recipeDB.get(circuitType);
//        // Initialize
//        Set<ChemicalReactionRecipe> queriedRecipes = map.get(inputs.get(0));
//        List<GenericStack> results = new Stack<>();
//        // First, remove the invalid inputs
//        inputs.removeIf(gs -> map.get(gs) == null);
//
//        for (int i = 1; i < inputs.size(); i++) {
//            results.add(inputs.get(i - 1));
//            GenericStack currentStack = inputs.get(i);
//            Set<ChemicalReactionRecipe> searchedRecipes = map.get(currentStack);
//            queriedRecipes = Sets.intersection(queriedRecipes, Objects.requireNonNullElse(map.get(currentStack), new ObjectOpenHashSet<>()));
//            // No intersections, skip
//            if (queriedRecipes.isEmpty()) {
//                queriedRecipes = map.get(inputs.get(i));
//                continue;
//            }
//            // Got public recipes, which means the currentStack is a possible result
//            results.add(currentStack);
//
//        }
//
//        return null;
//    }
//
//    private void setupRecipeDB() {
//        MekanismExtras.LOGGER.debug("Start setting up chemical reaction recipe database...");
//        long start = System.currentTimeMillis();
//        for (ChemicalReactionRecipe recipe : recipes) {
//            for (InputIngredient<?> ingredient : new ArrayList<InputIngredient<?>>() {{
//                addAll(recipe.getInputItems());
//                addAll(recipe.getInputFluids());
//                addAll(recipe.getInputGases());
//                addAll(recipe.getInputInfusions());
//                addAll(recipe.getInputPigments());
//                addAll(recipe.getInputSlurries());
//            }}) {
//                ingredient.getRepresentations().stream().map(ChemicalReactionRecipeLookup::normalizeStack)
//                        .forEach(stack -> recipeDB.computeIfAbsent(recipe.getCircuitType(), i -> new Object2ObjectLinkedOpenHashMap<>())
//                                .compute(stack, (key, oldRecipes) -> {
//                                    Set<ChemicalReactionRecipe> result = oldRecipes == null ? new HashSet<>() : oldRecipes;
//                                    result.add(recipe);
//                                    return result;
//                                }));
//            }
//        }
//        long end = System.currentTimeMillis();
//        MekanismExtras.LOGGER.debug("Done! Time elapsed: {}", end - start);
//    }
//
//    private static GenericStack normalizeStack(Object stack) {
//        if (stack instanceof ItemStack s)
//            return new GenericStack(s.copyWithCount(1));
//        else if (stack instanceof FluidStack s) {
//            FluidStack result = s.copy();
//            result.setAmount(1);
//            return new GenericStack(result);
//        } else if (stack instanceof ChemicalStack<?> s) {
//            ChemicalStack<?> result = s.copy();
//            s.setAmount(1);
//            return new GenericStack(result);
//        } else
//            throw new IllegalArgumentException("Expected stack to be an ItemStack, FluidStack or ChemicalStack<?>, got " + stack.getClass().getName());
//    }
//
//    public record GenericStack(Object what) {
//
//        public boolean isSameType(GenericStack other) {
//            return what.getClass() == other.getClass();
//        }
//
//        public boolean matchesIngredient(InputIngredient<?> ingredient) {
//            return (what instanceof ItemStack && ingredient instanceof ItemStackIngredient) ||
//                    (what instanceof FluidStack && ingredient instanceof FluidStackIngredient) ||
//                    (what instanceof GasStack && ingredient instanceof GasStackIngredient) ||
//                    (what instanceof InfusionStack && ingredient instanceof InfusionStackIngredient) ||
//                    (what instanceof PigmentStack && ingredient instanceof PigmentStackIngredient) ||
//                    (what instanceof SlurryStack && ingredient instanceof SlurryStackIngredient);
//        }
//    }
}
