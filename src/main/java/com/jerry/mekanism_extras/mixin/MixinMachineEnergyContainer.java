package com.jerry.mekanism_extras.mixin;

import com.jerry.mekanism_extras.api.ExtraUpgrade;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(value = MachineEnergyContainer.class, remap = false)
public abstract class MixinMachineEnergyContainer<TILE extends TileEntityMekanism> extends BasicEnergyContainer {

    @Shadow
    @Final
    protected TILE tile;

    @Shadow
    protected FloatingLong currentEnergyPerTick;

    @Shadow
    public abstract void setMaxEnergy(FloatingLong maxEnergy);

    @Shadow public abstract FloatingLong getBaseMaxEnergy();

    protected MixinMachineEnergyContainer(FloatingLong maxEnergy, Predicate<@NotNull AutomationType> canExtract, Predicate<@NotNull AutomationType> canInsert, @Nullable IContentsListener listener) {
        super(maxEnergy, canExtract, canInsert, listener);
    }

    @Inject(method = "getEnergyPerTick", at = @At(value = "RETURN"), cancellable = true)
    public void mixinGetEnergyPerTick(CallbackInfoReturnable<FloatingLong> cir) {
        if (tile.supportsUpgrade(ExtraUpgrade.CREATIVE)) {
            cir.setReturnValue(tile.getComponent().isUpgradeInstalled(ExtraUpgrade.CREATIVE) ? FloatingLong.ZERO : currentEnergyPerTick);
        }
    }

    /**
     * @author LostMyself
     * @reason 兼容创造升级带来的能量变化，不过这会使得兼容性不太高。
     */
    @Overwrite
    public void updateMaxEnergy() {
        if (tile.supportsUpgrade(Upgrade.ENERGY) || tile.supportsUpgrade(ExtraUpgrade.CREATIVE)) {
            if (tile.getComponent().isUpgradeInstalled(ExtraUpgrade.CREATIVE)) {
                setMaxEnergy(FloatingLong.MAX_VALUE);
            } else {
                setMaxEnergy(MekanismUtils.getMaxEnergy(tile, getBaseMaxEnergy()));
            }
        }
    }
}
