package com.jerry.mekanism_extras.common.recipes;

import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData.ReactionCondition;
import com.jerry.mekanism_extras.common.registries.ExtraRecipeSerializers;
import com.jerry.mekanism_extras.common.registries.ExtraRecipeTypes;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.InfusionStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.PigmentStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.SlurryStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public class ChemicalReactionRecipe
        extends MekanismRecipe {

    private final List<ItemStackIngredient> inputItems;
    private final List<FluidStackIngredient> inputFluids;
    private final List<GasStackIngredient> inputGases;
    private final List<InfusionStackIngredient> inputInfusions;
    private final List<PigmentStackIngredient> inputPigments;
    private final List<SlurryStackIngredient> inputSlurries;

    private final Set<ItemStack> outputItems;
    private final Set<FluidStack> outputFluids;
    private final Set<GasStack> outputGases;
    private final Set<InfusionStack> outputInfusions;
    private final Set<PigmentStack> outputPigments;
    private final Set<SlurryStack> outputSlurries;

    private final int duration;
    private final FloatingLong energyRequired;
    private final int circuitType;
    private final EnumSet<ReactionCondition> conditions;

    public ChemicalReactionRecipe(ResourceLocation id,
                                  List<ItemStackIngredient> inputItems,
                                  List<FluidStackIngredient> inputFluids,
                                  List<GasStackIngredient> inputGases,
                                  List<InfusionStackIngredient> inputInfusions,
                                  List<PigmentStackIngredient> inputPigments,
                                  List<SlurryStackIngredient> inputSlurries,
                                  Set<ItemStack> outputItems,
                                  Set<FluidStack> outputFluids,
                                  Set<GasStack> outputGases,
                                  Set<InfusionStack> outputInfusions,
                                  Set<PigmentStack> outputPigments,
                                  Set<SlurryStack> outputSlurries,
                                  int duration, FloatingLong energyRequired,
                                  int circuitType,
                                  EnumSet<ReactionCondition> conditions) {
        super(id);
        if (duration <= 0) throw new IllegalArgumentException("Duration must be positive.");

        // We don't check if any of the inputs or outputs is null as that
        // would be troublesome and laggy
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
        if (conditions.size() > 2) throw new IllegalArgumentException("Required conditions must not be more than two.");
        this.circuitType = circuitType;
        this.conditions = conditions;
    }

    public List<ItemStackIngredient> getInputItems() {
        return inputItems;
    }

    public List<FluidStackIngredient> getInputFluids() {
        return inputFluids;
    }

    public List<GasStackIngredient> getInputGases() {
        return inputGases;
    }

    public List<InfusionStackIngredient> getInputInfusions() {
        return inputInfusions;
    }

    public List<PigmentStackIngredient> getInputPigments() {
        return inputPigments;
    }

    public List<SlurryStackIngredient> getInputSlurries() {
        return inputSlurries;
    }

    public int getDuration() {
        return duration;
    }

    public FloatingLong getEnergyRequired() {
        return energyRequired;
    }

    public int getCircuitType() {
        return circuitType;
    }

    public EnumSet<ReactionCondition> getConditions() {
        return conditions;
    }

    /**
     * For JEI, gets the output representations to display.
     *
     * @return Representation of the output, <strong>MUST NOT</strong> be modified.
     */
    public List<ChemicalReactionRecipeJEIOutput> getOutputDefinition() {
        return List.of(
                new ChemicalReactionRecipeJEIOutput(outputItems, outputFluids, outputGases, outputInfusions, outputPigments, outputSlurries)
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
    public ChemicalReactionRecipeJEIOutput getOutput(Set<@NotNull ItemStack> itemStacks, Set<@NotNull FluidStack> fluidStacks,
                                                     Set<@NotNull GasStack> gasStacks, Set<@NotNull InfusionStack> infusionStacks,
                                                     Set<@NotNull PigmentStack> pigmentStacks, Set<@NotNull SlurryStack> slurryStacks) {
        return new ChemicalReactionRecipeJEIOutput(outputItems, outputFluids, outputGases, outputInfusions, outputPigments, outputSlurries);
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
        buffer.writeInt(circuitType);
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

    /**
     * @apiNote Though not explicitly checked, at least one output must be
     * non-empty. Also, no null values are allowed. Use empty stacks or sets
     * instead.
     */
    public record ChemicalReactionRecipeJEIOutput(@NotNull Set<@NotNull ItemStack> outputItems,
                                                  @NotNull Set<@NotNull FluidStack> outputFluids,
                                                  @NotNull Set<@NotNull GasStack> outputGases,
                                                  @NotNull Set<@NotNull InfusionStack> outputInfusions,
                                                  @NotNull Set<@NotNull PigmentStack> outputPigments,
                                                  @NotNull Set<@NotNull SlurryStack> outputSlurries) {
    }

    /**
     * @apiNote Though this record has the same structure with {@link ChemicalReactionRecipeJEIOutput},
     * its fields have to be declared to be lists. We need to search the stacks
     * to consume in the order from the first slot or tank to the last, which
     * requires them to be ordered.
     */
    public record ChemicalReactionRecipeInput(@NotNull List<@NotNull ItemStack> itemStacks,
                                              @NotNull List<@NotNull FluidStack> fluidStacks,
                                              @NotNull List<@NotNull GasStack> gasStacks,
                                              @NotNull List<@NotNull InfusionStack> infusionStacks,
                                              @NotNull List<@NotNull PigmentStack> pigmentStacks,
                                              @NotNull List<@NotNull SlurryStack> slurryStacks) {

        public static final ChemicalReactionRecipeInput INVALID_INPUT = new ChemicalReactionRecipeInput(
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of()
        );

        public boolean invalid() {
            return (this == INVALID_INPUT) || (itemStacks.isEmpty() && fluidStacks.isEmpty() && gasStacks.isEmpty()
                    && infusionStacks.isEmpty() && pigmentStacks.isEmpty() && slurryStacks.isEmpty());
        }
    }
}
