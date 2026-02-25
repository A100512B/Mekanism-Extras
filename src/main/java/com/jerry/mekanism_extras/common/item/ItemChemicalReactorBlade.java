package com.jerry.mekanism_extras.common.item;

import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorRotor;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

public class ItemChemicalReactorBlade extends Item {

    public ItemChemicalReactorBlade(Properties properties) {
        super(properties);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return WorldUtils.getTileEntity(TileEntityChemicalReactorRotor.class, level, pos) == null;
    }
}
