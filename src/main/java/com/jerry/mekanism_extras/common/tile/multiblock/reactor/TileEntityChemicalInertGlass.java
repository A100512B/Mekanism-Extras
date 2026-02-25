package com.jerry.mekanism_extras.common.tile.multiblock.reactor;

import com.jerry.mekanism_extras.common.registries.ExtraBlock;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityChemicalInertGlass extends TileEntityStructuralMultiblock {

    public TileEntityChemicalInertGlass(BlockPos pos, BlockState state) {
        super(ExtraBlock.CHEMICAL_INERT_GLASS, pos, state);
    }

    @Override
    public boolean canInterface(MultiblockManager<?> manager) {
        return false;
    }
}
