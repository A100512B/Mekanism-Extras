package com.jerry.mekanism_extras.common.command;

import com.jerry.generator_extras.common.genregistry.ExtraGenBlocks;
import com.jerry.mekanism_extras.common.registries.ExtraBlock;
import mekanism.common.command.builders.StructureBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ExtraBuilders {

    private ExtraBuilders() {}

    public static class NaquadahReactorBuilder extends StructureBuilder {

        public NaquadahReactorBuilder() {
            super(9, 9, 9);
        }

        @Override
        protected void build(Level world, BlockPos start, boolean empty) {
            buildPartialFrame(world, start, 2);
            buildWalls(world, start);
            // Count from 0, like arrays
            buildInteriorLayers(world, start, 1, 7, Blocks.AIR);
            world.setBlockAndUpdate(start.offset(4, 8, 4), ExtraGenBlocks.NAQUADAH_REACTOR_CONTROLLER.getBlock().defaultBlockState());
        }

        @Override
        protected Block getWallBlock(BlockPos pos) {
            return ExtraGenBlocks.NAQUADAH_REACTOR_CASING.getBlock();
        }

        @Override
        protected Block getCasing() {
            return ExtraGenBlocks.NAQUADAH_REACTOR_CASING.getBlock();
        }
    }

    public static class MatrixBuilder extends StructureBuilder {

        public MatrixBuilder() {
            super(18, 18, 18);
        }

        @Override
        public void build(Level world, BlockPos start, boolean empty) {
            buildFrame(world, start);
            buildWalls(world, start);
            if (empty) {
                buildInteriorLayers(world, start, 1, 16, Blocks.AIR);
            } else {
                buildInteriorLayers(world, start, 1, 15, ExtraBlock.INFINITE_INDUCTION_CELL.getBlock());
                buildInteriorLayer(world, start, 16, ExtraBlock.INFINITE_INDUCTION_PROVIDER.getBlock());
            }
        }

        @Override
        protected Block getCasing() {
            return ExtraBlock.REINFORCED_INDUCTION_CASING.getBlock();
        }
    }

    public static class PlasmaEvaporationPlantBuilder extends StructureBuilder {

        public PlasmaEvaporationPlantBuilder() {
            super(6, 36, 6);
        }

        @Override
        protected void build(Level world, BlockPos start, boolean empty) {
            buildFrame(world, start);
            buildWalls(world, start);
            buildInteriorLayers(world, start, 1, 34, Blocks.AIR);
            buildInteriorLayer(world, start, 18, ExtraGenBlocks.PLASMA_INSULATION_LAYER.getBlock());
            world.setBlockAndUpdate(start.offset(1, 1, 0), ExtraGenBlocks.PLASMA_EVAPORATION_CONTROLLER.getBlock().defaultBlockState());
        }

        @Override
        protected Block getCasing() {
            return ExtraGenBlocks.PLASMA_EVAPORATION_BLOCK.getBlock();
        }

        @Override
        protected Block getRoofBlock(BlockPos pos) {
            return ExtraGenBlocks.PLASMA_EVAPORATION_VENT.getBlock();
        }
    }


    public static class ChemicalReactorBuilder extends StructureBuilder {

        public ChemicalReactorBuilder() {
            super(7, 36, 7);
        }

        @Override
        protected void build(Level world, BlockPos start, boolean empty) {
            // It's a cylinder, so we cannot use the inherited methods
            for (int x = 0; x < sizeX; x++) {
                for (int z = 0; z < sizeZ; z++) {
                    int distToCenterSqr = (x-3) * (x-3) + (z-3) * (z-3);
                    // Some math tricks
                    if (distToCenterSqr == 8 || distToCenterSqr == 9 || distToCenterSqr == 10) {
                        // Border
                        world.setBlockAndUpdate(start.offset(x, 0, z), getCasing().defaultBlockState());
                        for (int y = 1; y < sizeY - 1; y++) {
                            world.setBlockAndUpdate(start.offset(x, y, z), ExtraBlock.CHEMICAL_INERT_GLASS.getBlock().defaultBlockState());
                        }
                        world.setBlockAndUpdate(start.offset(x, sizeY - 1, z), getCasing().defaultBlockState());
                    } else if (distToCenterSqr > 0) {
                        // Inside (not middle)
                        world.setBlockAndUpdate(start.offset(x, 0, z), getCasing().defaultBlockState());
                        world.setBlockAndUpdate(start.offset(x, sizeY - 1, z), getCasing().defaultBlockState());
                    } else if (distToCenterSqr == 0) {
                        // Middle
                        world.setBlockAndUpdate(start.offset(x, 0, z), getCasing().defaultBlockState());
                        for (int y = 1; y < sizeY - 1; y++) {
                            world.setBlockAndUpdate(start.offset(x, y, z), ExtraBlock.CHEMICAL_REACTOR_ROTOR.getBlock().defaultBlockState());
                        }
                        world.setBlockAndUpdate(start.offset(x, sizeY - 1, z), ExtraBlock.CHEMICAL_REACTOR_CONTROLLER.getBlock().defaultBlockState());
                    }
                }
            }
        }

        @Override
        protected Block getCasing() {
            return ExtraBlock.CHEMICAL_REACTOR_CASING.getBlock();
        }
    }
}
