package com.jerry.mekanism_extras.common;

import com.jerry.mekanism_extras.MekanismExtras;
import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;

public enum ExtraLang implements ILangEntry {

    EXTRA_TAB("constants", "mod_name"),
    STOP_FLASHING("pack", "pack_name"),
    STOP_FLASHING_DESC("pack", "pack_description"),

    // Chemical Reactor
    CHEMICAL_REACTOR("chemical_reactor", "chemical_reactor"),
    CHEMICAL_REACTOR_IO_HATCH_MODE("chemical_reactor", "io_hatch_mode"),
    CHEMICAL_REACTOR_REFUSE_TO_SET_MODE("chemical_reactor", "refuse_to_set_mode"),
    CHEMICAL_REACTOR_IDLE("chemical_reactor", "idle"),
    CHEMICAL_REACTOR_PREPARING("chemical_reactor", "preparing"),
    CHEMICAL_REACTOR_READY("chemical_reactor", "ready"),
    CHEMICAL_REACTOR_RUNNING("chemical_reactor", "running"),
    CHEMICAL_REACTOR_ERROR_INSUFFICIENT_INPUT("chemical_reactor", "error.insufficient_input"),
    CHEMICAL_REACTOR_ERROR_INSUFFICIENT_INPUT_FOR_MAX_PARALLEL("chemical_reactor", "error.insufficient_input_for_max_parallel"),
    CHEMICAL_REACTOR_ERROR_INSUFFICIENT_ENERGY("chemical_reactor", "error.insufficient_energy"),
    CHEMICAL_REACTOR_ERROR_INSUFFICIENT_ENERGY_FOR_MAX_PARALLEL("chemical_reactor", "error.insufficient_energy_for_max_parallel"),
    CHEMICAL_REACTOR_ERROR_CONDITION_NOT_MATCHED("chemical_reactor", "error.condition_not_matched"),
    CHEMICAL_REACTOR_ERROR_OUTPUT_FULL("chemical_reactor", "error.output_full"),
    CHEMICAL_REACTOR_ERROR_WELL("chemical_reactor", "error.well"),
    CHEMICAL_REACTOR_MALFORMED_ROTOR("chemical_reactor", "bad_rotor"),

    // JEI
    JEI_INFO_RICH_NAQUADAH_FUEL("info", "jei.rich_naquadah_fuel"),
    JEI_INFO_RICH_URANIUM_FUEL("info", "jei.rich_uranium_fuel"),

    // Upgrades
    UPGRADES_STACK("gui", "upgrades.stack"),
    ENERGY_CONSUMPTION("gui", "energy_consumption"),

    // Reinforced Induction Matrix
    REINFORCED_MATRIX("matrix", "reinforced_induction_matrix"),

    // Description
    DESCRIPTION_TUNGSTEN_CASING("description", "tungsten_casing"),
    DESCRIPTION_CHEMICAL_REACTOR_CASING("description", "chemical_reactor_casing"),
    DESCRIPTION_CHEMICAL_REACTOR_CONTROLLER("description", "chemical_reactor_controller"),
    DESCRIPTION_CHEMICAL_REACTOR_ROTOR("description", "chemical_reactor_rotor"),
    DESCRIPTION_CHEMICAL_INERT_CLASS("description", "chemical_inert_time"),
    DESCRIPTION_CHEMICAL_REACTOR_ITEM_IO_HATCH("description", "chemical_reactor_item_io_hatch"),
    DESCRIPTION_CHEMICAL_REACTOR_FLUID_IO_HATCH("description", "chemical_reactor_fluid_io_hatch"),
    DESCRIPTION_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH("description", "chemical_reactor_chemical_io_hatch"),

    ;
    private final String key;

    ExtraLang(String type, String path) {
        this(Util.makeDescriptionId(type, MekanismExtras.rl(path)));
    }

    ExtraLang(String key) {
        this.key = key;
    }

    @Override
    public @NotNull String getTranslationKey() {
        return key;
    }
}
