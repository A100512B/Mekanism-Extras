package com.jerry.mekanism_extras.common.content.reactor;

import com.jerry.mekanism_extras.common.ExtraLang;
import com.jerry.mekanism_extras.common.registries.ExtraBlockType;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorController;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorRotor;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.common.MekanismLang;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.FormationProtocol.CasingType;
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult;
import mekanism.common.lib.multiblock.FormationProtocol.StructureRequirement;
import mekanism.common.lib.multiblock.StructureHelper;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Map;

public class ChemicalReactorValidator extends CuboidStructureValidator<ChemicalReactorMultiblockData> {

    private static final VoxelCuboid MIN_CUBOID = new VoxelCuboid(7, 6, 7);
    private static final VoxelCuboid MAX_CUBOID = new VoxelCuboid(7, 36, 7);

    @Override
    protected StructureRequirement getStructureRequirement(BlockPos pos) {
        pos = pos.subtract(cuboid.getMinPos());
        double dist = pos.distManhattan(new BlockPos(3, pos.getY(), 3));
        // Some math tricks
        if (dist >= 5)
            return StructureRequirement.IGNORED;
        else {
            if (pos.getY() == 0 || pos.getY() == cuboid.height() - 1)
                return StructureRequirement.FRAME;
            else if (dist <= 3)
                return StructureRequirement.INNER;
            else
                return StructureRequirement.OTHER;
        }
    }

    @Override
    protected boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        return super.validateInner(state, chunkMap, pos) ||
                BlockType.is(state.getBlock(), ExtraBlockType.CHEMICAL_REACTOR_ROTOR);
    }

    @Override
    protected CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        if (BlockType.is(block, ExtraBlockType.CHEMICAL_REACTOR_CASING, ExtraBlockType.CHEMICAL_INERT_GLASS)) {
            return CasingType.FRAME;
        } else if (BlockType.is(block, ExtraBlockType.BASIC_CHEMICAL_REACTOR_ITEM_IO_HATCH,
                ExtraBlockType.ADVANCED_CHEMICAL_REACTOR_ITEM_IO_HATCH,
                ExtraBlockType.ELITE_CHEMICAL_REACTOR_ITEM_IO_HATCH,
                ExtraBlockType.ULTIMATE_CHEMICAL_REACTOR_ITEM_IO_HATCH,
                ExtraBlockType.ABSOLUTE_CHEMICAL_REACTOR_ITEM_IO_HATCH,
                ExtraBlockType.SUPREME_CHEMICAL_REACTOR_ITEM_IO_HATCH,
                ExtraBlockType.COSMIC_CHEMICAL_REACTOR_ITEM_IO_HATCH,
                ExtraBlockType.INFINITE_CHEMICAL_REACTOR_ITEM_IO_HATCH,
                ExtraBlockType.BASIC_CHEMICAL_REACTOR_FLUID_IO_HATCH,
                ExtraBlockType.ADVANCED_CHEMICAL_REACTOR_FLUID_IO_HATCH,
                ExtraBlockType.ELITE_CHEMICAL_REACTOR_FLUID_IO_HATCH,
                ExtraBlockType.ULTIMATE_CHEMICAL_REACTOR_FLUID_IO_HATCH,
                ExtraBlockType.ABSOLUTE_CHEMICAL_REACTOR_FLUID_IO_HATCH,
                ExtraBlockType.SUPREME_CHEMICAL_REACTOR_FLUID_IO_HATCH,
                ExtraBlockType.COSMIC_CHEMICAL_REACTOR_FLUID_IO_HATCH,
                ExtraBlockType.INFINITE_CHEMICAL_REACTOR_FLUID_IO_HATCH,
                ExtraBlockType.BASIC_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH,
                ExtraBlockType.ADVANCED_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH,
                ExtraBlockType.ELITE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH,
                ExtraBlockType.ULTIMATE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH,
                ExtraBlockType.ABSOLUTE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH,
                ExtraBlockType.SUPREME_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH,
                ExtraBlockType.COSMIC_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH,
                ExtraBlockType.INFINITE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH)) {
            return CasingType.VALVE;
        } else if (BlockType.is(block, ExtraBlockType.CHEMICAL_REACTOR_CONTROLLER)) {
            return CasingType.OTHER;
        }
        return CasingType.INVALID;
    }

    @Override
    public boolean precheck() {
        return (cuboid = StructureHelper.fetchCuboid(structure, MIN_CUBOID, MAX_CUBOID)) != null;
    }

    @Override
    public FormationResult postcheck(ChemicalReactorMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        for (BlockPos pos : structure.internalLocations) {
            BlockPos relativePos = pos.subtract(cuboid.getMinPos());
            boolean validRotorPos = relativePos.getX() == 3 && relativePos.getY() > 0
                    && relativePos.getY() < cuboid.height() && relativePos.getZ() == 3;

            if (WorldUtils.getTileEntity(world, chunkMap, pos) instanceof TileEntityChemicalReactorRotor rotor) {
                if (rotor.blades != 2 || !validRotorPos)
                    return FormationResult.fail(ExtraLang.CHEMICAL_REACTOR_MALFORMED_ROTOR);
            } else {
                if (validRotorPos)
                    return FormationResult.fail(ExtraLang.CHEMICAL_REACTOR_MALFORMED_ROTOR);
            }
        }

        return super.postcheck(structure, chunkMap);
    }

    @Override
    protected FormationResult validateFrame(FormationProtocol<ChemicalReactorMultiblockData> ctx, BlockPos pos, BlockState state, CasingType type, boolean needsFrame) {
        boolean isControllerPos = pos.getY() == cuboid.getMaxPos().getY() &&
                pos.getX() == cuboid.getMinPos().getX() + 3 &&
                pos.getZ() == cuboid.getMinPos().getZ() + 3;
        boolean controller = structure.getTile(pos) instanceof TileEntityChemicalReactorController;
        if (isControllerPos && !controller) {
            return FormationResult.fail(MekanismLang.MULTIBLOCK_INVALID_NO_CONTROLLER);
        } else if (!isControllerPos && controller) {
            return FormationResult.fail(MekanismLang.MULTIBLOCK_INVALID_CONTROLLER_CONFLICT, true);
        }
        return super.validateFrame(ctx, pos, state, type, needsFrame);
    }
}
