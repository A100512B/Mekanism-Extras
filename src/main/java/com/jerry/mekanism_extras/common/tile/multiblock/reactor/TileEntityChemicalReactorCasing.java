package com.jerry.mekanism_extras.common.tile.multiblock.reactor;

import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.api.ExtraNBTConstants;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData.ReactionCondition;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class TileEntityChemicalReactorCasing extends TileEntityMultiblock<ChemicalReactorMultiblockData> {

    public TileEntityChemicalReactorCasing(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    public ChemicalReactorMultiblockData createMultiblock() {
        return new ChemicalReactorMultiblockData(this);
    }

    @Override
    public MultiblockManager<ChemicalReactorMultiblockData> getManager() {
        return MekanismExtras.chemicalReactorManager;
    }

    @Override
    public boolean canBeMaster() {
        return false;
    }

    public EnumSet<ReactionCondition> getSupportedConditions() {
        return (EnumSet<ReactionCondition>) this.serializeNBT().getList(ExtraNBTConstants.SUPPORTED_CONDITIONS, CompoundTag.TAG_STRING)
                .stream()
                .map(tag -> ReactionCondition.byId(tag.getAsString()))
                .collect(Collectors.toSet());
    }
}
