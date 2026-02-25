package com.jerry.mekanism_extras.common.content.reactor;

import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorItemIOHatch;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongObjectMutablePair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mekanism.api.Action;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.lib.inventory.HashedItem;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InputItemIndexManager {

    private final ChemicalReactorMultiblockData data;
    private final Level level;
    // Item may have NBT differences, so use HashedItem instead of Item
    private final Map<HashedItem, LongObjectMutablePair<ObjectArrayList<IInventorySlot>>> itemIndex = new Object2ObjectOpenHashMap<>();

    public InputItemIndexManager(ChemicalReactorMultiblockData data, Level level) {
        this.data = data;
        this.level = level;
    }

    public void updateIndex(IInventorySlot slot, ItemStack oldStack, ItemStack newStack) {
        itemIndex.entrySet().stream().filter(entry -> entry.getValue().right().contains(slot))
                .findFirst()
                .ifPresentOrElse(entry -> {
                    // 1. old type == new type or the slot gets drained
                    HashedItem oldType = HashedItem.create(oldStack);
                    HashedItem newType = HashedItem.create(newStack);
                    LongObjectMutablePair<ObjectArrayList<IInventorySlot>> pair = entry.getValue();
                    if (oldType.equals(newType) || (oldType.getItem() != Items.AIR && newType.getItem() == Items.AIR)) {
                        // I hope neither the two stacks are empty...
                        int diff = newStack.getCount() - oldStack.getCount();
                        pair.left(pair.leftLong() + diff);
                        if (newStack.isEmpty())
                            pair.right().remove(slot);
                    } else {
                        // 2. old type != new type
                        pair.right().remove(slot);
                        pair.left(pair.leftLong() - oldStack.getCount());
                        itemIndex.compute(newType, (item, pair1) -> {
                            if (pair1 != null) {
                                pair1.left(pair1.leftLong() + newStack.getCount());
                                pair1.right().add(slot);
                                return pair1;
                            } else {
                                return new LongObjectMutablePair<>(newStack.getCount(), new ObjectArrayList<>(List.of(slot)));
                            }
                        });
                    }
                    long l = pair.leftLong();
                    // Check if we should remove an empty entry
                    if (l <= 0) {
                        // Shouldn't be less than 0, but in case something wrong happens
                        if (l < 0)
                            MekanismExtras.LOGGER.warn("Try to extract too many items from Chemical Reactor, got {} {}s finally.", l, oldType.getItem());
                        itemIndex.remove(oldType);
                        data.shouldRequeryRecipe = true;
                    } else data.shouldRequeryRecipe = false;
                }, () -> {
                    // 3. empty tank gets filled
                    itemIndex.compute(HashedItem.create(newStack), (item, pair) -> {
                        if (pair != null) {
                            pair.left(pair.leftLong() + newStack.getCount());
                            pair.right().add(slot);
                            data.shouldRequeryRecipe = false;
                            return pair;
                        } else {
                            data.shouldRequeryRecipe = !data.recipeQueried;
                            return new LongObjectMutablePair<>(newStack.getCount(), new ObjectArrayList<>(List.of(slot)));
                        }
                    });
                });
    }

    public void init(List<BlockPos> inputItemHatches) {
        itemIndex.putAll(inputItemHatches.stream().map(pos -> WorldUtils.getTileEntity(TileEntityChemicalReactorItemIOHatch.class, level, pos, true))
                .filter(Objects::nonNull)
                .flatMap(tile -> tile.getInventorySlots(null).stream())
                .collect(Collectors.toMap(
                        slot -> HashedItem.create(slot.getStack()),
                        slot -> new LongObjectMutablePair<>(slot.getCount(), new ObjectArrayList<>() {{
                            add(slot);
                        }}),
                        (pair1, pair2) ->
                                new LongObjectMutablePair<>(pair1.leftLong() + pair2.leftLong(),
                                        new ObjectArrayList<>(Stream.concat(pair1.right().stream(), pair2.right().stream()).toList()))
                )));
    }

    public long getCount(HashedItem item) {
        LongObjectMutablePair<ObjectArrayList<IInventorySlot>> pair = itemIndex.get(item);
        return pair == null ? 0 : pair.leftLong();
    }

    public List<ItemStack> getInputItems() {
        return new ObjectArrayList<>(itemIndex.keySet().stream().map(hashedItem -> hashedItem.createStack(1)).toList());
    }

    public void removeFromIndex(IInventorySlot slot) {
        // An empty slot should not appear in our index, so there's no need to look up for it
        if (slot.isEmpty()) return;
        itemIndex.values().stream().filter(pair -> pair.right().contains(slot))
                .map(Pair::right)
                .findFirst()
                .ifPresent(list -> list.remove(slot));
    }

    public void addToIndex(IInventorySlot slot) {
        // We don't need to add an empty slot
        if (slot.isEmpty()) return;
        updateIndex(slot, ItemStack.EMPTY, slot.getStack());
    }

    public void consume(ItemStack stack) {
        consume(stack, stack.getCount());
    }

    public void consume(ItemStack stack, long count) {
        if (stack.isEmpty()) return;
        if (count <= 0) return;
        HashedItem type = HashedItem.create(stack);
        if (!itemIndex.containsKey(type)) return;
        LongObjectMutablePair<ObjectArrayList<IInventorySlot>> pair = itemIndex.get(type);
        if (count > pair.leftLong()) return;
        for (IInventorySlot slot : pair.right()) {
            // The shrinking operation should call updateIndex
            count -= slot.shrinkStack(safeULong2Int(count), Action.EXECUTE);
        }
    }

    public void clear() {
        itemIndex.clear();
    }

    private static int safeULong2Int(long ul) {
        if (ul > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) ul;
    }
}
