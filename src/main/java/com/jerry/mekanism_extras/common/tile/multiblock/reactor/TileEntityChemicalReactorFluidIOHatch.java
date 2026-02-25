package com.jerry.mekanism_extras.common.tile.multiblock.reactor;

import com.jerry.mekanism_extras.common.block.attribute.ExtraAttribute;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData;
import com.jerry.mekanism_extras.common.tier.AdvancedFluidIOHatchTier;
import com.jerry.mekanism_extras.common.tier.BasicFluidIOHatchTier;
import com.jerry.mekanism_extras.common.util.ExtraGUIUtils;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

public class TileEntityChemicalReactorFluidIOHatch
        extends TileEntityChemicalReactorIOHatchBase {

    protected final List<IExtendedFluidTank> cachedFluidTanks = new ArrayList<>();
    protected int tankCapacity;
    private final IntSupplier tankCapacitySupplier = () -> {
        if (tankCapacity == 0) precomputeSpec();
        return tankCapacity;
    };

    public TileEntityChemicalReactorFluidIOHatch(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected void precomputeSpec() {
        AdvancedFluidIOHatchTier tier = ExtraAttribute.getTier(getBlockType(), AdvancedFluidIOHatchTier.class);
        if (tier == null) {
            BasicFluidIOHatchTier tier1 = Attribute.getTier(getBlockType(), BasicFluidIOHatchTier.class);
            spec = ExtraGUIUtils.getIOHatchSpec(tier1);
            tankCapacity = tier1.getTankCapacity();
            ofBasicTier = true;
        } else {
            spec = ExtraGUIUtils.getIOHatchSpec(tier);
            tankCapacity = tier.getTankCapacity();
            ofBasicTier = false;
        }
    }

    @Override
    @Nullable
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> {
            if (cachedFluidTanks.isEmpty()) {
                for (int i = 0; i < spec.size(); i++) {
                    cachedFluidTanks.add(createFluidTank(listener));
                }
            }
            return cachedFluidTanks;
        };
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        Stream<IExtendedFluidTank> tanks = cachedFluidTanks.stream().filter(tank -> !tank.isEmpty());
        if (active) {
            tanks.forEach(tank -> getMultiblock().fluidIndex.removeFromIndex(tank));
        } else {
            tanks.forEach(tank -> getMultiblock().fluidIndex.addToIndex(tank));
        }
    }

    @Override
    public void outputContent(ChemicalReactorMultiblockData data) {
        cachedFluidTanks.stream().filter(tank -> !tank.isEmpty())
                .findFirst()
                .ifPresent(tank -> FluidUtils.emit(outputDirections, tank, this));
    }

    private IExtendedFluidTank createFluidTank(IContentsListener listener) {
        return new BasicFluidTank(tankCapacitySupplier.getAsInt(), (stack, type) -> true,
                (stack, type) -> !getActive() || type == AutomationType.INTERNAL,
                stack -> true, listener) {

            @Override
            public void setStack(@NotNull FluidStack stack) {
                FluidStack old = stored;
                super.setStack(stack);
                if (!getActive()) getMultiblock().fluidIndex.updateIndex(this, old, stored);
            }

            @Override
            public int setStackSize(int amount, @NotNull Action action) {
                FluidStack old = stored;
                int result = super.setStackSize(amount, action);
                if (!getActive()) getMultiblock().fluidIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public FluidStack insert(@NotNull FluidStack stack, @NotNull Action action, @NotNull AutomationType automationType) {
                FluidStack old = stored;
                FluidStack result = super.insert(stack, action, automationType);
                if (!getActive()) getMultiblock().fluidIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public FluidStack extract(int amount, @NotNull Action action, @NotNull AutomationType automationType) {
                FluidStack old = stored;
                FluidStack result = super.extract(amount, action, automationType);
                if (!getActive()) getMultiblock().fluidIndex.updateIndex(this, old, stored);
                return result;
            }
        };
    }
}
