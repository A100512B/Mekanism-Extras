package com.jerry.mekanism_extras.common.tile.multiblock.reactor;

import com.jerry.mekanism_extras.common.block.attribute.ExtraAttribute;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData;
import com.jerry.mekanism_extras.common.tier.AdvancedItemIOHatchTier;
import com.jerry.mekanism_extras.common.tier.BasicItemIOHatchTier;
import com.jerry.mekanism_extras.common.util.ExtraGUIUtils;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.inventory.TransitRequest;
import mekanism.common.tile.transmitter.TileEntityLogisticalTransporterBase;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

public class TileEntityChemicalReactorItemIOHatch
    extends TileEntityChemicalReactorIOHatchBase {

    protected final List<IInventorySlot> cachedInventorySlots = new ArrayList<>();
    protected int slotCapacity;
    private final IntSupplier slotCapacitySupplier = () -> {
        if (slotCapacity == 0) precomputeSpec();
        return slotCapacity;
    };

    public TileEntityChemicalReactorItemIOHatch(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected void precomputeSpec() {
        AdvancedItemIOHatchTier tier = ExtraAttribute.getTier(getBlockType(), AdvancedItemIOHatchTier.class);
        if (tier == null) {
            BasicItemIOHatchTier tier1 = Attribute.getTier(getBlockType(), BasicItemIOHatchTier.class);
            spec = ExtraGUIUtils.getIOHatchSpec(tier1);
            slotCapacity = tier1.getMaxStackSize();
            ofBasicTier = true;
        } else {
            spec = ExtraGUIUtils.getIOHatchSpec(tier);
            slotCapacity = tier.getMaxStackSize();
            ofBasicTier = false;
        }
    }

    @Override
    @NotNull
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return side -> {
            if (cachedInventorySlots.isEmpty()) {
                spec.forEach(pair -> cachedInventorySlots.add(createInventorySlot(listener, pair.leftInt(), pair.rightInt())));
            }
            return cachedInventorySlots;
        };
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        Stream<IInventorySlot> slots = cachedInventorySlots.stream().filter(slot -> !slot.isEmpty());
        if (active) {
            slots.forEach(slot -> getMultiblock().itemIndex.removeFromIndex(slot));
        } else {
            slots.forEach(slot -> getMultiblock().itemIndex.addToIndex(slot));
        }
    }

    @Override
    public void outputContent(ChemicalReactorMultiblockData data) {
        Direction direction = getDirection();
        BlockEntity target = WorldUtils.getTileEntity(level, getBlockPos().relative(direction));
        TransitRequest ejectMap = InventoryUtils.getEjectItemMap(this, direction, getInventorySlots(direction));
        if (!ejectMap.isEmpty()) {
            TransitRequest.TransitResponse response;
            if (target instanceof TileEntityLogisticalTransporterBase transporter) {
                response = transporter.getTransmitter().insert(this, ejectMap, transporter.getTransmitter().getColor(), true, 0);
            } else {
                response = ejectMap.addToInventory(target, direction.getOpposite(), 0, false);
            }
            if (!response.isEmpty()) {
                response.useAll();
            }
        }
    }

    @Override
    public boolean persistInventory() {
        return true;
    }

    private IInventorySlot createInventorySlot(IContentsListener listener, int x, int y) {
        return new BasicInventorySlot(slotCapacitySupplier.getAsInt(), (stack, type) -> true,
                (stack, type) -> !getActive() || type == AutomationType.INTERNAL,
                stack -> true, listener, x, y) {

            @Override
            public void setStack(@NotNull ItemStack stack) {
                ItemStack old = getStack();
                super.setStack(stack);
                if (!getActive()) getMultiblock().itemIndex.updateIndex(this, old, getStack());
            }

            @Override
            public int setStackSize(int amount, @NotNull Action action) {
                ItemStack old = getStack();
                int result = super.setStackSize(amount, action);
                if (!getActive()) getMultiblock().itemIndex.updateIndex(this, old, getStack());
                return result;
            }

            @Override
            @NotNull
            public ItemStack insertItem(@NotNull ItemStack stack, @NotNull Action action, @NotNull AutomationType automationType) {
                ItemStack old = getStack();
                ItemStack result = super.insertItem(stack, action, automationType);
                if (!getActive()) getMultiblock().itemIndex.updateIndex(this, old, getStack());
                return result;
            }

            @Override
            @NotNull
            public ItemStack extractItem(int amount, @NotNull Action action, @NotNull AutomationType automationType) {
                ItemStack old = getStack();
                ItemStack result = super.extractItem(amount, action, automationType);
                getMultiblock().itemIndex.updateIndex(this, old, getStack());
                return result;
            }
        };
    }
}
