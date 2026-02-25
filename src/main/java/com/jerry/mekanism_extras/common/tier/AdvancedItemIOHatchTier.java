package com.jerry.mekanism_extras.common.tier;

import com.jerry.mekanism_extras.api.tier.AdvancedTier;
import com.jerry.mekanism_extras.api.tier.IAdvancedTier;

public enum AdvancedItemIOHatchTier implements IAdvancedTier {

    ABSOLUTE(AdvancedTier.ABSOLUTE, 128),
    SUPREME(AdvancedTier.SUPREME, 256),
    COSMIC(AdvancedTier.COSMIC, 512),
    INFINITE(AdvancedTier.INFINITE, 1024);

    private final AdvancedTier advancedTier;
    private final int maxStackSize;

    AdvancedItemIOHatchTier(AdvancedTier advancedTier, int maxStackSize) {
        this.advancedTier = advancedTier;
        this.maxStackSize = maxStackSize;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public AdvancedTier getAdvancedTier() {
        return advancedTier;
    }
}
