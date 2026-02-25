package com.jerry.mekanism_extras.common.tile.multiblock.reactor;

import com.jerry.mekanism_extras.common.block.attribute.ExtraAttribute;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData;
import com.jerry.mekanism_extras.common.tier.AdvancedChemicalIOHatchTier;
import com.jerry.mekanism_extras.common.tier.BasicChemicalIOHatchTier;
import com.jerry.mekanism_extras.common.util.ExtraGUIUtils;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.ChemicalTankBuilder.BasicGasTank;
import mekanism.api.chemical.ChemicalTankBuilder.BasicInfusionTank;
import mekanism.api.chemical.ChemicalTankBuilder.BasicPigmentTank;
import mekanism.api.chemical.ChemicalTankBuilder.BasicSlurryTank;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank.Current;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.util.ChemicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

public class TileEntityChemicalReactorChemicalIOHatch
        extends TileEntityChemicalReactorIOHatchBase {

    protected final List<MergedChemicalTank> cachedChemicalTanks = new ArrayList<>();
    protected int tankCapacity;
    private final IntSupplier tankCapacitySupplier = () -> {
        if (tankCapacity == 0) precomputeSpec();
        return tankCapacity;
    };

    public TileEntityChemicalReactorChemicalIOHatch(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected void precomputeSpec() {
        AdvancedChemicalIOHatchTier tier = ExtraAttribute.getTier(getBlockType(), AdvancedChemicalIOHatchTier.class);
        if (tier == null) {
            BasicChemicalIOHatchTier tier1 = Attribute.getTier(getBlockType(), BasicChemicalIOHatchTier.class);
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
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        initChemicalTanksIfNecessary(listener);
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSide(this::getDirection);
        cachedChemicalTanks.forEach(tank -> builder.addTank(tank.getGasTank()));
        return builder.build();
    }

    @Override
    @Nullable
    public IChemicalTankHolder<InfuseType, InfusionStack, IInfusionTank> getInitialInfusionTanks(IContentsListener listener) {
        initChemicalTanksIfNecessary(listener);
        ChemicalTankHelper<InfuseType, InfusionStack, IInfusionTank> builder = ChemicalTankHelper.forSide(this::getDirection);
        cachedChemicalTanks.forEach(tank -> builder.addTank(tank.getInfusionTank()));
        return builder.build();
    }

    @Override
    @Nullable
    public IChemicalTankHolder<Pigment, PigmentStack, IPigmentTank> getInitialPigmentTanks(IContentsListener listener) {
        initChemicalTanksIfNecessary(listener);
        ChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> builder = ChemicalTankHelper.forSide(this::getDirection);
        cachedChemicalTanks.forEach(tank -> builder.addTank(tank.getPigmentTank()));
        return builder.build();
    }

    @Override
    @Nullable
    public IChemicalTankHolder<Slurry, SlurryStack, ISlurryTank> getInitialSlurryTanks(IContentsListener listener) {
        initChemicalTanksIfNecessary(listener);
        ChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> builder = ChemicalTankHelper.forSide(this::getDirection);
        cachedChemicalTanks.forEach(tank -> builder.addTank(tank.getSlurryTank()));
        return builder.build();
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        Stream<IGasTank> gasTanks = cachedChemicalTanks.stream().filter(this::isMergedChemicalTankEmpty)
                .filter(tank -> tank.getCurrent() == Current.GAS)
                .map(MergedChemicalTank::getGasTank);
        Stream<IInfusionTank> infusionTanks = cachedChemicalTanks.stream().filter(this::isMergedChemicalTankEmpty)
                .filter(tank -> tank.getCurrent() == Current.INFUSION)
                .map(MergedChemicalTank::getInfusionTank);
        Stream<IPigmentTank> pigmentTanks = cachedChemicalTanks.stream().filter(this::isMergedChemicalTankEmpty)
                .filter(tank -> tank.getCurrent() == Current.PIGMENT)
                .map(MergedChemicalTank::getPigmentTank);
        Stream<ISlurryTank> slurryTanks = cachedChemicalTanks.stream().filter(this::isMergedChemicalTankEmpty)
                .filter(tank -> tank.getCurrent() == Current.SLURRY)
                .map(MergedChemicalTank::getSlurryTank);
        ChemicalReactorMultiblockData multiblock = getMultiblock();
        if (active) {
            gasTanks.forEach(multiblock.chemicalIndex::removeFromIndex);
            infusionTanks.forEach(multiblock.chemicalIndex::removeFromIndex);
            pigmentTanks.forEach(multiblock.chemicalIndex::removeFromIndex);
            slurryTanks.forEach(multiblock.chemicalIndex::removeFromIndex);
        } else {
            gasTanks.forEach(multiblock.chemicalIndex::addToIndex);
            infusionTanks.forEach(multiblock.chemicalIndex::addToIndex);
            pigmentTanks.forEach(multiblock.chemicalIndex::addToIndex);
            slurryTanks.forEach(multiblock.chemicalIndex::addToIndex);
        }
    }

    public List<MergedChemicalTank> getCachedChemicalTanks() {
        return cachedChemicalTanks;
    }

    @Override
    public void outputContent(ChemicalReactorMultiblockData data) {
        cachedChemicalTanks.stream().filter(tank -> !isMergedChemicalTankEmpty(tank))
                .findFirst()
                .ifPresent(this::emitMergedChemicalTank);
    }

    private void initChemicalTanksIfNecessary(IContentsListener listener) {
        if (!cachedChemicalTanks.isEmpty()) return;
        for (int i = 0; i < spec.size(); i++) {
            cachedChemicalTanks.add(MergedChemicalTank.create(
                    createGasTank(listener),
                    createInfusionTank(listener),
                    createPigmentTank(listener),
                    createSlurryTank(listener)
            ));
        }
    }

    private boolean isMergedChemicalTankEmpty(MergedChemicalTank tank) {
        return tank.getGasTank().isEmpty() && tank.getInfusionTank().isEmpty() &&
                tank.getPigmentTank().isEmpty() && tank.getSlurryTank().isEmpty();
    }

    private void emitMergedChemicalTank(MergedChemicalTank tank) {
        tank.getAllTanks().forEach(tank1 -> ChemicalUtil.emit(outputDirections, tank1, this));
    }

    private IGasTank createGasTank(IContentsListener listener) {
        return new BasicGasTank(tankCapacitySupplier.getAsInt(), (stack, type) -> true,
                (stack, type) -> !getActive() || type == AutomationType.INTERNAL,
                gas -> true, attribute -> true, listener) {

            @Override
            public void setStack(@NotNull GasStack stack) {
                GasStack old = stored;
                super.setStack(stack);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
            }

            @Override
            public long setStackSize(long amount, @NotNull Action action) {
                GasStack old = stored;
                long result = super.setStackSize(amount, action);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public GasStack insert(@NotNull GasStack stack, @NotNull Action action, @NotNull AutomationType automationType) {
                GasStack old = stored;
                GasStack result = super.insert(stack, action, automationType);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public GasStack extract(long amount, @NotNull Action action, @NotNull AutomationType automationType) {
                GasStack old = stored;
                GasStack result = super.extract(amount, action, automationType);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }
        };
    }

    private IInfusionTank createInfusionTank(IContentsListener listener) {
        return new BasicInfusionTank(tankCapacitySupplier.getAsInt(), (stack, type) -> true,
                (stack, type) -> !getActive() || type == AutomationType.INTERNAL,
                infuseType -> true, attribute -> true, listener) {

            @Override
            public void setStack(@NotNull InfusionStack stack) {
                InfusionStack old = stored;
                super.setStack(stack);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stack);
            }

            @Override
            public long setStackSize(long amount, @NotNull Action action) {
                InfusionStack old = stored;
                long result = super.setStackSize(amount, action);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public InfusionStack insert(@NotNull InfusionStack stack, @NotNull Action action, @NotNull AutomationType automationType) {
                InfusionStack old = stored;
                InfusionStack result = super.insert(stack, action, automationType);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public InfusionStack extract(long amount, @NotNull Action action, @NotNull AutomationType automationType) {
                InfusionStack old = stored;
                InfusionStack result = super.extract(amount, action, automationType);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }
        };
    }

    private IPigmentTank createPigmentTank(IContentsListener listener) {
        return new BasicPigmentTank(tankCapacitySupplier.getAsInt(), (stack, type) -> true,
                (stack, type) -> !getActive() || type == AutomationType.INTERNAL,
                pigment -> true, attribute -> true, listener) {

            @Override
            public void setStack(@NotNull PigmentStack stack) {
                PigmentStack old = stored;
                super.setStack(stack);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
            }

            @Override
            public long setStackSize(long amount, @NotNull Action action) {
                PigmentStack old = stored;
                long result = super.setStackSize(amount, action);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public PigmentStack insert(@NotNull PigmentStack stack, @NotNull Action action, @NotNull AutomationType automationType) {
                PigmentStack old = stored;
                PigmentStack result = super.insert(stack, action, automationType);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public PigmentStack extract(long amount, @NotNull Action action, @NotNull AutomationType automationType) {
                PigmentStack old = stored;
                PigmentStack result = super.extract(amount, action, automationType);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }
        };
    }

    private ISlurryTank createSlurryTank(IContentsListener listener) {
        return new BasicSlurryTank(tankCapacitySupplier.getAsInt(), (stack, type) -> true,
                (stack, type) -> !getActive() || type == AutomationType.INTERNAL,
                slurry -> true, attribute -> true, listener) {

            @Override
            public void setStack(@NotNull SlurryStack stack) {
                SlurryStack old = stored;
                super.setStack(stack);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
            }

            @Override
            public long setStackSize(long amount, @NotNull Action action) {
                SlurryStack old = stored;
                long result = super.setStackSize(amount, action);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public SlurryStack insert(@NotNull SlurryStack stack, @NotNull Action action, @NotNull AutomationType automationType) {
                SlurryStack old = stored;
                SlurryStack result = super.insert(stack, action, automationType);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }

            @Override
            @NotNull
            public SlurryStack extract(long amount, @NotNull Action action, @NotNull AutomationType automationType) {
                SlurryStack old = stored;
                SlurryStack result = super.extract(amount, action, automationType);
                if (!getActive()) getMultiblock().chemicalIndex.updateIndex(this, old, stored);
                return result;
            }
        };
    }
}
