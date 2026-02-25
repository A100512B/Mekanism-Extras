package com.jerry.mekanism_extras.common.tier;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;

public enum BasicChemicalIOHatchTier implements ITier {

    BASIC(BaseTier.BASIC, 2_000),
    ADVANCED(BaseTier.ADVANCED, 16_000),
    ELITE(BaseTier.ELITE, 64_000),
    ULTIMATE(BaseTier.ULTIMATE, 256_000);

    private final BaseTier baseTier;
    private final int tankCapacity;

    BasicChemicalIOHatchTier(BaseTier baseTier, int tankCapacity) {
        this.baseTier = baseTier;
        this.tankCapacity = tankCapacity;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }
}
