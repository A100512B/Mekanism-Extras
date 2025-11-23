package com.jerry.mekanism_extras.common.recipes.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.api.ExtraJsonConstants;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiGasStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiInfusionStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiPigmentStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiChemicalStackIngredient.FeaturedMultiSlurryStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiFluidStackIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiIngredient;
import com.jerry.mekanism_extras.common.recipes.ingredient.FeaturedMultiItemStackIngredient;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.FloatingLong;
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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ChemicalReactionRecipeSerializer implements RecipeSerializer<ChemicalReactionRecipe> {

    private final IFactory factory;

    public ChemicalReactionRecipeSerializer(IFactory factory) {
        this.factory = factory;
    }

    /**
     * JSON format:
     * <code>
     * <pre>
     * {
     *   "itemInputs": [
     *     {
     *       "ingredients": someIngredients...,
     *       "nonConsumable": true,
     *       "someOtherFeatures": ...
     *     },
     *     {
     *       someSimilarObjects...
     *     }
     *   ],
     *   "otherSimilarInputs": ...,
     *   "itemOutputs": [
     *     someStackDefinitions...
     *   ],
     *   "otherSimilarOutputs": ...,
     *   "conditionsRequired": [
     *     "someConditions"
     *   ]
     * }
     * </pre>
     * </code>
     */
    @Override
    public ChemicalReactionRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        // Holy shit!!!!! Why did Mekanism create four chemical types but not unify them into one in 1.20.1?
        Set<FeaturedMultiItemStackIngredient> inputItems = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.ITEM_INPUTS)) {
            JsonArray itemInputsArray = json.getAsJsonArray(ExtraJsonConstants.ITEM_INPUTS);
            itemInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputItems.add(new FeaturedMultiItemStackIngredient(
                        IngredientCreatorAccess.item().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)),
                        FeaturedMultiIngredient.FeatureJSONHandler.deserialize(obj).toArray(FeaturedMultiIngredient.Feature[]::new)
                ));
            });
        }

        Set<FeaturedMultiFluidStackIngredient> inputFluids = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.FLUID_INPUTS)) {
            JsonArray fluidInputsArray = json.getAsJsonArray(ExtraJsonConstants.FLUID_INPUTS);
            fluidInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputFluids.add(new FeaturedMultiFluidStackIngredient(
                        IngredientCreatorAccess.fluid().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)),
                        FeaturedMultiIngredient.FeatureJSONHandler.deserialize(obj).toArray(FeaturedMultiIngredient.Feature[]::new)
                ));
            });
        }

        Set<FeaturedMultiGasStackIngredient> inputGases = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.GAS_INPUTS)) {
            JsonArray gasInputsArray = json.getAsJsonArray(ExtraJsonConstants.GAS_INPUTS);
            gasInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputGases.add(new FeaturedMultiGasStackIngredient(
                        IngredientCreatorAccess.gas().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)),
                        FeaturedMultiIngredient.FeatureJSONHandler.deserialize(obj).toArray(FeaturedMultiIngredient.Feature[]::new)
                ));
            });
        }

        Set<FeaturedMultiInfusionStackIngredient> inputInfusions = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.INFUSION_INPUTS)) {
            JsonArray infusionInputsArray = json.getAsJsonArray(ExtraJsonConstants.INFUSION_INPUTS);
            infusionInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputInfusions.add(new FeaturedMultiInfusionStackIngredient(
                        IngredientCreatorAccess.infusion().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)),
                        FeaturedMultiIngredient.FeatureJSONHandler.deserialize(obj).toArray(FeaturedMultiIngredient.Feature[]::new)
                ));
            });
        }

        Set<FeaturedMultiPigmentStackIngredient> inputPigments = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.PIGMENT_INPUTS)) {
            JsonArray pigmentInputsArray = json.getAsJsonArray(ExtraJsonConstants.PIGMENT_INPUTS);
            pigmentInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputPigments.add(new FeaturedMultiPigmentStackIngredient(
                        IngredientCreatorAccess.pigment().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)),
                        FeaturedMultiIngredient.FeatureJSONHandler.deserialize(obj).toArray(FeaturedMultiIngredient.Feature[]::new)
                ));
            });
        }

        Set<FeaturedMultiSlurryStackIngredient> inputSlurries = new HashSet<>();
        if (GsonHelper.isArrayNode(json, ExtraJsonConstants.SLURRY_INPUTS)) {
            JsonArray slurryInputsArray = json.getAsJsonArray(ExtraJsonConstants.SLURRY_INPUTS);
            slurryInputsArray.forEach(e -> {
                JsonObject obj = e.getAsJsonObject();
                inputSlurries.add(new FeaturedMultiSlurryStackIngredient(
                        IngredientCreatorAccess.slurry().deserialize(obj.get(ExtraJsonConstants.INGREDIENTS)),
                        FeaturedMultiIngredient.FeatureJSONHandler.deserialize(obj).toArray(FeaturedMultiIngredient.Feature[]::new)
                ));
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
                energyRequired, conditions);
    }

    @Override
    @Nullable
    public ChemicalReactionRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        try {
            int inputItemsSize = buffer.readVarInt();
            Set<FeaturedMultiItemStackIngredient> inputItems = new HashSet<>();
            for (int i = 0; i < inputItemsSize; i++) {
                inputItems.add(new FeaturedMultiItemStackIngredient(IngredientCreatorAccess.item().read(buffer),
                        FeaturedMultiIngredient.FeatureNetworkHandler.read(buffer).toArray(FeaturedMultiIngredient.Feature[]::new)));
            }
            int inputFluidsSize = buffer.readVarInt();
            Set<FeaturedMultiFluidStackIngredient> inputFluids = new HashSet<>();
            for (int i = 0; i < inputFluidsSize; i++) {
                inputFluids.add(new FeaturedMultiFluidStackIngredient(IngredientCreatorAccess.fluid().read(buffer),
                        FeaturedMultiIngredient.FeatureNetworkHandler.read(buffer).toArray(FeaturedMultiIngredient.Feature[]::new)));
            }
            int inputGasesSize = buffer.readVarInt();
            Set<FeaturedMultiGasStackIngredient> inputGases = new HashSet<>();
            for (int i = 0; i < inputGasesSize; i++) {
                inputGases.add(new FeaturedMultiGasStackIngredient(IngredientCreatorAccess.gas().read(buffer),
                        FeaturedMultiIngredient.FeatureNetworkHandler.read(buffer).toArray(FeaturedMultiIngredient.Feature[]::new)));
            }
            int inputInfusionsSize = buffer.readVarInt();
            Set<FeaturedMultiInfusionStackIngredient> inputInfusions = new HashSet<>();
            for (int i = 0; i < inputInfusionsSize; i++) {
                inputInfusions.add(new FeaturedMultiInfusionStackIngredient(IngredientCreatorAccess.infusion().read(buffer),
                        FeaturedMultiIngredient.FeatureNetworkHandler.read(buffer).toArray(FeaturedMultiIngredient.Feature[]::new)));
            }
            int inputPigmentsSize = buffer.readVarInt();
            Set<FeaturedMultiPigmentStackIngredient> inputPigments = new HashSet<>();
            for (int i = 0; i < inputPigmentsSize; i++) {
                inputPigments.add(new FeaturedMultiPigmentStackIngredient(IngredientCreatorAccess.pigment().read(buffer),
                        FeaturedMultiIngredient.FeatureNetworkHandler.read(buffer).toArray(FeaturedMultiIngredient.Feature[]::new)));
            }
            int inputSlurriesSize = buffer.readVarInt();
            Set<FeaturedMultiSlurryStackIngredient> inputSlurries = new HashSet<>();
            for (int i = 0; i < inputSlurriesSize; i++) {
                inputSlurries.add(new FeaturedMultiSlurryStackIngredient(IngredientCreatorAccess.slurry().read(buffer),
                        FeaturedMultiIngredient.FeatureNetworkHandler.read(buffer).toArray(FeaturedMultiIngredient.Feature[]::new)));
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
                    duration, energyRequired, conditions);
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
                                      EnumSet<ChemicalReactorMultiblockData.ReactionCondition> conditions);
    }
}
