package com.jerry.mekanism_extras.common.util;

import com.jerry.mekanism_extras.MekanismExtras;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ExtraTransferUtils {

    private ExtraTransferUtils() {
    }

    public static boolean simulateItems(List<IInventorySlot> slots, Collection<ItemStack> stacks) {
        return simulate(slots, stacks,
                slot -> slot.getStack().copy(),
                (slot, current, stack) -> {
                    if (current.isEmpty()) return stack.getCount() <= slot.getLimit(stack);
                    return ItemHandlerHelper.canItemStacksStack(current, stack) &&
                            current.getCount() + stack.getCount() <= slot.getLimit(stack);
                },
                (current, stack) -> {
                    if (current.isEmpty()) return stack.copy();
                    ItemStack merged = current.copy();
                    merged.grow(stack.getCount());
                    return merged;
                });
    }

    public static boolean simulateFluids(List<IExtendedFluidTank> tanks, Collection<FluidStack> stacks) {
        return simulate(tanks, stacks,
                tank -> tank.getFluid().copy(),
                (tank, current, stack) -> {
                    if (current.isEmpty()) return stack.getAmount() <= tank.getCapacity();
                    return current.isFluidEqual(stack) &&
                            current.getAmount() + stack.getAmount() <= tank.getCapacity();
                },
                (current, stack) -> {
                    if (current.isEmpty()) return stack.copy();
                    FluidStack merged = current.copy();
                    merged.grow(stack.getAmount());
                    return merged;
                });
    }

    public static boolean simulateChemicals(
            List<MergedChemicalTank> allMergedTanks,
            Collection<GasStack> gases,
            Collection<InfusionStack> infusions,
            Collection<PigmentStack> pigments,
            Collection<SlurryStack> slurries
    ) {
        List<ChemicalOutput> outputs = new ArrayList<>();
        gases.stream().map(s -> new ChemicalOutput(ChemicalType.GAS, s)).forEach(outputs::add);
        infusions.stream().map(s -> new ChemicalOutput(ChemicalType.INFUSION, s)).forEach(outputs::add);
        pigments.stream().map(s -> new ChemicalOutput(ChemicalType.PIGMENT, s)).forEach(outputs::add);
        slurries.stream().map(s -> new ChemicalOutput(ChemicalType.SLURRY, s)).forEach(outputs::add);

        if (outputs.isEmpty()) return true;

        List<MergedTankSimState> simStates = allMergedTanks.stream()
                .map(MergedTankSimState::new)
                .toList();

        for (ChemicalOutput output : outputs) {
            boolean allocated = false;
            for (MergedTankSimState state : simStates) {
                if (state.tryInsert(output.type, output.stack)) {
                    allocated = true;
                    break;
                }
            }
            if (!allocated) return false;
        }
        return true;
    }

    private record ChemicalOutput(ChemicalType type, ChemicalStack<?> stack) {}

    private static class MergedTankSimState {
        private final MergedChemicalTank realTank;
        private ChemicalType currentType = null;
        private long currentAmount = 0;

        MergedTankSimState(MergedChemicalTank realTank) {
            this.realTank = realTank;
        }

        boolean tryInsert(ChemicalType type, ChemicalStack<?> stack) {
            IChemicalTank<?, ?> subTank = realTank.getTankForType(type);
            long capacity = subTank.getCapacity();
            long needed = stack.getAmount();

            if (currentType == null) {
                if (needed <= capacity) {
                    currentType = type;
                    currentAmount = needed;
                    return true;
                }
            } else if (currentType == type) {
                if (currentAmount + needed <= capacity) {
                    currentAmount += needed;
                    return true;
                }
            }
            return false;
        }
    }

    private static <TYPE, STACK> boolean simulate(List<TYPE> targets, Collection<STACK> stacks,
                                                  Function<TYPE, STACK> currentExtractor,
                                                  TriPredicate<TYPE, STACK, STACK> canInsert,
                                                  Merger<STACK> merger) {
        List<SimulatedTarget<TYPE, STACK>> simulated = new ArrayList<>();
        for (TYPE target : targets) {
            STACK current = currentExtractor.apply(target);
            simulated.add(new SimulatedTarget<>(target, current));
        }

        for (STACK stack : stacks) {
            for (SimulatedTarget<TYPE, STACK> sim : simulated) {
                if (sim.canInsert(stack, canInsert)) {
                    sim.insert(stack, merger);
                    if (isEmpty(stack)) break;
                }
            }
            if (!isEmpty(stack)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(Object stack) {
        if (stack instanceof ItemStack s) return s.isEmpty();
        if (stack instanceof FluidStack s) return s.isEmpty();
        if (stack instanceof GasStack s) return s.isEmpty();
        if (stack instanceof InfusionStack s) return s.isEmpty();
        if (stack instanceof PigmentStack s) return s.isEmpty();
        if (stack instanceof SlurryStack s) return s.isEmpty();
        return false;
    }

    @FunctionalInterface
    private interface TriPredicate<A, B, C> {
        boolean test(A a, B b, C c);
    }

    @FunctionalInterface
    private interface Merger<S> {
        S merge(S current, S toAdd);
    }

    private static class SimulatedTarget<T, S> {
        private final T target;
        private S current;

        SimulatedTarget(T target, S current) {
            this.target = target;
            this.current = current;
        }

        boolean canInsert(S stack, TriPredicate<T, S, S> canInsert) {
            return canInsert.test(target, current, stack);
        }

        void insert(S stack, Merger<S> merger) {
            this.current = merger.merge(this.current, stack);
            // Later the stack should be set empty, which will be done by the caller
        }
    }

    public static void executeItems(List<IInventorySlot> slots, Collection<ItemStack> stacks) {
        if (stacks.isEmpty()) return;
        for (ItemStack stack : stacks) {
            ItemStack toInsert = stack.copy();
            for (IInventorySlot slot : slots) {
                toInsert = slot.insertItem(toInsert, Action.EXECUTE, AutomationType.INTERNAL);
                if (toInsert.isEmpty()) break;
            }
            if (!toInsert.isEmpty()) {
                MekanismExtras.LOGGER.error("Failed to insert item {} during execute, simulation should have guaranteed success.", stack);
            }
        }
    }

    public static void executeFluids(List<IExtendedFluidTank> tanks, Collection<FluidStack> stacks) {
        if (stacks.isEmpty()) return;
        for (FluidStack stack : stacks) {
            FluidStack toInsert = stack.copy();
            for (IExtendedFluidTank tank : tanks) {
                toInsert = tank.insert(toInsert, Action.EXECUTE, AutomationType.INTERNAL);
                if (toInsert.isEmpty()) break;
            }
            if (!toInsert.isEmpty()) {
                MekanismExtras.LOGGER.error("Failed to insert fluid {} during execute, simulation should have guaranteed success.", stack);
            }
        }
    }

    public static void executeGases(List<IGasTank> tanks, Collection<GasStack> stacks) {
        if (stacks.isEmpty()) return;
        for (GasStack stack : stacks) {
            GasStack toInsert = stack.copy();
            for (IGasTank tank : tanks) {
                toInsert = tank.insert(toInsert, Action.EXECUTE, AutomationType.INTERNAL);
                if (toInsert.isEmpty()) break;
            }
            if (!toInsert.isEmpty()) {
                MekanismExtras.LOGGER.error("Failed to insert gas {} during execute, simulation should have guaranteed success.", stack);
            }
        }
    }

    public static void executeInfusions(List<IInfusionTank> tanks, Collection<InfusionStack> stacks) {
        if (stacks.isEmpty()) return;
        for (InfusionStack stack : stacks) {
            InfusionStack toInsert = stack.copy();
            for (IInfusionTank tank : tanks) {
                toInsert = tank.insert(toInsert, Action.EXECUTE, AutomationType.INTERNAL);
                if (toInsert.isEmpty()) break;
            }
            if (!toInsert.isEmpty()) {
                MekanismExtras.LOGGER.error("Failed to insert infusion {} during execute, simulation should have guaranteed success.", stack);
            }
        }
    }

    public static void executePigments(List<IPigmentTank> tanks, Collection<PigmentStack> stacks) {
        if (stacks.isEmpty()) return;
        for (PigmentStack stack : stacks) {
            PigmentStack toInsert = stack.copy();
            for (IPigmentTank tank : tanks) {
                toInsert = tank.insert(toInsert, Action.EXECUTE, AutomationType.INTERNAL);
                if (toInsert.isEmpty()) break;
            }
            if (!toInsert.isEmpty()) {
                MekanismExtras.LOGGER.error("Failed to insert pigment {} during execute, simulation should have guaranteed success.", stack);
            }
        }
    }

    public static void executeSlurries(List<ISlurryTank> tanks, Collection<SlurryStack> stacks) {
        if (stacks.isEmpty()) return;
        for (SlurryStack stack : stacks) {
            SlurryStack toInsert = stack.copy();
            for (ISlurryTank tank : tanks) {
                toInsert = tank.insert(toInsert, Action.EXECUTE, AutomationType.INTERNAL);
                if (toInsert.isEmpty()) break;
            }
            if (!toInsert.isEmpty()) {
                MekanismExtras.LOGGER.error("Failed to insert slurry {} during execute, simulation should have guaranteed success.", stack);
            }
        }
    }
}
