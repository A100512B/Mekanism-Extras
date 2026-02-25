package com.jerry.mekanism_extras.common.util;

import com.jerry.mekanism_extras.common.tier.*;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class ExtraGUIUtils {

    private ExtraGUIUtils() {}

    public static final int ITEM_SLOT_WIDTH = 18;  // 16 + 1*2 for the border
    public static final int ITEM_SLOT_HEIGHT = 18;
    public static final int STANDARD_TANK_WIDTH = 16; // from GaugeOverlay.STANDARD
    public static final int STANDARD_TANK_HEIGHT = 58;
    private static final int FLUID_IO_HATCH_MARGIN_X = 5;

    private static final List<IntIntImmutablePair> BASIC_ITEM_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> ADVANCED_ITEM_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> ELITE_ITEM_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> ULTIMATE_ITEM_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> ABSOLUTE_ITEM_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> SUPREME_ITEM_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> COSMIC_ITEM_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> INFINITE_ITEM_IO_HATCH_LAYOUT = new ArrayList<>();
    // Fluid IO Hatch layouts are the same as chemical ones
    private static final List<IntIntImmutablePair> BASIC_FLUID_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> ADVANCED_FLUID_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> ELITE_FLUID_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> ULTIMATE_FLUID_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> ABSOLUTE_FLUID_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> SUPREME_FLUID_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> COSMIC_FLUID_IO_HATCH_LAYOUT = new ArrayList<>();
    private static final List<IntIntImmutablePair> INFINITE_FLUID_IO_HATCH_LAYOUT = new ArrayList<>();

    static {
        // The magic numbers all come from textures/gui/container/shulker_box.png
        // The item slots covers from (7, 17) to (169, 71) (the left-top pos of the right-bottom slot)
        for (int i = 0; i < 5; i++) BASIC_ITEM_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(43 + i * ITEM_SLOT_WIDTH, 35));
        for (int i = 0; i < 9; i++) ADVANCED_ITEM_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(7 + i * ITEM_SLOT_WIDTH, 35));
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 9; j++)
                ELITE_ITEM_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(25 + j * ITEM_SLOT_WIDTH, 35 + i * ITEM_SLOT_HEIGHT));
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                ULTIMATE_ITEM_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(7 + j * ITEM_SLOT_WIDTH, 17 + i * ITEM_SLOT_HEIGHT));
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 9; j++)
                ABSOLUTE_ITEM_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(7 + j * ITEM_SLOT_WIDTH, 17 + i * ITEM_SLOT_HEIGHT));
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 11; j++)
                SUPREME_ITEM_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(7 + j * ITEM_SLOT_WIDTH, 17 + i * ITEM_SLOT_HEIGHT));
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 13; j++)
                COSMIC_ITEM_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(7 + j * ITEM_SLOT_WIDTH, 17 + i * ITEM_SLOT_HEIGHT));
        for (int i = 0; i < 11; i++)
            for (int j = 0; j < 17; j++)
                INFINITE_ITEM_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(7 + j * ITEM_SLOT_WIDTH, 17 + i * ITEM_SLOT_HEIGHT));

        // The distance between each tank is 4 bits
        // 60 = (176-20-20-16)/2, where 176 is the width of a regular inventory
        for (int i = 0; i < 3; i++) BASIC_FLUID_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(60 + i * (STANDARD_TANK_WIDTH + 4), 17));
        for (int i = 0; i < 5; i++) ADVANCED_FLUID_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(40 + i * (STANDARD_TANK_WIDTH + 4), 17));
        for (int i = 0; i < 7; i++) ELITE_FLUID_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(20 + i * (STANDARD_TANK_WIDTH + 4), 17));
        // 20-20 = 0, so we have to add a margin of 5 bits
        for (int i = 0; i < 9; i++) ULTIMATE_FLUID_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(FLUID_IO_HATCH_MARGIN_X + i * (STANDARD_TANK_WIDTH + 4), 17));
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 7; j++)
                ABSOLUTE_FLUID_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(20 + j * (STANDARD_TANK_WIDTH + 4), 17 + i * (STANDARD_TANK_HEIGHT + 4)));
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 9; j++)
                SUPREME_FLUID_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(FLUID_IO_HATCH_MARGIN_X + j * (STANDARD_TANK_WIDTH + 4), 17 + i * (STANDARD_TANK_HEIGHT + 4)));
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 13; j++)
                COSMIC_FLUID_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(FLUID_IO_HATCH_MARGIN_X + j * (STANDARD_TANK_WIDTH + 4), 17 + i * (STANDARD_TANK_HEIGHT + 4)));
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 17; j++)
                INFINITE_FLUID_IO_HATCH_LAYOUT.add(new IntIntImmutablePair(FLUID_IO_HATCH_MARGIN_X + j * (STANDARD_TANK_WIDTH + 4), 17 + i * (STANDARD_TANK_HEIGHT + 4)));
    }

    public static List<IntIntImmutablePair> getIOHatchSpec(BasicItemIOHatchTier tier) {
        return switch (tier) {
            case BASIC -> BASIC_ITEM_IO_HATCH_LAYOUT;
            case ADVANCED -> ADVANCED_ITEM_IO_HATCH_LAYOUT;
            case ELITE -> ELITE_ITEM_IO_HATCH_LAYOUT;
            case ULTIMATE -> ULTIMATE_ITEM_IO_HATCH_LAYOUT;
        };
    }

    public static List<IntIntImmutablePair> getIOHatchSpec(AdvancedItemIOHatchTier tier) {
        return switch (tier) {
            case ABSOLUTE -> ABSOLUTE_ITEM_IO_HATCH_LAYOUT;
            case SUPREME -> SUPREME_ITEM_IO_HATCH_LAYOUT;
            case COSMIC -> COSMIC_ITEM_IO_HATCH_LAYOUT;
            case INFINITE -> INFINITE_ITEM_IO_HATCH_LAYOUT;
        };
    }
    
    public static List<IntIntImmutablePair> getIOHatchSpec(BasicFluidIOHatchTier tier) {
        return switch (tier) {
            case BASIC -> BASIC_FLUID_IO_HATCH_LAYOUT;
            case ADVANCED -> ADVANCED_FLUID_IO_HATCH_LAYOUT;
            case ELITE -> ELITE_FLUID_IO_HATCH_LAYOUT;
            case ULTIMATE -> ULTIMATE_FLUID_IO_HATCH_LAYOUT;
        };
    }

    public static List<IntIntImmutablePair> getIOHatchSpec(AdvancedFluidIOHatchTier tier) {
        return switch (tier) {
            case ABSOLUTE -> ABSOLUTE_FLUID_IO_HATCH_LAYOUT;
            case SUPREME -> SUPREME_FLUID_IO_HATCH_LAYOUT;
            case COSMIC -> COSMIC_FLUID_IO_HATCH_LAYOUT;
            case INFINITE -> INFINITE_FLUID_IO_HATCH_LAYOUT;
        };
    }

    public static List<IntIntImmutablePair> getIOHatchSpec(BasicChemicalIOHatchTier tier) {
        return switch (tier) {
            case BASIC -> BASIC_FLUID_IO_HATCH_LAYOUT;
            case ADVANCED -> ADVANCED_FLUID_IO_HATCH_LAYOUT;
            case ELITE -> ELITE_FLUID_IO_HATCH_LAYOUT;
            case ULTIMATE -> ULTIMATE_FLUID_IO_HATCH_LAYOUT;
        };
    }

    public static List<IntIntImmutablePair> getIOHatchSpec(AdvancedChemicalIOHatchTier tier) {
        return switch (tier) {
            case ABSOLUTE -> ABSOLUTE_FLUID_IO_HATCH_LAYOUT;
            case SUPREME -> SUPREME_FLUID_IO_HATCH_LAYOUT;
            case COSMIC -> COSMIC_FLUID_IO_HATCH_LAYOUT;
            case INFINITE -> INFINITE_FLUID_IO_HATCH_LAYOUT;
        };
    }
}
