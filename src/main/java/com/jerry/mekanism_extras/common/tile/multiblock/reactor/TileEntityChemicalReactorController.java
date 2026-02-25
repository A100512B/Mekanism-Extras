package com.jerry.mekanism_extras.common.tile.multiblock.reactor;

import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData;
import com.jerry.mekanism_extras.common.registries.ExtraBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityChemicalReactorController extends TileEntityChemicalReactorCasing {

    public TileEntityChemicalReactorController(BlockPos pos, BlockState state) {
        super(ExtraBlock.CHEMICAL_REACTOR_CONTROLLER, pos, state);
    }

    @Override
    protected boolean onUpdateServer(ChemicalReactorMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        setActive(multiblock.isFormed());
        return needsPacket;
    }

    @Override
    public boolean canBeMaster() {
        return true;
    }
}
