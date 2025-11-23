package com.jerry.mekanism_extras.common.recipes.ingredient;

import com.google.gson.JsonObject;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.common.recipe.ingredient.creator.FluidStackIngredientCreator.MultiFluidStackIngredient;
import mekanism.common.recipe.ingredient.creator.FluidStackIngredientCreator.SingleFluidStackIngredient;
import mekanism.common.recipe.ingredient.creator.FluidStackIngredientCreator.TaggedFluidStackIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;

public class FeaturedMultiFluidStackIngredient
        extends FluidStackIngredient
        implements FeaturedMultiIngredient<FluidStack, FluidStackIngredient> {

    private final FluidStackIngredient ingredient;
    private final long featureMask;
    @Unmodifiable
    private final List<Feature<?>> features;

    public FeaturedMultiFluidStackIngredient(FluidStackIngredient ingredient, Feature<?>... features) {
        this.ingredient = ingredient;
        this.features = Arrays.stream(features).toList();
        this.featureMask = this.features.stream()
                .map(Feature::getMask)
                .reduce((l1, l2) -> l1 | l2)
                .orElse(0L);
    }

    @Override
    public boolean handleable() {
        return forEachIngredient(i -> i instanceof SingleFluidStackIngredient ||
                i instanceof TaggedFluidStackIngredient ||
                i instanceof MultiFluidStackIngredient);
    }

    @Override
    public boolean forEachIngredient(Predicate<FluidStackIngredient> checker) {
        if (isIngredientSingle(ingredient)) {
            return checker.test(ingredient);
        } else {
            return ((MultiFluidStackIngredient) ingredient).forEachIngredient(checker);
        }
    }

    @Override
    public List<FluidStackIngredient> getIngredients() {
        if (isIngredientSingle(ingredient)) {
            return List.of(ingredient);
        } else {
            return ((MultiFluidStackIngredient) ingredient).getIngredients();
        }
    }

    @Override
    public boolean testType(@NotNull FluidStack fluidStack) {
        return ingredient.testType(fluidStack);
    }

    @Override
    public @NotNull FluidStack getMatchingInstance(@NotNull FluidStack fluidStack) {
        return ingredient.getMatchingInstance(fluidStack);
    }

    @Override
    public long getNeededAmount(@NotNull FluidStack fluidStack) {
        return ingredient.getNeededAmount(fluidStack);
    }

    @Override
    public List<@NotNull FluidStack> getRepresentations() {
        return ingredient.getRepresentations();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        ingredient.write(buffer);
        FeatureNetworkHandler.write(buffer, features.toArray(Feature[]::new));
    }

    @Override
    public JsonObject serialize() {
        JsonObject obj = ingredient.serialize().getAsJsonObject();
        return FeatureJSONHandler.serialize(obj, features);
    }

    @Override
    public boolean test(@NotNull FluidStack fluidStack) {
        return ingredient.test(fluidStack);
    }

    private boolean isIngredientSingle(FluidStackIngredient ingredient) {
        return ingredient instanceof SingleFluidStackIngredient ||
                ingredient instanceof TaggedFluidStackIngredient;
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
}
