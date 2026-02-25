package com.jerry.mekanism_extras.common.tile.multiblock.reactor;

import com.jerry.mekanism_extras.common.ExtraLang;
import com.jerry.mekanism_extras.common.content.reactor.ChemicalReactorMultiblockData;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.text.EnumColor;
import mekanism.common.lib.multiblock.IMultiblockEjector;
import mekanism.common.util.text.BooleanStateDisplay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class TileEntityChemicalReactorIOHatchBase
        extends TileEntityChemicalReactorCasing
        implements IMultiblockEjector {

    protected Set<Direction> outputDirections = Collections.emptySet();
    protected List<IntIntImmutablePair> spec = new ArrayList<>();
    protected boolean ofBasicTier;

    public TileEntityChemicalReactorIOHatchBase(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    protected abstract void precomputeSpec();

    public abstract void outputContent(ChemicalReactorMultiblockData data);

    @Override
    protected void presetVariables() {
        super.presetVariables();
        precomputeSpec();
    }

    @Override
    public void setEjectSides(Set<Direction> sides) {
        outputDirections = sides;
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            boolean oldMode = getActive();
            setActive(!oldMode);
            player.displayClientMessage(ExtraLang.CHEMICAL_REACTOR_IO_HATCH_MODE.translateColored(EnumColor.GRAY, BooleanStateDisplay.InputOutput.of(oldMode, true)), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onUpdateServer(ChemicalReactorMultiblockData data) {
        boolean needPacket = super.onUpdateServer(data);
        if (data.isFormed() && getActive()) {
            outputContent(data);
        }
        return needPacket;
    }
}
