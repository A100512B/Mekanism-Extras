package com.jerry.mekanism_extras.common.block.transmitter;

import com.jerry.mekanism_extras.common.tile.transmitter.ExtraTileEntityLogisticalTransporter;
import com.jerry.mekanism_extras.common.tile.transmitter.ExtraTileEntityLogisticalTransporterBase;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.block.interfaces.ITypeBlock;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import com.jerry.mekanism_extras.common.registry.ExtraTileEntityTypes;
import mekanism.common.tier.TransporterTier;

public class ExtraBlockLogisticalTransporter extends BlockLargeTransmitter implements ITypeBlock, IHasTileEntity<ExtraTileEntityLogisticalTransporterBase> {

    private final TransporterTier tier;

    public ExtraBlockLogisticalTransporter(TransporterTier tier) {
        super(properties -> properties.mapColor(tier.getBaseTier().getMapColor()));
        this.tier = tier;
    }

    @Override
    public BlockType getType() {
        return AttributeTier.getPassthroughType(this.tier);
    }

    public TileEntityTypeRegistryObject<ExtraTileEntityLogisticalTransporter> getTileType() {
        return switch (this.tier) {
            case BASIC -> ExtraTileEntityTypes.ABSOLUTE_LOGISTICAL_TRANSPORTER;
            case ADVANCED -> ExtraTileEntityTypes.SUPREME_LOGISTICAL_TRANSPORTER;
            case ELITE -> ExtraTileEntityTypes.COSMIC_LOGISTICAL_TRANSPORTER;
            case ULTIMATE -> ExtraTileEntityTypes.INFINITE_LOGISTICAL_TRANSPORTER;
        };
    }
}
