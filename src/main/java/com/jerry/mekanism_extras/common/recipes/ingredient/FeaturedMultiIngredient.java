package com.jerry.mekanism_extras.common.recipes.ingredient;

import com.google.gson.JsonObject;
import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.api.ExtraJsonConstants;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.common.recipe.ingredient.IMultiIngredient;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * Extended implementation that adds features, like non-consumable，to the
 * {@link IMultiIngredient}s.
 */
public interface FeaturedMultiIngredient<TYPE, INGREDIENT extends InputIngredient<@NotNull TYPE>>
        extends IMultiIngredient<TYPE, INGREDIENT> {

    boolean hasFeature(long mask);

    default boolean hasFeature(Feature<?> feature) {
        return hasFeature(feature.mask);
    }

    Optional<Feature<?>> getFeature(long mask);

    Optional<Feature<?>> getFeature(String id);

    default Optional<Feature<?>> getFeature(Feature<?> feature) {
        return getFeature(feature.mask);
    }

    /**
     * Describes a feature of a {@link FeaturedMultiIngredient}.
     * @param <T> The parameter type of this feature.
     */
    abstract class Feature<T> {

        /**
         * Used for JSON serialization.
         */
        private final String id;
        /**
         * The value stored by this feature. The value type depends on specific
         * feature types.
         */
        private final T value;
        /**
         * Used for network transferring.
         * @apiNote Currently, we only make use of the lowest bit, which represents
         * non-consumable, but it's important to reserve a whole long number for
         * future updates and compatibility between all the addons.
         */
        private final long mask;

        protected Feature(String id, T value, long mask) {
            this.id = id;
            this.value = value;
            this.mask = mask;
        }

        public String getId() {
            return id;
        }

        public T getValue() {
            return value;
        }

        public long getMask() {
            return mask;
        }

        public abstract void write(FriendlyByteBuf buf);

        public abstract JsonObject serialize(JsonObject base);
    }

    class NonConsumable extends Feature<Boolean> {

        public NonConsumable(boolean value) {
            super(ExtraJsonConstants.NON_CONSUMABLE, value, 0x00000001);
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeBoolean(getValue());
        }

        @Override
        public JsonObject serialize(JsonObject base) {
            JsonObject obj = base.deepCopy();
            obj.addProperty(getId(), getValue());
            return obj;
        }
    }

    final class FeatureNetworkHandler {

        public static final Map<Long, FriendlyByteBuf.Reader<? extends Feature<?>>> READER_MAP
                 = new HashMap<>();

        static {
            READER_MAP.put(0x1L, buf -> new NonConsumable(buf.readBoolean()));
        }

        public static void registerReader(long mask, FriendlyByteBuf.Reader<? extends Feature<?>> reader) {
            if (READER_MAP.putIfAbsent(mask, reader) != null) {
                MekanismExtras.LOGGER.warn("Try to overwrite feature reader for the mask {}, denied.", Long.toHexString(mask));
            }
        }

        /**
         * Writes the given features into the buffer by the order of mask in
         * small-endian.
         * @param buf The buffer.
         * @param features The given features. Should not contain features
         *                 of the same type, though not explicitly checked.
         */
        public static void writeFeatures(FriendlyByteBuf buf, Feature<?>... features) {
            long mask = Arrays.stream(features)
                    .map(Feature::getMask)
                    .reduce((i1, i2) -> i1 | i2)
                    .orElse(0L);
            buf.writeLong(mask);
            // If the input features include multiple same features, this will
            // go wrong, but we hope modders won't do this.
            Arrays.stream(features)
                    // To sort by small-endian, we swap f1 and f2
                    .sorted((f1, f2) -> Long.compare(f2.mask, f1.mask))
                    .forEach(feature -> feature.write(buf));
        }

        /**
         * Reads features out of the given buffer by the order of mask in small-endian.
         * @param buf The buffer.
         * @return The unmodifiable list of features. May be empty.
         */
        public static List<Feature<?>> readFeatures(FriendlyByteBuf buf) {
            long mask = buf.readLong();
            if (mask == 0L) return List.of();

            List<Feature<?>> result = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                if (((mask >> i) & 1) == 1) {
                    if (READER_MAP.containsKey(1L << i)) {
                        result.add(READER_MAP.get(1L << i).apply(buf));
                    } else {
                        MekanismExtras.LOGGER.warn("Failed to parse bit {}", Long.toHexString(1L << i));
                    }
                }
            }
            return result;
        }

    }

    final class FeatureJSONHandler {

        public static final Map<String, Function<JsonObject, ? extends Feature<?>>> READER_MAP
                = new HashMap<>();

        static {
            READER_MAP.put(ExtraJsonConstants.NON_CONSUMABLE,
                    obj -> new NonConsumable(obj.get(ExtraJsonConstants.NON_CONSUMABLE).getAsBoolean())
            );
        }

        public static void registerReader(String id, Function<JsonObject, ? extends Feature<?>> reader) {
            if (READER_MAP.putIfAbsent(id, reader) != null) {
                MekanismExtras.LOGGER.warn("Try to overwrite feature reader for the id {}, denied.", id);
            }
        }

        /**
         * Serializes features based on the given JSON object.
         * @param base The base JSON object.
         * @return The JSON object appended with the features.
         */
        public static JsonObject serialize(JsonObject base, List<Feature<?>> features) {
            JsonObject result = base.deepCopy();
            features.forEach(feature -> feature.serialize(result));
            return result;
        }

        /**
         * Deserializes features out of the given JSON object.
         * @param obj The base JSON object.
         * @return The unmodifiable list of features included in the object.
         */
        public static List<Feature<?>> deserialize(JsonObject obj) {
            List<Feature<?>> result = new ArrayList<>();
            obj.asMap().keySet().forEach(key -> {
                // We don't care about ingredients, it should not be a feature
                if (key.equals(ExtraJsonConstants.INGREDIENTS)) return;
                if (READER_MAP.containsKey(key)) {
                    result.add(READER_MAP.get(key).apply(obj));
                } else {
                    MekanismExtras.LOGGER.warn("Failed to parse feature of id: {}", key);
                }
            });
            return result;
        }
    }
}
