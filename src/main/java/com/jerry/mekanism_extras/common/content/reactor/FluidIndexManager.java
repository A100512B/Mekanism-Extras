package com.jerry.mekanism_extras.common.content.reactor;

import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorFluidIOHatch;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongObjectMutablePair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mekanism.api.Action;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluidIndexManager {

    private final ChemicalReactorMultiblockData data;
    private final Level level;
    // Fluid stacks may also have NBT differences, but ignore since it's really a rare case
    public final Map<Fluid, LongObjectMutablePair<ObjectArrayList<IExtendedFluidTank>>> fluidIndex = new Object2ObjectOpenHashMap<>();

    public FluidIndexManager(ChemicalReactorMultiblockData data, Level level) {
        this.data = data;
        this.level = level;
    }

    public void updateIndex(IExtendedFluidTank tank, FluidStack oldStack, FluidStack newStack) {
        fluidIndex.entrySet().stream().filter(entry -> entry.getValue().right().contains(tank))
                .findFirst()
                .ifPresentOrElse(entry -> {
                    // 1. old type == new type or the tank gets drained
                    Fluid oldType = oldStack.getFluid();
                    Fluid newType = newStack.getFluid();
                    LongObjectMutablePair<ObjectArrayList<IExtendedFluidTank>> pair = entry.getValue();
                    if (oldType == newType || (!oldStack.isEmpty() && newStack.isEmpty())) {
                        // I hope neither the two stacks are empty...
                        int diff = newStack.getAmount() - oldStack.getAmount();
                        pair.left(pair.leftLong() + diff);
                        if (newStack.isEmpty())
                            pair.right().remove(tank);
                    } else {
                        // 2. old type != new type
                        pair.right().remove(tank);
                        pair.left(pair.leftLong() - oldStack.getAmount());
                        fluidIndex.compute(newType, (fluid, pair1) -> {
                            if (pair1 != null) {
                                pair1.left(pair1.leftLong() + newStack.getAmount());
                                pair1.right().add(tank);
                                return pair1;
                            } else {
                                return new LongObjectMutablePair<>(newStack.getAmount(), new ObjectArrayList<>(List.of(tank)));
                            }
                        });
                    }
                    long l = pair.leftLong();
                    // Check if we should remove an empty entry
                    if (l <= 0) {
                        // Shouldn't be less than 0, but in case something wrong happens
                        if (l < 0)
                            MekanismExtras.LOGGER.warn("Try to extract too much fluid from Chemical Reactor, got {} mB {} finally.", l, oldType.getFluidType());
                        fluidIndex.remove(oldType);
                        data.shouldRequeryRecipe = true;
                    } else data.shouldRequeryRecipe = false;
                }, () -> {
                    // 3. empty tank gets filled
                    fluidIndex.compute(newStack.getFluid(), (fluid, pair) -> {
                        if (pair != null) {
                            pair.left(pair.leftLong() + newStack.getAmount());
                            pair.right().add(tank);
                            data.shouldRequeryRecipe = false;
                            return pair;
                        } else {
                            data.shouldRequeryRecipe = !data.recipeQueried;
                            return new LongObjectMutablePair<>(newStack.getAmount(), new ObjectArrayList<>(List.of(tank)));
                        }
                    });
                });
    }

    public void init(List<BlockPos> inputFluidHatches) {
        fluidIndex.putAll(inputFluidHatches.stream().map(pos -> WorldUtils.getTileEntity(TileEntityChemicalReactorFluidIOHatch.class, level, pos, true))
                .filter(Objects::nonNull)
                .flatMap(tile -> tile.getFluidTanks(null).stream())
                .collect(Collectors.toMap(
                        tank -> tank.getFluid().getFluid(),
                        tank -> new LongObjectMutablePair<>(tank.getFluidAmount(), new ObjectArrayList<>() {{
                            add(tank);
                        }}),
                        (pair1, pair2) ->
                                new LongObjectMutablePair<>(pair1.leftLong() + pair2.leftLong(),
                                        new ObjectArrayList<>(Stream.concat(pair1.right().stream(), pair2.right().stream()).toList()))
                )));
    }

    public long getCount(Fluid fluid) {
        LongObjectMutablePair<ObjectArrayList<IExtendedFluidTank>> pair = fluidIndex.get(fluid);
        return pair == null ? 0 : pair.firstLong();
    }

    /**
     * @apiNote The stacks are all with a size of 1, as the type doesn't matter.
     */
    public List<FluidStack> getInputFluids() {
        return new ObjectArrayList<>(fluidIndex.keySet().stream().map(fluid -> new FluidStack(fluid, 1)).toList());
    }

    public void removeFromIndex(IExtendedFluidTank tank) {
        // An empty tank should not appear in our index, so there's no need to look up for it
        if (tank.isEmpty()) return;
        fluidIndex.values().stream().filter(pair -> pair.right().contains(tank))
                .map(Pair::right)
                .findFirst()
                .ifPresent(list -> list.remove(tank));
    }

    public void addToIndex(IExtendedFluidTank tank) {
        // We don't need to add an empty tank
        if (tank.isEmpty()) return;
        updateIndex(tank, FluidStack.EMPTY, tank.getFluid());
    }

    public void consume(FluidStack stack) {
        consume(stack, stack.getAmount());
    }

    public void consume(FluidStack stack, long amount) {
        if (stack.isEmpty()) return;
        Fluid type = stack.getFluid();
        if (!fluidIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<IExtendedFluidTank>> pair = fluidIndex.get(type);
        if (amount > pair.leftLong()) return;
        for (IExtendedFluidTank tank : pair.right()) {
            // The shrinking operation should call updateIndex
            amount -= tank.shrinkStack(safeULong2Int(amount), Action.EXECUTE);
        }
    }

    private static int safeULong2Int(long ul) {
        if (ul > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) ul;
    }
}
