package com.jerry.mekanism_extras.common.recipes;

import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData.ReactionCondition;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiGasStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiInfusionStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiPigmentStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiSlurryStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiFluidStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiItemStackIngredient;
import com.jerry.mekanism_extras.common.registries.ExtraRecipeSerializers;
import com.jerry.mekanism_extras.common.registries.ExtraRecipeTypes;
import com.jerry.mekanism_extras.common.util.Predicate6;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChemicalReactionRecipe
        extends MekanismRecipe
        implements Predicate6<Set<@NotNull ItemStack>, Set<@NotNull FluidStack>, Set<@NotNull GasStack>, Set<@NotNull InfusionStack>, Set<@NotNull PigmentStack>, Set<@NotNull SlurryStack>> {

    private final Set<FeaturedMultiItemStackIngredient> inputItems;
    private final Set<FeaturedMultiFluidStackIngredient> inputFluids;
    private final Set<FeaturedMultiGasStackIngredient> inputGases;
    private final Set<FeaturedMultiInfusionStackIngredient> inputInfusions;
    private final Set<FeaturedMultiPigmentStackIngredient> inputPigments;
    private final Set<FeaturedMultiSlurryStackIngredient> inputSlurries;

    private final Set<ItemStack> outputItems;
    private final Set<FluidStack> outputFluids;
    private final Set<GasStack> outputGases;
    private final Set<InfusionStack> outputInfusions;
    private final Set<PigmentStack> outputPigments;
    private final Set<SlurryStack> outputSlurries;

    private final int duration;
    private final FloatingLong energyRequired;
    private final EnumSet<ReactionCondition> conditions;

    public ChemicalReactionRecipe(ResourceLocation id,
                                   Set<FeaturedMultiItemStackIngredient> inputItems,
                                   Set<FeaturedMultiFluidStackIngredient> inputFluids,
                                   Set<FeaturedMultiGasStackIngredient> inputGases,
                                   Set<FeaturedMultiInfusionStackIngredient> inputInfusions,
                                   Set<FeaturedMultiPigmentStackIngredient> inputPigments,
                                   Set<FeaturedMultiSlurryStackIngredient> inputSlurries,
                                   Set<ItemStack> outputItems,
                                   Set<FluidStack> outputFluids,
                                   Set<GasStack> outputGases,
                                   Set<InfusionStack> outputInfusions,
                                   Set<PigmentStack> outputPigments,
                                   Set<SlurryStack> outputSlurries,
                                   int duration, FloatingLong energyRequired,
                                   EnumSet<ReactionCondition> conditions) {
        super(id);
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }

        // We don't check if any of the inputs or outputs is null as that
        // would be troublesome
        this.inputItems = inputItems;
        this.inputFluids = inputFluids;
        this.inputGases = inputGases;
        this.inputInfusions = inputInfusions;
        this.inputPigments = inputPigments;
        this.inputSlurries = inputSlurries;
        // We should first make copies of them in case the origin stacks
        // get modified
        this.outputItems = outputItems.stream().map(ItemStack::copy).collect(Collectors.toSet());
        this.outputFluids = outputFluids.stream().map(FluidStack::copy).collect(Collectors.toSet());
        this.outputGases = outputGases.stream().map(GasStack::copy).collect(Collectors.toSet());
        this.outputInfusions = outputInfusions.stream().map(InfusionStack::copy).collect(Collectors.toSet());
        this.outputPigments = outputPigments.stream().map(PigmentStack::copy).collect(Collectors.toSet());
        this.outputSlurries = outputSlurries.stream().map(SlurryStack::copy).collect(Collectors.toSet());
        this.duration = duration;
        this.energyRequired = Objects.requireNonNull(energyRequired, "Required energy cannot be null.").copyAsConst();
        this.conditions = conditions;
    }

    public Set<FeaturedMultiItemStackIngredient> getInputItems() {
        return inputItems;
    }

    public Set<FeaturedMultiFluidStackIngredient> getInputFluids() {
        return inputFluids;
    }

    public Set<FeaturedMultiGasStackIngredient> getInputGases() {
        return inputGases;
    }

    public Set<FeaturedMultiInfusionStackIngredient> getInputInfusions() {
        return inputInfusions;
    }

    public Set<FeaturedMultiPigmentStackIngredient> getInputPigments() {
        return inputPigments;
    }

    public Set<FeaturedMultiSlurryStackIngredient> getInputSlurries() {
        return inputSlurries;
    }

    public int getDuration() {
        return duration;
    }

    public FloatingLong getEnergyRequired() {
        return energyRequired;
    }

    public EnumSet<ReactionCondition> getConditions() {
        return conditions;
    }

    /**
     * For JEI, gets the output representations to display.
     *
     * @return Representation of the output, <strong>MUST NOT</strong> be modified.
     */
    public List<ChemicalReactionRecipeOutput> getOutputDefinition() {
        return List.of(
                new ChemicalReactionRecipeOutput(outputItems, outputFluids, outputGases, outputInfusions, outputPigments, outputSlurries)
        );
    }

    /**
     * Gets a new output based on the given inputs.
     *
     * @return New output.
     * @apiNote While Mekanism does not currently make use of the inputs, it is
     * important to support it and pass the proper value in case any addons
     * define input based outputs where things like NBT may be different.
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    public ChemicalReactionRecipeOutput getOutput(Set<@NotNull ItemStack> itemStacks, Set<@NotNull FluidStack> fluidStacks,
                                                  Set<@NotNull GasStack> gasStacks, Set<@NotNull InfusionStack> infusionStacks,
                                                  Set<@NotNull PigmentStack> pigmentStacks, Set<@NotNull SlurryStack> slurryStacks) {
        return new ChemicalReactionRecipeOutput(outputItems, outputFluids, outputGases, outputInfusions, outputPigments, outputSlurries);
    }

    @Override
    public void logMissingTags() {
        inputItems.forEach(InputIngredient::logMissingTags);
        inputFluids.forEach(InputIngredient::logMissingTags);
        inputGases.forEach(InputIngredient::logMissingTags);
        inputInfusions.forEach(InputIngredient::logMissingTags);
        inputPigments.forEach(InputIngredient::logMissingTags);
        inputSlurries.forEach(InputIngredient::logMissingTags);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(inputItems.size());
        inputItems.forEach(i -> i.write(buffer));
        buffer.writeVarInt(inputFluids.size());
        inputFluids.forEach(i -> i.write(buffer));
        buffer.writeVarInt(inputGases.size());
        inputGases.forEach(i -> i.write(buffer));
        buffer.writeVarInt(inputInfusions.size());
        inputInfusions.forEach(i -> i.write(buffer));
        buffer.writeVarInt(inputPigments.size());
        inputPigments.forEach(i -> i.write(buffer));
        buffer.writeVarInt(inputSlurries.size());
        inputSlurries.forEach(i -> i.write(buffer));
        buffer.writeVarInt(outputItems.size());
        outputItems.forEach(i -> buffer.writeItemStack(i, false));
        buffer.writeVarInt(outputFluids.size());
        outputFluids.forEach(buffer::writeFluidStack);
        buffer.writeVarInt(outputGases.size());
        outputGases.forEach(i -> i.writeToPacket(buffer));
        buffer.writeVarInt(outputInfusions.size());
        outputInfusions.forEach(i -> i.writeToPacket(buffer));
        buffer.writeVarInt(outputPigments.size());
        outputPigments.forEach(i -> i.writeToPacket(buffer));
        buffer.writeVarInt(outputSlurries.size());
        outputSlurries.forEach(i -> i.writeToPacket(buffer));
        buffer.writeVarInt(duration);
        energyRequired.writeToBuffer(buffer);
        buffer.writeEnumSet(conditions, ReactionCondition.class);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ExtraRecipeSerializers.CHEMICAL_REACTION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ExtraRecipeTypes.CHEMICAL_REACTION.get();
    }

    @Override
    public boolean isIncomplete() {
        return inputItems.stream().anyMatch(InputIngredient::hasNoMatchingInstances) ||
                inputFluids.stream().anyMatch(InputIngredient::hasNoMatchingInstances) ||
                inputGases.stream().anyMatch(InputIngredient::hasNoMatchingInstances) ||
                inputInfusions.stream().anyMatch(InputIngredient::hasNoMatchingInstances) ||
                inputPigments.stream().anyMatch(InputIngredient::hasNoMatchingInstances) ||
                inputSlurries.stream().anyMatch(InputIngredient::hasNoMatchingInstances);
    }

    @Override
    public boolean test(Set<@NotNull ItemStack> itemStacks,
                        Set<@NotNull FluidStack> fluidStacks,
                        Set<@NotNull GasStack> gasStacks,
                        Set<@NotNull InfusionStack> infusionStacks,
                        Set<@NotNull PigmentStack> pigmentStacks,
                        Set<@NotNull SlurryStack> slurryStacks) {
        return uniqueMatch(inputItems, itemStacks) &&
                uniqueMatch(inputFluids, fluidStacks) &&
                uniqueMatch(inputGases, gasStacks) &&
                uniqueMatch(inputInfusions, infusionStacks) &&
                uniqueMatch(inputPigments, pigmentStacks) &&
                uniqueMatch(inputSlurries, slurryStacks);
    }

    /**
     * Checks if there exists a perfect bijective matching between predicates
     * and elements where each predicate matches exactly one unique element
     * <b>and</b> each element is matched by exactly one predicate.
     *
     * @param predicates The set of predicates to test
     * @param ts         The set of elements to test against
     * @param <T>        The type of elements being tested
     * @return true if a perfect bijective match exists, false otherwise
     */
    private static <T> boolean uniqueMatch(Set<? extends Predicate<T>> predicates, Set<T> ts) {
        // Must have equal sizes for bijective mapping
        if (predicates.size() != ts.size()) {
            return false;
        }

        // Precompute all matching relationships
        Map<T, List<Predicate<T>>> elementToPredicates = new HashMap<>();
        Map<Predicate<T>, List<T>> predicateToElements = new HashMap<>();

        // Build mapping from elements to matching predicates
        for (T element : ts) {
            List<Predicate<T>> matches = predicates.stream()
                    .filter(p -> p.test(element))
                    .collect(Collectors.toList());
            if (matches.isEmpty()) {
                return false; // Element not matched by any predicate
            }
            elementToPredicates.put(element, matches);
        }

        // Build mapping from predicates to matching elements
        for (Predicate<T> predicate : predicates) {
            List<T> matches = ts.stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
            if (matches.isEmpty()) {
                return false; // Predicate doesn't match any element
            }
            predicateToElements.put(predicate, matches);
        }

        // Find unique matches using greedy algorithm
        Set<T> matchedElements = new HashSet<>();
        for (Predicate<T> predicate : predicates) {
            // Find element that is only matched by this predicate
            Optional<T> uniqueMatch = predicateToElements.get(predicate).stream()
                    .filter(e -> elementToPredicates.get(e).size() == 1)
                    .filter(e -> !matchedElements.contains(e))
                    .findFirst();
            if (uniqueMatch.isEmpty()) {
                return false;
            }
            matchedElements.add(uniqueMatch.get());
        }

        return true;
    }

    /**
     * @apiNote Though not explicitly checked, at least one output must be
     * non-empty. Also, no null values are allowed. Use empty stacks or sets
     * instead.
     */
    public record ChemicalReactionRecipeOutput(@NotNull Set<@NotNull ItemStack> outputItems,
                                               @NotNull Set<@NotNull FluidStack> outputFluids,
                                               @NotNull Set<@NotNull GasStack> outputGases,
                                               @NotNull Set<@NotNull InfusionStack> outputInfusions,
                                               @NotNull Set<@NotNull PigmentStack> outputPigments,
                                               @NotNull Set<@NotNull SlurryStack> outputSlurries) {
    }
}
