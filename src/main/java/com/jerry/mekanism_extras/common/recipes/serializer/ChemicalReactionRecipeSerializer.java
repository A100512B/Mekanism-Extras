package com.jerry.mekanism_extras.common.recipes.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.api.ExtraJsonConstants;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.InfusionStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.PigmentStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.SlurryStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.Mekanism;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChemicalReactionRecipeSerializer implements RecipeSerializer<ChemicalReactionRecipe> {

    private final IFactory factory;

    public ChemicalReactionRecipeSerializer(IFactory factory) {
        this.factory = factory;
    }

    @Override
    @NotNull
    public ChemicalReactionRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        int circuitType = 0;
        if (json.has(ExtraJsonConstants.CIRCUIT_TYPE))
            circuitType = json.get(ExtraJsonConstants.CIRCUIT_TYPE).getAsInt();

        List<ItemStackIngredient> inputItems = new ArrayList<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.ITEM_INPUTS)) {
            JsonArray itemInputsArray = json.getAsJsonArray(ExtraJsonConstants.ITEM_INPUTS);
            itemInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputItems.add(IngredientCreatorAccess.item().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)));
            });
        }

        List<FluidStackIngredient> inputFluids = new ArrayList<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.FLUID_INPUTS)) {
            JsonArray fluidInputsArray = json.getAsJsonArray(ExtraJsonConstants.FLUID_INPUTS);
            fluidInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputFluids.add(IngredientCreatorAccess.fluid().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)));
            });
        }

        List<GasStackIngredient> inputGases = new ArrayList<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.GAS_INPUTS)) {
            JsonArray gasInputsArray = json.getAsJsonArray(ExtraJsonConstants.GAS_INPUTS);
            gasInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputGases.add(IngredientCreatorAccess.gas().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)));
            });
        }

        List<InfusionStackIngredient> inputInfusions = new ArrayList<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.INFUSION_INPUTS)) {
            JsonArray infusionInputsArray = json.getAsJsonArray(ExtraJsonConstants.INFUSION_INPUTS);
            infusionInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputInfusions.add(IngredientCreatorAccess.infusion().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)));
            });
        }

        List<PigmentStackIngredient> inputPigments = new ArrayList<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.PIGMENT_INPUTS)) {
            JsonArray pigmentInputsArray = json.getAsJsonArray(ExtraJsonConstants.PIGMENT_INPUTS);
            pigmentInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputPigments.add(IngredientCreatorAccess.pigment().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)));
            });
        }

        List<SlurryStackIngredient> inputSlurries = new ArrayList<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.SLURRY_INPUTS)) {
            JsonArray slurryInputsArray = json.getAsJsonArray(ExtraJsonConstants.SLURRY_INPUTS);
            slurryInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputSlurries.add(IngredientCreatorAccess.slurry().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)));
            });
        }

        if (inputItems.isEmpty() && inputFluids.isEmpty() && inputGases.isEmpty() && inputInfusions.isEmpty() && inputPigments.isEmpty() && inputSlurries.isEmpty())
            throw new JsonSyntaxException("Expected at least one input.");

        FloatingLong energyRequired = FloatingLong.ZERO;
        if (json.has(JsonConstants.ENERGY_REQUIRED)) {
            energyRequired = SerializerHelper.getFloatingLong(json, JsonConstants.ENERGY_REQUIRED);
        }

        JsonElement ticks = json.get(JsonConstants.DURATION);
        if (!GsonHelper.isNumberValue(ticks)) {
            throw new JsonSyntaxException("Expected duration to be a number greater than zero.");
        }

        Set<ItemStack> outputItems = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.ITEM_OUTPUTS)) {
            JsonArray itemOutputsArray = json.getAsJsonArray(ExtraJsonConstants.ITEM_OUTPUTS);
            itemOutputsArray.forEach(e -> {
                ItemStack is = ShapedRecipe.itemStackFromJson(e.getAsJsonObject());
                if (is.isEmpty()) throw new JsonSyntaxException("Chemical Reaction Chamber item output must not be empty, if it is defined.");
                outputItems.add(is);
            });
        }

        Set<FluidStack> outputFluids = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.FLUID_OUTPUTS)) {
            JsonArray fluidOutputsArray = json.getAsJsonArray(ExtraJsonConstants.FLUID_OUTPUTS);
            fluidOutputsArray.forEach(e -> {
                FluidStack fs = SerializerHelper.deserializeFluid(e.getAsJsonObject());
                if (fs.isEmpty()) throw new JsonSyntaxException("Chemical Reaction Chamber fluid output must not be empty, if it is defined.");
                outputFluids.add(fs);
            });
        }

        Set<GasStack> outputGases = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.GAS_OUTPUTS)) {
            JsonArray gasOutputsArray = json.getAsJsonArray(ExtraJsonConstants.GAS_OUTPUTS);
            gasOutputsArray.forEach(e -> {
                GasStack gs = SerializerHelper.deserializeGas(e.getAsJsonObject());
                if (gs.isEmpty()) throw new JsonSyntaxException("Chemical Reaction Chamber gas output must not be empty, if it is defined.");
                outputGases.add(gs);
            });
        }

        Set<InfusionStack> outputInfusions = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.INFUSION_OUTPUTS)) {
            JsonArray infusionOutputsArray = json.getAsJsonArray(ExtraJsonConstants.INFUSION_OUTPUTS);
            infusionOutputsArray.forEach(e -> {
                InfusionStack is = SerializerHelper.deserializeInfuseType(e.getAsJsonObject());
                if (is.isEmpty()) throw new JsonSyntaxException("Chemical Reaction Chamber infusion output must not be empty, if it is defined.");
                outputInfusions.add(is);
            });
        }

        Set<PigmentStack> outputPigments = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.PIGMENT_OUTPUTS)) {
            JsonArray pigmentOutputsArray = json.getAsJsonArray(ExtraJsonConstants.PIGMENT_OUTPUTS);
            pigmentOutputsArray.forEach(e -> {
                PigmentStack ps = SerializerHelper.deserializePigment(e.getAsJsonObject());
                if (ps.isEmpty()) throw new JsonSyntaxException("Chemical Reaction Chamber pigment output must not be empty, if it is defined.");
                outputPigments.add(ps);
            });
        }

        Set<SlurryStack> outputSlurries = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.SLURRY_OUTPUTS)) {
            JsonArray slurryOutputsArray = json.getAsJsonArray(ExtraJsonConstants.SLURRY_OUTPUTS);
            slurryOutputsArray.forEach(e -> {
                SlurryStack ss = SerializerHelper.deserializeSlurry(e.getAsJsonObject());
                if (ss.isEmpty()) throw new JsonSyntaxException("Chemical Reaction Chamber slurry output must not be empty, if it is defined.");
                outputSlurries.add(ss);
            });
        }

        if (outputItems.isEmpty() && outputFluids.isEmpty() && outputGases.isEmpty() && outputInfusions.isEmpty() && outputPigments.isEmpty() && outputSlurries.isEmpty())
            throw new JsonSyntaxException("Expected at least one output.");

        EnumSet<ChemicalReactorMultiblockData.ReactionCondition> conditions = EnumSet.noneOf(ChemicalReactorMultiblockData.ReactionCondition.class);
        if (json.has(ExtraJsonConstants.CONDITIONS_REQUIRED)) {
            JsonArray conditionsArray = json.getAsJsonArray(ExtraJsonConstants.CONDITIONS_REQUIRED);
            conditionsArray.forEach(e -> conditions.add(ChemicalReactorMultiblockData.ReactionCondition.byId(e.getAsString())));
        }

        return factory.create(id, inputItems, inputFluids, inputGases, inputInfusions, inputPigments, inputSlurries,
                outputItems, outputFluids, outputGases, outputInfusions, outputPigments, outputSlurries, ticks.getAsInt(),
                energyRequired, circuitType, conditions);
    }

    @Override
    @Nullable
    public ChemicalReactionRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        try {
            int circuitType = buffer.readInt();

            int inputItemsSize = buffer.readVarInt();
            List<ItemStackIngredient> inputItems = new ArrayList<>();
            for (int i = 0; i < inputItemsSize; i++) {
                inputItems.add(IngredientCreatorAccess.item().read(buffer));
            }
            int inputFluidsSize = buffer.readVarInt();
            List<FluidStackIngredient> inputFluids = new ArrayList<>();
            for (int i = 0; i < inputFluidsSize; i++) {
                inputFluids.add(IngredientCreatorAccess.fluid().read(buffer));
            }
            int inputGasesSize = buffer.readVarInt();
            List<GasStackIngredient> inputGases = new ArrayList<>();
            for (int i = 0; i < inputGasesSize; i++) {
                inputGases.add(IngredientCreatorAccess.gas().read(buffer));
            }
            int inputInfusionsSize = buffer.readVarInt();
            List<InfusionStackIngredient> inputInfusions = new ArrayList<>();
            for (int i = 0; i < inputInfusionsSize; i++) {
                inputInfusions.add(IngredientCreatorAccess.infusion().read(buffer));
            }
            int inputPigmentsSize = buffer.readVarInt();
            List<PigmentStackIngredient> inputPigments = new ArrayList<>();
            for (int i = 0; i < inputPigmentsSize; i++) {
                inputPigments.add(IngredientCreatorAccess.pigment().read(buffer));
            }
            int inputSlurriesSize = buffer.readVarInt();
            List<SlurryStackIngredient> inputSlurries = new ArrayList<>();
            for (int i = 0; i < inputSlurriesSize; i++) {
                inputSlurries.add(IngredientCreatorAccess.slurry().read(buffer));
            }
            int outputItemsSize = buffer.readVarInt();
            Set<ItemStack> outputItems = new HashSet<>();
            for (int i = 0; i < outputItemsSize; i++) {
                outputItems.add(buffer.readItem());
            }
            int outputFluidsSize = buffer.readVarInt();
            Set<FluidStack> outputFluids = new HashSet<>();
            for (int i = 0; i < outputFluidsSize; i++) {
                outputFluids.add(buffer.readFluidStack());
            }
            int outputGasesSize = buffer.readVarInt();
            Set<GasStack> outputGases = new HashSet<>();
            for (int i = 0; i < outputGasesSize; i++) {
                outputGases.add(GasStack.readFromPacket(buffer));
            }
            int outputInfusionsSize = buffer.readVarInt();
            Set<InfusionStack> outputInfusions = new HashSet<>();
            for (int i = 0; i < outputInfusionsSize; i++) {
                outputInfusions.add(InfusionStack.readFromPacket(buffer));
            }
            int outputPigmentsSize = buffer.readVarInt();
            Set<PigmentStack> outputPigments = new HashSet<>();
            for (int i = 0; i < outputPigmentsSize; i++) {
                outputPigments.add(PigmentStack.readFromPacket(buffer));
            }
            int outputSlurriesSize = buffer.readVarInt();
            Set<SlurryStack> outputSlurries = new HashSet<>();
            for (int i = 0; i < outputSlurriesSize; i++) {
                outputSlurries.add(SlurryStack.readFromPacket(buffer));
            }
            int duration = buffer.readVarInt();
            FloatingLong energyRequired = FloatingLong.readFromBuffer(buffer);
            EnumSet<ChemicalReactorMultiblockData.ReactionCondition> conditions = buffer.readEnumSet(ChemicalReactorMultiblockData.ReactionCondition.class);
            return factory.create(id, inputItems, inputFluids, inputGases, inputInfusions, inputPigments, inputSlurries,
                    outputItems, outputFluids, outputGases, outputInfusions, outputPigments, outputSlurries,
                    duration, energyRequired, circuitType, conditions);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading chemical reaction recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ChemicalReactionRecipe recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            MekanismExtras.LOGGER.error("Error writing chemical reaction recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory {

        ChemicalReactionRecipe create(ResourceLocation id,
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
                                      EnumSet<ChemicalReactorMultiblockData.ReactionCondition> conditions);
    }
}
