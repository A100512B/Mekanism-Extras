package com.jerry.mekanism_extras.common.tier;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;

public enum BasicFluidIOHatchTier implements ITier {

    BASIC(BaseTier.BASIC, 8_000),
    ADVANCED(BaseTier.ADVANCED, 32_000),
    ELITE(BaseTier.ELITE, 128_000),
    ULTIMATE(BaseTier.ULTIMATE, 512_000);

    private final BaseTier baseTier;
    private final int tankCapacity;

    BasicFluidIOHatchTier(BaseTier baseTier, int tankCapacity) {
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
