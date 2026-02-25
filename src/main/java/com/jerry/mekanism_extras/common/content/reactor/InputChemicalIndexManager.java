package com.jerry.mekanism_extras.common.content.reactor;

import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorChemicalIOHatch;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongObjectMutablePair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mekanism.api.Action;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InputChemicalIndexManager {

    private final ChemicalReactorMultiblockData data;
    private final Level level;
    private final Map<Gas, LongObjectMutablePair<ObjectArrayList<IGasTank>>> gasIndex = new Object2ObjectOpenHashMap<>();
    private final Map<InfuseType, LongObjectMutablePair<ObjectArrayList<IInfusionTank>>> infusionIndex = new Object2ObjectOpenHashMap<>();
    private final Map<Pigment, LongObjectMutablePair<ObjectArrayList<IPigmentTank>>> pigmentIndex = new Object2ObjectOpenHashMap<>();
    private final Map<Slurry, LongObjectMutablePair<ObjectArrayList<ISlurryTank>>> slurryIndex = new Object2ObjectOpenHashMap<>();

    public InputChemicalIndexManager(ChemicalReactorMultiblockData data, Level level) {
        this.data = data;
        this.level = level;
    }

    public void updateIndex(IGasTank tank, GasStack oldStack, GasStack newStack) {
        gasIndex.entrySet().stream().filter(entry -> entry.getValue().right().contains(tank))
                .findFirst()
                .ifPresentOrElse(entry -> {
                    // 1. old type == new type or the tank gets drained
                    Gas oldType = oldStack.getType();
                    Gas newType = newStack.getType();
                    LongObjectMutablePair<ObjectArrayList<IGasTank>> pair = entry.getValue();
                    if (oldType == newType || (!oldType.isEmptyType() && newType.isEmptyType())) {
                        // I hope neither the two stacks are empty...
                        long diff = newStack.getAmount() - oldStack.getAmount();
                        pair.left(pair.leftLong() + diff);
                        if (newStack.isEmpty())
                            pair.right().remove(tank);
                    } else {
                        // 2. old type != new type
                        pair.right().remove(tank);
                        pair.left(pair.leftLong() - oldStack.getAmount());
                        gasIndex.compute(newType, (gas, pair1) -> {
                            if (pair1 != null) {
                                pair1.left(pair1.leftLong() + newStack.getAmount());
                                pair1.right().add(tank);
                                return pair1;
                            } else {
                                return new LongObjectMutablePair<>(newStack.getAmount(), new ObjectArrayList<>(List.of(tank)));
                            }
                        });
                    }
                    long l;
                    // Check if we should remove an empty entry
                    if ((l = pair.leftLong()) <= 0) {
                        // Shouldn't be less than 0, but in case something wrong happens
                        if (l < 0)
                            MekanismExtras.LOGGER.warn("Try to extract too much gas from Chemical Reactor, got {} mB {} finally.", l, oldType.getRegistryName());
                        gasIndex.remove(oldType);
                        data.shouldRequeryRecipe = true;
                    } else data.shouldRequeryRecipe = false;
                }, () -> {
                    // 3. empty tank gets filled
                    gasIndex.compute(newStack.getType(), (gas, pair) -> {
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

    public void updateIndex(IInfusionTank tank, InfusionStack oldStack, InfusionStack newStack) {
        infusionIndex.entrySet().stream().filter(entry -> entry.getValue().right().contains(tank))
                .findFirst()
                .ifPresentOrElse(entry -> {
                    // 1. old type == new type or the tank gets drained
                    InfuseType oldType = oldStack.getType();
                    InfuseType newType = newStack.getType();
                    LongObjectMutablePair<ObjectArrayList<IInfusionTank>> pair = entry.getValue();
                    if (oldType == newType || (!oldType.isEmptyType() && newType.isEmptyType())) {
                        // I hope neither the two stacks are empty...
                        long diff = newStack.getAmount() - oldStack.getAmount();
                        pair.left(pair.leftLong() + diff);
                        if (newStack.isEmpty())
                            pair.right().remove(tank);
                    } else {
                        // 2. old type != new type
                        pair.right().remove(tank);
                        pair.left(pair.leftLong() - oldStack.getAmount());
                        infusionIndex.compute(newType, (infuseType, pair1) -> {
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
                            MekanismExtras.LOGGER.warn("Try to extract too much infusion from Chemical Reactor, got {} mB {} finally.", l, oldType.getRegistryName());
                        infusionIndex.remove(oldType);
                        data.shouldRequeryRecipe = true;
                    } else data.shouldRequeryRecipe = false;
                }, () -> {
                    // 3. empty tank gets filled
                    infusionIndex.compute(newStack.getType(), (infuseType, pair) -> {
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

    public void updateIndex(IPigmentTank tank, PigmentStack oldStack, PigmentStack newStack) {
        pigmentIndex.entrySet().stream().filter(entry -> entry.getValue().right().contains(tank))
                .findFirst()
                .ifPresentOrElse(entry -> {
                    // 1. old type == new type or the tank gets drained
                    Pigment oldType = oldStack.getType();
                    Pigment newType = newStack.getType();
                    LongObjectMutablePair<ObjectArrayList<IPigmentTank>> pair = entry.getValue();
                    if (oldType == newType || (!oldType.isEmptyType() && newType.isEmptyType())) {
                        // I hope neither the two stacks are empty...
                        long diff = newStack.getAmount() - oldStack.getAmount();
                        pair.left(pair.leftLong() + diff);
                        if (newStack.isEmpty())
                            pair.right().remove(tank);
                    } else {
                        // 2. old type != new type
                        pair.right().remove(tank);
                        pair.left(pair.leftLong() - oldStack.getAmount());
                        pigmentIndex.compute(newType, (pigment, pair1) -> {
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
                            MekanismExtras.LOGGER.warn("Try to extract too much pigment from Chemical Reactor, got {} mB {} finally.", l, oldType.getRegistryName());
                        pigmentIndex.remove(oldType);
                        data.shouldRequeryRecipe = true;
                    } else data.shouldRequeryRecipe = false;
                }, () -> {
                    // 3. empty tank gets filled
                    pigmentIndex.compute(newStack.getType(), (pigment, pair) -> {
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


    public void updateIndex(ISlurryTank tank, SlurryStack oldStack, SlurryStack newStack) {
        slurryIndex.entrySet().stream().filter(entry -> entry.getValue().right().contains(tank))
                .findFirst()
                .ifPresentOrElse(entry -> {
                    // 1. old type == new type or the tank gets drained
                    Slurry oldType = oldStack.getType();
                    Slurry newType = newStack.getType();
                    LongObjectMutablePair<ObjectArrayList<ISlurryTank>> pair = entry.getValue();
                    if (oldType == newType || (!oldType.isEmptyType() && newType.isEmptyType())) {
                        // I hope neither the two stacks are empty...
                        long diff = newStack.getAmount() - oldStack.getAmount();
                        pair.left(pair.leftLong() + diff);
                        if (newStack.isEmpty())
                            pair.right().remove(tank);
                    } else {
                        // 2. old type != new type
                        pair.right().remove(tank);
                        pair.left(pair.leftLong() - oldStack.getAmount());
                        slurryIndex.compute(newType, (slurry, pair1) -> {
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
                            MekanismExtras.LOGGER.warn("Try to extract too much slurry from Chemical Reactor, got {} mB {} finally.", l, oldType.getRegistryName());
                        slurryIndex.remove(oldType);
                        data.shouldRequeryRecipe = true;
                    } else data.shouldRequeryRecipe = false;
                }, () -> {
                    // 3. empty tank gets filled
                    slurryIndex.compute(newStack.getType(), (slurry, pair) -> {
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

    public void init(List<BlockPos> inputChemicalHatches) {
        inputChemicalHatches.stream().map(pos -> WorldUtils.getTileEntity(TileEntityChemicalReactorChemicalIOHatch.class, level, pos, true))
                .filter(Objects::nonNull)
                .flatMap(tile -> tile.getCachedChemicalTanks().stream())
                .filter(tank -> tank.getCurrent() != MergedChemicalTank.Current.EMPTY)
                .collect(Collectors.groupingBy(MergedChemicalTank::getCurrent))
                .forEach((type, tanks) -> {
                    switch (type) {
                        case GAS -> gasIndex.putAll(tanks.stream().map(MergedChemicalTank::getGasTank)
                                .collect(Collectors.toMap(
                                        tank -> tank.getStack().getType(),
                                        tank -> new LongObjectMutablePair<>(tank.getStored(), new ObjectArrayList<>() {{
                                            add(tank);
                                        }}),
                                        (pair1, pair2) ->
                                                new LongObjectMutablePair<>(pair1.leftLong() + pair2.leftLong(),
                                                        new ObjectArrayList<>(Stream.concat(pair1.right().stream(), pair2.right().stream()).toList()))
                                )));
                        case INFUSION -> infusionIndex.putAll(tanks.stream().map(MergedChemicalTank::getInfusionTank)
                                .collect(Collectors.toMap(
                                        tank -> tank.getStack().getType(),
                                        tank -> new LongObjectMutablePair<>(tank.getStored(), new ObjectArrayList<>() {{
                                            add(tank);
                                        }}),
                                        (pair1, pair2) ->
                                                new LongObjectMutablePair<>(pair1.leftLong() + pair2.leftLong(),
                                                        new ObjectArrayList<>(Stream.concat(pair1.right().stream(), pair2.right().stream()).toList()))
                                )));
                        case PIGMENT -> pigmentIndex.putAll(tanks.stream().map(MergedChemicalTank::getPigmentTank)
                                .collect(Collectors.toMap(
                                        tank -> tank.getStack().getType(),
                                        tank -> new LongObjectMutablePair<>(tank.getStored(), new ObjectArrayList<>() {{
                                            add(tank);
                                        }}),
                                        (pair1, pair2) ->
                                                new LongObjectMutablePair<>(pair1.leftLong() + pair2.leftLong(),
                                                        new ObjectArrayList<>(Stream.concat(pair1.right().stream(), pair2.right().stream()).toList()))
                                )));
                        case SLURRY -> slurryIndex.putAll(tanks.stream().map(MergedChemicalTank::getSlurryTank)
                                .collect(Collectors.toMap(
                                        tank -> tank.getStack().getType(),
                                        tank -> new LongObjectMutablePair<>(tank.getStored(), new ObjectArrayList<>() {{
                                            add(tank);
                                        }}),
                                        (pair1, pair2) ->
                                                new LongObjectMutablePair<>(pair1.leftLong() + pair2.leftLong(),
                                                        new ObjectArrayList<>(Stream.concat(pair1.right().stream(), pair2.right().stream()).toList()))
                                )));
                    }
                });
    }

    public long getCount(Gas gas) {
        var pair = gasIndex.get(gas);
        return pair == null ? 0 : pair.leftLong();
    }

    public long getCount(InfuseType infuseType) {
        var pair = infusionIndex.get(infuseType);
        return pair == null ? 0 : pair.leftLong();
    }

    public long getCount(Pigment pigment) {
        var pair = pigmentIndex.get(pigment);
        return pair == null ? 0 : pair.leftLong();
    }

    public long getCount(Slurry slurry) {
        var pair = slurryIndex.get(slurry);
        return pair == null ? 0 : pair.leftLong();
    }

    public List<GasStack> getInputGases() {
        return new ObjectArrayList<>(gasIndex.keySet().stream().map(gas -> new GasStack(gas, 1))
                .toList());
    }

    public List<InfusionStack> getInputInfusions() {
        return new ObjectArrayList<>(infusionIndex.keySet().stream().map(infuseType -> new InfusionStack(infuseType, 1))
                .toList());
    }

    public List<PigmentStack> getInputPigments() {
        return new ObjectArrayList<>(pigmentIndex.keySet().stream().map(pigment -> new PigmentStack(pigment, 1))
                .toList());
    }

    public List<SlurryStack> getInputSlurries() {
        return new ObjectArrayList<>(slurryIndex.keySet().stream().map(slurry -> new SlurryStack(slurry, 1))
                .toList());
    }

    public void removeFromIndex(IGasTank tank) {
        // An empty tank should not appear in our index, so there's no need to look up for it
        if (tank.isEmpty()) return;
        gasIndex.values().stream().filter(pair -> pair.right().contains(tank))
                .map(Pair::right)
                .findFirst()
                .ifPresent(list -> list.remove(tank));
    }

    public void addToIndex(IGasTank tank) {
        // We don't need to add an empty tank
        if (tank.isEmpty()) return;
        updateIndex(tank, GasStack.EMPTY, tank.getStack());
    }

    public void consume(GasStack stack) {
        consume(stack, stack.getAmount());
    }

    public void consume(GasStack stack, long amount) {
        if (stack.isEmpty()) return;
        Gas type = stack.getType();
        if (!gasIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<IGasTank>> pair = gasIndex.get(type);
        if (amount > pair.leftLong()) return;
        for (IGasTank tank : pair.right()) {
            // The shrinking operation should call updateIndex
            amount -= tank.shrinkStack(amount, Action.EXECUTE);
        }
    }

    public void removeFromIndex(IInfusionTank tank) {
        // An empty tank should not appear in our index, so there's no need to look up for it
        if (tank.isEmpty()) return;
        infusionIndex.values().stream().filter(pair -> pair.right().contains(tank))
                .map(Pair::right)
                .findFirst()
                .ifPresent(list -> list.remove(tank));
    }

    public void addToIndex(IInfusionTank tank) {
        // We don't need to add an empty tank
        if (tank.isEmpty()) return;
        updateIndex(tank, InfusionStack.EMPTY, tank.getStack());
    }

    public void consume(InfusionStack stack) {
        if (stack.isEmpty()) return;
        InfuseType type = stack.getType();
        if (!infusionIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<IInfusionTank>> pair = infusionIndex.get(type);
        long count = stack.getAmount();
        if (count > pair.leftLong()) return;
        for (IInfusionTank tank : pair.right()) {
            // The shrinking operation should call updateIndex
            count -= tank.shrinkStack(count, Action.EXECUTE);
        }
    }

    public void consume(InfusionStack stack, long amount) {
        if (stack.isEmpty()) return;
        InfuseType type = stack.getType();
        if (!infusionIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<IInfusionTank>> pair = infusionIndex.get(type);
        if (amount > pair.leftLong()) return;
        for (IInfusionTank tank : pair.right()) {
            // The shrinking operation should call updateIndex
            amount -= tank.shrinkStack(amount, Action.EXECUTE);
        }
    }

    public void removeFromIndex(IPigmentTank tank) {
        // An empty tank should not appear in our index, so there's no need to look up for it
        if (tank.isEmpty()) return;
        pigmentIndex.values().stream().filter(pair -> pair.right().contains(tank))
                .map(Pair::right)
                .findFirst()
                .ifPresent(list -> list.remove(tank));
    }

    public void addToIndex(IPigmentTank tank) {
        // We don't need to add an empty tank
        if (tank.isEmpty()) return;
        updateIndex(tank, PigmentStack.EMPTY, tank.getStack());
    }

    public void consume(PigmentStack stack) {
        if (stack.isEmpty()) return;
        Pigment type = stack.getType();
        if (!pigmentIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<IPigmentTank>> pair = pigmentIndex.get(type);
        long count = stack.getAmount();
        if (count > pair.leftLong()) return;
        for (IPigmentTank tank : pair.right()) {
            // The shrinking operation should call updateIndex
            count -= tank.shrinkStack(count, Action.EXECUTE);
        }
    }

    public void consume(PigmentStack stack, long amount) {
        if (stack.isEmpty()) return;
        Pigment type = stack.getType();
        if (!pigmentIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<IPigmentTank>> pair = pigmentIndex.get(type);
        if (amount > pair.leftLong()) return;
        for (IPigmentTank tank : pair.right()) {
            // The shrinking operation should call updateIndex
            amount -= tank.shrinkStack(amount, Action.EXECUTE);
        }
    }

    public void removeFromIndex(ISlurryTank tank) {
        // An empty tank should not appear in our index, so there's no need to look up for it
        if (tank.isEmpty()) return;
        slurryIndex.values().stream().filter(pair -> pair.right().contains(tank))
                .map(Pair::right)
                .findFirst()
                .ifPresent(list -> list.remove(tank));
    }

    public void addToIndex(ISlurryTank tank) {
        // We don't need to add an empty tank
        if (tank.isEmpty()) return;
        updateIndex(tank, SlurryStack.EMPTY, tank.getStack());
    }

    public void consume(SlurryStack stack) {
        if (stack.isEmpty()) return;
        Slurry type = stack.getType();
        if (!slurryIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<ISlurryTank>> pair = slurryIndex.get(type);
        long count = stack.getAmount();
        if (count > pair.leftLong()) return;
        for (ISlurryTank tank : pair.right()) {
            // The shrinking operation should call updateIndex
            count -= tank.shrinkStack(count, Action.EXECUTE);
        }
    }

    public void consume(SlurryStack stack, long amount) {
        if (stack.isEmpty()) return;
        Slurry type = stack.getType();
        if (!slurryIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<ISlurryTank>> pair = slurryIndex.get(type);
        if (amount > pair.leftLong()) return;
        for (ISlurryTank tank : pair.right()) {
            // The shrinking operation should call updateIndex
            amount -= tank.shrinkStack(amount, Action.EXECUTE);
        }
    }

    public void clear() {
        gasIndex.clear();
        infusionIndex.clear();
        pigmentIndex.clear();
        slurryIndex.clear();
    }
}
