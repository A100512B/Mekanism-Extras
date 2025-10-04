package com.jerry.generator_extras.common.tile.plasma;

import com.jerry.generator_extras.common.content.plasma.PlasmaEvaporationMultiblockData;
import com.jerry.generator_extras.common.genregistry.ExtraGenBlocks;
import com.jerry.mekanism_extras.MekanismExtras;
import mekanism.api.NBTConstants;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityPlasmaEvaporationBlock
        extends TileEntityMultiblock<PlasmaEvaporationMultiblockData> {

    private boolean handleSound;
    private boolean prevActive;

    public TileEntityPlasmaEvaporationBlock(BlockPos pos, BlockState state) {
        this(ExtraGenBlocks.PLASMA_EVAPORATION_BLOCK, pos, state);
    }

    public TileEntityPlasmaEvaporationBlock(IBlockProvider provider, BlockPos pos, BlockState state) {
        super(provider, pos, state);
    }

    @Override
    public PlasmaEvaporationMultiblockData createMultiblock() {
        return new PlasmaEvaporationMultiblockData(this);
    }

    @Override
    public MultiblockManager<PlasmaEvaporationMultiblockData> getManager() {
        return MekanismExtras.plasmaEvaporationPlantManager;
    }

    @Override
    public boolean canBeMaster() {
        return false;
    }

    @Override
    protected boolean canPlaySound() {
        PlasmaEvaporationMultiblockData multiblock = getMultiblock();
        return multiblock.isFormed() && multiblock.lastProcessed > 0;
    }

    @Override
    protected boolean onUpdateServer(PlasmaEvaporationMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        boolean active = multiblock.isFormed() && multiblock.handlesSound(this) && multiblock.lastProcessed > 0;
        if (active != prevActive) {
            prevActive = active;
            needsPacket = true;
        }
        return needsPacket;
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag updateTag = super.getReducedUpdateTag();
        PlasmaEvaporationMultiblockData multiblock = getMultiblock();
        updateTag.putBoolean(NBTConstants.HANDLE_SOUND, multiblock.isFormed() && multiblock.handlesSound(this) && multiblock.lastProcessed > 0);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        super.handleUpdateTag(tag);
        NBTUtils.setBooleanIfPresent(tag, NBTConstants.HANDLE_SOUND, value -> handleSound = value);
    }
}
