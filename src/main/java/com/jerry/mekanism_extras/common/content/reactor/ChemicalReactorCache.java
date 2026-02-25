package com.jerry.mekanism_extras.common.content.reactor;

import com.jerry.mekanism_extras.api.ExtraNBTConstants;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData.ReactionCondition;
import mekanism.common.lib.multiblock.MultiblockCache;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;

import java.util.*;

public class ChemicalReactorCache extends MultiblockCache<ChemicalReactorMultiblockData> {

    private int maxParallel = 16;
    private double speedMultiplier = 1D;
    private EnumSet<ReactionCondition> supportedConditions = EnumSet.noneOf(ReactionCondition.class);
    private List<BlockPos> inputItemHatches = new ArrayList<>();
    private List<BlockPos> inputFluidHatches = new ArrayList<>();
    private List<BlockPos> inputChemicalHatches = new ArrayList<>();
    private List<BlockPos> outputItemHatches = new ArrayList<>();
    private List<BlockPos> outputFluidHatches = new ArrayList<>();
    private List<BlockPos> outputChemicalHatches = new ArrayList<>();

    @Override
    public void apply(ChemicalReactorMultiblockData data) {
        super.apply(data);
        data.maxParallel = this.maxParallel;
        data.speedMultiplier = this.speedMultiplier;
        data.supportedConditions = this.supportedConditions;
        data.inputItemHatches = this.inputItemHatches;
        data.inputFluidHatches = this.inputFluidHatches;
        data.inputChemicalHatches = this.inputChemicalHatches;
        data.outputItemHatches = this.outputItemHatches;
        data.outputFluidHatches = this.outputFluidHatches;
        data.outputChemicalHatches = this.outputChemicalHatches;
    }

    @Override
    public void sync(ChemicalReactorMultiblockData data) {
        super.sync(data);
        this.maxParallel = data.maxParallel;
        this.speedMultiplier = data.speedMultiplier;
        this.supportedConditions = data.supportedConditions;
        this.inputItemHatches = data.inputItemHatches;
        this.inputFluidHatches = data.inputFluidHatches;
        this.inputChemicalHatches = data.inputChemicalHatches;
        this.outputItemHatches = data.outputItemHatches;
        this.outputFluidHatches = data.outputFluidHatches;
        this.outputChemicalHatches = data.outputChemicalHatches;
    }

    @Override
    public void load(CompoundTag nbtTags) {
        super.load(nbtTags);
        this.maxParallel = nbtTags.getInt(ExtraNBTConstants.MAX_PARALLEL);
        this.speedMultiplier = nbtTags.getDouble(ExtraNBTConstants.SPEED_MULTIPLIER);
        this.supportedConditions = EnumSet.noneOf(ReactionCondition.class);
        ListTag conditions = nbtTags.getList(ExtraNBTConstants.SUPPORTED_CONDITIONS, CompoundTag.TAG_STRING);
        conditions.forEach(condition -> supportedConditions.add(ReactionCondition.byId(condition.getAsString())));
        this.inputItemHatches = new ArrayList<>();
        ListTag itemInputs = nbtTags.getList(ExtraNBTConstants.INPUT_ITEM_HATCHES, CompoundTag.TAG_LIST);
        itemInputs.forEach(input -> this.inputItemHatches.add(NbtUtils.readBlockPos((CompoundTag) input)));
        this.inputFluidHatches = new ArrayList<>();
        ListTag fluidInputs = nbtTags.getList(ExtraNBTConstants.INPUT_FLUID_HATCHES, CompoundTag.TAG_LIST);
        fluidInputs.forEach(input -> this.inputFluidHatches.add(NbtUtils.readBlockPos((CompoundTag) input)));
        this.inputChemicalHatches = new ArrayList<>();
        ListTag chemicalInputs = nbtTags.getList(ExtraNBTConstants.INPUT_CHEMICAL_HATCHES, CompoundTag.TAG_LIST);
        chemicalInputs.forEach(input -> this.inputChemicalHatches.add(NbtUtils.readBlockPos((CompoundTag) input)));
        this.outputItemHatches = new ArrayList<>();
        ListTag itemOutputs = nbtTags.getList(ExtraNBTConstants.OUTPUT_ITEM_HATCHES, CompoundTag.TAG_LIST);
        itemOutputs.forEach(output -> this.outputItemHatches.add(NbtUtils.readBlockPos((CompoundTag) output)));
        this.outputFluidHatches = new ArrayList<>();
        ListTag fluidOutputs = nbtTags.getList(ExtraNBTConstants.OUTPUT_FLUID_HATCHES, CompoundTag.TAG_LIST);
        fluidOutputs.forEach(output -> this.outputFluidHatches.add(NbtUtils.readBlockPos((CompoundTag) output)));
        this.outputChemicalHatches = new ArrayList<>();
        ListTag chemicalOutputs = nbtTags.getList(ExtraNBTConstants.OUTPUT_CHEMICAL_HATCHES, CompoundTag.TAG_LIST);
        chemicalOutputs.forEach(output -> this.outputChemicalHatches.add(NbtUtils.readBlockPos((CompoundTag) output)));
    }

    @Override
    public void save(CompoundTag nbtTags) {
        super.save(nbtTags);
        nbtTags.putInt(ExtraNBTConstants.MAX_PARALLEL, this.maxParallel);
        nbtTags.putDouble(ExtraNBTConstants.SPEED_MULTIPLIER, this.speedMultiplier);
        ListTag conditions = new ListTag();
        this.supportedConditions.forEach(condition -> conditions.add(StringTag.valueOf(condition.id)));
        nbtTags.put(ExtraNBTConstants.SUPPORTED_CONDITIONS, conditions);
        ListTag itemInputs = new ListTag();
        this.inputItemHatches.forEach(input -> itemInputs.add(NbtUtils.writeBlockPos(input)));
        nbtTags.put(ExtraNBTConstants.INPUT_ITEM_HATCHES, itemInputs);
        ListTag fluidInputs = new ListTag();
        this.inputFluidHatches.forEach(input -> fluidInputs.add(NbtUtils.writeBlockPos(input)));
        nbtTags.put(ExtraNBTConstants.INPUT_FLUID_HATCHES, fluidInputs);
        ListTag chemicalInputs = new ListTag();
        this.inputChemicalHatches.forEach(input -> chemicalInputs.add(NbtUtils.writeBlockPos(input)));
        nbtTags.put(ExtraNBTConstants.INPUT_CHEMICAL_HATCHES, chemicalInputs);
        ListTag itemOutputs = new ListTag();
        this.outputItemHatches.forEach(output -> itemOutputs.add(NbtUtils.writeBlockPos(output)));
        nbtTags.put(ExtraNBTConstants.OUTPUT_ITEM_HATCHES, itemOutputs);
        ListTag fluidOutputs = new ListTag();
        this.outputFluidHatches.forEach(output -> fluidOutputs.add(NbtUtils.writeBlockPos(output)));
        nbtTags.put(ExtraNBTConstants.OUTPUT_FLUID_HATCHES, fluidOutputs);
        ListTag chemicalOutputs = new ListTag();
        this.outputChemicalHatches.forEach(output -> chemicalOutputs.add(NbtUtils.writeBlockPos(output)));
        nbtTags.put(ExtraNBTConstants.OUTPUT_CHEMICAL_HATCHES, chemicalOutputs);
    }

    @Override
    public void merge(MultiblockCache<ChemicalReactorMultiblockData> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        ChemicalReactorCache chemicalReactorCache = (ChemicalReactorCache) mergeCache;
        this.maxParallel = Math.max(this.maxParallel, chemicalReactorCache.maxParallel);
        this.speedMultiplier = Math.max(this.speedMultiplier, chemicalReactorCache.speedMultiplier);
        this.supportedConditions.addAll(chemicalReactorCache.supportedConditions);
        this.inputItemHatches.addAll(chemicalReactorCache.inputItemHatches);
        this.inputFluidHatches.addAll(chemicalReactorCache.inputFluidHatches);
        this.inputChemicalHatches.addAll(chemicalReactorCache.inputChemicalHatches);
        this.outputItemHatches.addAll(chemicalReactorCache.outputItemHatches);
        this.outputFluidHatches.addAll(chemicalReactorCache.outputFluidHatches);
        this.outputChemicalHatches.addAll(chemicalReactorCache.outputChemicalHatches);
    }
}
