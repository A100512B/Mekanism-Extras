package com.jerry.mekanism_extras.common;

import com.jerry.mekanism_extras.MekanismExtras;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.common.tags.LazyTagLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class ExtraTag {

    public static void init() {
        Items.init();
        Fluids.init();
        Gases.init();
        InfuseTypes.init();
        Slurries.init();
    }

    public static class Items {

        private static void init() {
        }

        private Items() {
        }

        public static final TagKey<Item> NAQUADAH = forgeTag("ores/naquadah");
        public static final TagKey<Item> END_NAQUADAH = forgeTag("ores/naquadah");

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Fluids {
        private static void init() {
        }

        private Fluids() {
        }
        public static final TagKey<Fluid> SILICON_TETRAFLUORIDE = forgeTag("silicon_tetrafluoride");
        public static final TagKey<Fluid> FLUORINATED_SILICON_URANIUM_FUEL = forgeTag("fluorinated_silicon_uranium_fuel");
        public static final TagKey<Fluid> RICH_SILICON_LIQUID_FUEL = forgeTag("rich_silicon_liquid_fuel");
        public static final TagKey<Fluid> RICH_URANIUM_LIQUID_FUEL = forgeTag("rich_uranium_liquid_fuel");

        public static final TagKey<Fluid> POLONIUM_CONTAINING_SOLUTION = forgeTag("polonium_containing_solution");
        public static final LazyTagLookup<Fluid> LAZY_POLONIUM_CONTAINING_SOLUTION = LazyTagLookup.create(ForgeRegistries.FLUIDS, POLONIUM_CONTAINING_SOLUTION);

        private static TagKey<Fluid> forgeTag(String name) {
            return FluidTags.create(new ResourceLocation("forge", name));
        }

        private static TagKey<Fluid> tag(String name) {
            return FluidTags.create(MekanismExtras.rl(name));
        }
    }

    public static class Gases {

        private static void init() {
        }

        private Gases() {
        }

        public static final TagKey<Gas> MOLTEN_THERMONUCLEAR = tag("molten_thermonuclear");
        public static final TagKey<Gas> SILICON_TETRAFLUORIDE = tag("silicon_tetrafluoride");
        public static final TagKey<Gas> FLUORINATED_SILICON_URANIUM_FUEL = tag("fluorinated_silicon_uranium_fuel");
        public static final TagKey<Gas> RICH_SILICON_FUEL = tag("rich_silicon_fuel");
        public static final LazyTagLookup<Gas> RICH_SILICON_FUEL_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, RICH_SILICON_FUEL);
        public static final TagKey<Gas> RICH_URANIUM_FUEL = tag("rich_uranium_fuel");
        public static final LazyTagLookup<Gas> RICH_URANIUM_FUEL_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, RICH_URANIUM_FUEL);
        public static final TagKey<Gas> SILICON_URANIUM_FUEL = tag("silicon_uranium_fuel");
        public static final LazyTagLookup<Gas> SILICON_URANIUM_FUEL_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, SILICON_URANIUM_FUEL);

        private static TagKey<Gas> tag(String name) {
            return ChemicalTags.GAS.tag(MekanismExtras.rl(name));
        }
    }

    public static class InfuseTypes {

        private static void init() {
        }

        private InfuseTypes() {
        }

        public static final TagKey<InfuseType> RADIANCE = tag("radiance");
        public static final TagKey<InfuseType> THERMONUCLEAR = tag("thermonuclear");
        public static final TagKey<InfuseType> SHINING = tag("shining");
        public static final TagKey<InfuseType> SPECTRUM = tag("spectrum");

        private static TagKey<InfuseType> tag(String name) {
            return ChemicalTags.INFUSE_TYPE.tag(MekanismExtras.rl(name));
        }
    }

    public static class Slurries {

        private static void init() {
        }

        private Slurries() {
        }

        public static final TagKey<Slurry> DIRTY = tag("dirty");
        public static final LazyTagLookup<Slurry> DIRTY_LOOKUP = LazyTagLookup.create(ChemicalTags.SLURRY, DIRTY);
        public static final TagKey<Slurry> CLEAN = tag("clean");

        private static TagKey<Slurry> tag(String name) {
            return ChemicalTags.SLURRY.tag(MekanismExtras.rl(name));
        }
    }
}
