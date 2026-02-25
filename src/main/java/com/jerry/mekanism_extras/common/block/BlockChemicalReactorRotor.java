package com.jerry.mekanism_extras.common.block;

import com.jerry.mekanism_extras.common.item.ItemChemicalReactorBlade;
import com.jerry.mekanism_extras.common.registries.ExtraBlockType;
import com.jerry.mekanism_extras.common.registries.ExtraItem;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorRotor;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockChemicalReactorRotor extends BlockTileModel<TileEntityChemicalReactorRotor, BlockTypeTile<TileEntityChemicalReactorRotor>> {

    public BlockChemicalReactorRotor() {
        super(ExtraBlockType.CHEMICAL_REACTOR_ROTOR, prop -> prop.mapColor(MapColor.COLOR_BLACK));
    }

    @NotNull
    @Override
    @Deprecated
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                 @NotNull BlockHitResult hit) {
        TileEntityChemicalReactorRotor tile = WorldUtils.getTileEntity(TileEntityChemicalReactorRotor.class, world, pos);
        if (tile == null) {
            return InteractionResult.PASS;
        } else if (world.isClientSide) {
            return genericClientActivated(player, hand);
        } else if (tile.tryWrench(state, player, hand, hit) != WrenchResult.PASS) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemChemicalReactorBlade) {
                if (tile.addBlade(true)) {
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
            }
        } else if (stack.isEmpty()) {
            if (tile.removeBlade()) {
                if (!player.isCreative()) {
                    player.setItemInHand(hand, ExtraItem.CHEMICAL_REACTOR_BLADE.getItemStack());
                    player.getInventory().setChanged();
                }
            }
        } else if (stack.getItem() instanceof ItemChemicalReactorBlade) {
            if (stack.getCount() < stack.getMaxStackSize()) {
                if (tile.removeBlade()) {
                    if (!player.isCreative()) {
                        stack.grow(1);
                        player.getInventory().setChanged();
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
