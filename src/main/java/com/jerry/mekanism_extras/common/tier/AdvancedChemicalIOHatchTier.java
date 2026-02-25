package com.jerry.mekanism_extras.common.tier;

import com.jerry.mekanism_extras.api.tier.AdvancedTier;
import com.jerry.mekanism_extras.api.tier.IAdvancedTier;

public enum AdvancedChemicalIOHatchTier implements IAdvancedTier {

    ABSOLUTE(AdvancedTier.ABSOLUTE, 2_048_000),
    SUPREME(AdvancedTier.SUPREME, 16_384_000),
    COSMIC(AdvancedTier.COSMIC, 131_072_000),
    INFINITE(AdvancedTier.INFINITE, 524_288_000);

    private final AdvancedTier advancedTier;
    private final int tankCapacity;

    AdvancedChemicalIOHatchTier(AdvancedTier advancedTier, int tankCapacity) {
        this.advancedTier = advancedTier;
        this.tankCapacity = tankCapacity;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    @Override
    public AdvancedTier getAdvancedTier() {
        return advancedTier;
    }
}
