package com.jerry.mekanism_extras.common.recipes.ingredient;

import com.google.gson.JsonObject;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.common.recipe.ingredient.chemical.ChemicalIngredientInfo;
import mekanism.common.recipe.ingredient.chemical.MultiChemicalStackIngredient;
import mekanism.common.recipe.ingredient.chemical.SingleChemicalStackIngredient;
import mekanism.common.recipe.ingredient.chemical.TaggedChemicalStackIngredient;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class FeaturedMultiChemicalStackIngredient<CHEMICAL extends Chemical<CHEMICAL>,
        STACK extends ChemicalStack<CHEMICAL>, INGREDIENT extends ChemicalStackIngredient<CHEMICAL, STACK>>
        implements ChemicalStackIngredient<CHEMICAL, STACK>, FeaturedMultiIngredient<STACK, INGREDIENT> {

    private final INGREDIENT ingredient;
    private final long featureMask;
    @Unmodifiable
    private final List<Feature<?>> features;

    FeaturedMultiChemicalStackIngredient(INGREDIENT ingredient, Feature<?>... features) {
        this.ingredient = ingredient;
        this.features = Arrays.stream(features).toList();
        this.featureMask = this.features.stream()
                .map(Feature::getMask)
                .reduce((l1, l2) -> l1 | l2)
                .orElse(0L);
    }

    protected abstract ChemicalIngredientInfo<CHEMICAL, STACK> getIngredientInfo();

    @Override
    public boolean forEachIngredient(Predicate<INGREDIENT> checker) {
        if (ingredient instanceof SingleChemicalStackIngredient || ingredient instanceof TaggedChemicalStackIngredient) {
            return checker.test(ingredient);
        } else {
            //noinspection unchecked
            return ((MultiChemicalStackIngredient<CHEMICAL, STACK, INGREDIENT>) ingredient).forEachIngredient(checker);
        }
    }

    @Override
    public List<INGREDIENT> getIngredients() {
        if (ingredient instanceof SingleChemicalStackIngredient || ingredient instanceof TaggedChemicalStackIngredient) {
            return List.of(ingredient);
        } else {
            //noinspection unchecked
            return ((MultiChemicalStackIngredient<CHEMICAL, STACK, INGREDIENT>) ingredient).getIngredients();
        }
    }

    @Override
    public boolean testType(@NotNull STACK stack) {
        return ingredient.testType(stack);
    }

    @Override
    public @NotNull STACK getMatchingInstance(@NotNull STACK stack) {
        return ingredient.getMatchingInstance(stack);
    }

    @Override
    public long getNeededAmount(@NotNull STACK stack) {
        return ingredient.getNeededAmount(stack);
    }

    @Override
    public List<@NotNull STACK> getRepresentations() {
        return ingredient.getRepresentations();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        ingredient.write(buffer);
        FeatureNetworkHandler.writeFeatures(buffer, features.toArray(Feature[]::new));
    }

    @Override
    public JsonObject serialize() {
        JsonObject obj = ingredient.serialize().getAsJsonObject();
        return FeatureJSONHandler.serialize(obj, features);
    }

    @Override
    public boolean test(@NotNull STACK stack) {
        return ingredient.test(stack);
    }

    @Override
    public boolean testType(@NotNull CHEMICAL chemical) {
        return ingredient.testType(chemical);
    }

    @Override
    public boolean hasFeature(long mask) {
        return (featureMask & mask) != 0;
    }

    @Override
    public Optional<Feature<?>> getFeature(long mask) {
        return features.stream()
                .filter(feature -> feature.getMask() == mask)
                .findFirst();
    }

    @Override
    public Optional<Feature<?>> getFeature(String id) {
        return features.stream()
                .filter(feature -> Objects.equals(feature.getId(), id))
                .findFirst();
    }

    public static class FeaturedMultiGasStackIngredient extends FeaturedMultiChemicalStackIngredient<Gas, GasStack, GasStackIngredient> {

        FeaturedMultiGasStackIngredient(GasStackIngredient ingredient, Feature<?>... features) {
            super(ingredient, features);
        }

        @Override
        protected ChemicalIngredientInfo<Gas, GasStack> getIngredientInfo() {
            return ChemicalIngredientInfo.GAS;
        }
    }

    public static class FeaturedMultiInfusionStackIngredient extends FeaturedMultiChemicalStackIngredient<InfuseType, InfusionStack, InfusionStackIngredient> {

        FeaturedMultiInfusionStackIngredient(InfusionStackIngredient ingredient, Feature<?>... features) {
            super(ingredient, features);
        }

        @Override
        protected ChemicalIngredientInfo<InfuseType, InfusionStack> getIngredientInfo() {
            return ChemicalIngredientInfo.INFUSION;
        }
    }

    public static class FeaturedMultiPigmentStackIngredient extends FeaturedMultiChemicalStackIngredient<Pigment, PigmentStack, PigmentStackIngredient> {

        FeaturedMultiPigmentStackIngredient(PigmentStackIngredient ingredient, Feature<?>... features) {
            super(ingredient, features);
        }

        @Override
        protected ChemicalIngredientInfo<Pigment, PigmentStack> getIngredientInfo() {
            return ChemicalIngredientInfo.PIGMENT;
        }
    }

    public static class FeaturedMultiSlurryStackIngredient extends FeaturedMultiChemicalStackIngredient<Slurry, SlurryStack, SlurryStackIngredient> {

        FeaturedMultiSlurryStackIngredient(SlurryStackIngredient ingredient, Feature<?>... features) {
            super(ingredient, features);
        }

        @Override
        protected ChemicalIngredientInfo<Slurry, SlurryStack> getIngredientInfo() {
            return ChemicalIngredientInfo.SLURRY;
        }
    }
}
