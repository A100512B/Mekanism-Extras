package com.jerry.mekanism_extras.common.tier;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;

public enum BasicItemIOHatchTier implements ITier {

    BASIC(BaseTier.BASIC, 64),
    ADVANCED(BaseTier.ADVANCED, 64),
    ELITE(BaseTier.ELITE, 64),
    ULTIMATE(BaseTier.ULTIMATE, 64);

    private final BaseTier baseTier;
    private final int maxStackSize;

    BasicItemIOHatchTier(BaseTier baseTier, int maxStackSize) {
        this.baseTier = baseTier;
        this.maxStackSize = maxStackSize;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }
}
