package com.jerry.mekanism_extras.common.recipes.ingredient;

import com.google.gson.JsonObject;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.recipe.ingredient.creator.ItemStackIngredientCreator.MultiItemStackIngredient;
import mekanism.common.recipe.ingredient.creator.ItemStackIngredientCreator.SingleItemStackIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;

public class FeaturedMultiItemStackIngredient
        extends ItemStackIngredient
        implements FeaturedMultiIngredient<ItemStack, ItemStackIngredient> {

    private final ItemStackIngredient ingredient;
    private final long featureMask;
    @Unmodifiable
    private final List<Feature<?>> features;

    FeaturedMultiItemStackIngredient(ItemStackIngredient ingredient, Feature<?>... features) {
        this.ingredient = ingredient;
        this.features = Arrays.stream(features).toList();
        this.featureMask = this.features.stream()
                .map(Feature::getMask)
                .reduce((l1, l2) -> l1 | l2)
                .orElse(0L);
    }

    @Override
    public boolean forEachIngredient(Predicate<ItemStackIngredient> checker) {
        if (ingredient instanceof SingleItemStackIngredient single) {
            return checker.test(single);
        } else {
            return ((MultiItemStackIngredient) ingredient).forEachIngredient(checker);
        }
    }

    @Override
    public List<ItemStackIngredient> getIngredients() {
        if (ingredient instanceof SingleItemStackIngredient) {
            return List.of(ingredient);
        } else {
            return ((MultiItemStackIngredient) ingredient).getIngredients();
        }
    }

    @Override
    public boolean testType(@NotNull ItemStack stack) {
        return ingredient.testType(stack);
    }

    @Override
    public @NotNull ItemStack getMatchingInstance(@NotNull ItemStack stack) {
        return ingredient.getMatchingInstance(stack);
    }

    @Override
    public long getNeededAmount(@NotNull ItemStack stack) {
        return ingredient.getNeededAmount(stack);
    }

    @Override
    public List<@NotNull ItemStack> getRepresentations() {
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
    public boolean test(@NotNull ItemStack stack) {
        return ingredient.test(stack);
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
