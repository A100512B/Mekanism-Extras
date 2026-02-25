package com.jerry.mekanism_extras.common.registries;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jerry.mekanism_extras.api.ExtraUpgrade;
import com.jerry.mekanism_extras.common.ExtraLang;
import com.jerry.mekanism_extras.common.block.attribute.ExtraAttributeTier;
import com.jerry.mekanism_extras.common.block.attribute.ExtraAttributeUpgradeable;
import com.jerry.mekanism_extras.common.config.LoadConfig;
import com.jerry.mekanism_extras.common.content.blocktype.AdvancedFactory;
import com.jerry.mekanism_extras.common.content.blocktype.AdvancedMachine;
import com.jerry.mekanism_extras.common.integration.Addons;
import com.jerry.mekanism_extras.common.tier.*;
import com.jerry.mekanism_extras.common.tile.machine.TileEntityAdvancedElectricPump;
import com.jerry.mekanism_extras.common.tile.ExtraTileEntityBin;
import com.jerry.mekanism_extras.common.tile.ExtraTileEntityChemicalTank;
import com.jerry.mekanism_extras.common.tile.ExtraTileEntityEnergyCube;
import com.jerry.mekanism_extras.common.tile.ExtraTileEntityFluidTank;
import com.jerry.mekanism_extras.common.tile.ExtraTileEntityRadioactiveWasteBarrel;
import com.jerry.mekanism_extras.common.tile.multiblock.matrix.ExtraTileEntityInductionCell;
import com.jerry.mekanism_extras.common.tile.multiblock.matrix.ExtraTileEntityInductionProvider;
import com.jerry.mekanism_extras.common.tile.multiblock.matrix.TileEntityReinforcedInductionCasing;
import com.jerry.mekanism_extras.common.tile.multiblock.matrix.TileEntityReinforcedInductionPort;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.*;
import com.jerry.mekanism_extras.common.util.ExtraEnumUtils;
import com.jerry.mekanism_extras.common.util.ExtraFloatingLong;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import mekanism.api.Upgrade;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.*;
import mekanism.common.block.attribute.Attributes.AttributeCustomResistance;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.*;
import mekanism.common.content.blocktype.BlockType.BlockTypeBuilder;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.machine.*;
import mekanism.common.util.EnumUtils;

import java.util.EnumSet;
import java.util.function.Supplier;

public class ExtraBlockType {

    private static final Table<AdvancedFactoryTier, FactoryType, AdvancedFactory<?>> FACTORIES = HashBasedTable.create();

    // Enrichment Chamber
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityEnrichmentChamber> ENRICHMENT_CHAMBER = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.ENRICHMENT_CHAMBER, MekanismLang.DESCRIPTION_ENRICHMENT_CHAMBER, FactoryType.ENRICHING)
            .withGui(() -> MekanismContainerTypes.ENRICHMENT_CHAMBER)
            .withSound(MekanismSounds.ENRICHMENT_CHAMBER)
            .withEnergyConfig(MekanismConfig.usage.enrichmentChamber, MekanismConfig.storage.enrichmentChamber)
            .withComputerSupport("enrichmentChamber")
            .build();
    // Crusher
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityCrusher> CRUSHER = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CRUSHER, MekanismLang.DESCRIPTION_CRUSHER, FactoryType.CRUSHING)
            .withGui(() -> MekanismContainerTypes.CRUSHER)
            .withSound(MekanismSounds.CRUSHER)
            .withEnergyConfig(MekanismConfig.usage.crusher, MekanismConfig.storage.crusher)
            .withComputerSupport("crusher")
            .build();
    // Energized Smelter
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityEnergizedSmelter> ENERGIZED_SMELTER = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.ENERGIZED_SMELTER, MekanismLang.DESCRIPTION_ENERGIZED_SMELTER, FactoryType.SMELTING)
            .withGui(() -> MekanismContainerTypes.ENERGIZED_SMELTER)
            .withSound(MekanismSounds.ENERGIZED_SMELTER)
            .withEnergyConfig(MekanismConfig.usage.energizedSmelter, MekanismConfig.storage.energizedSmelter)
            .withComputerSupport("energizedSmelter")
            .build();
    // Precision Sawmill
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityPrecisionSawmill> PRECISION_SAWMILL = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.PRECISION_SAWMILL, MekanismLang.DESCRIPTION_PRECISION_SAWMILL, FactoryType.SAWING)
            .withGui(() -> MekanismContainerTypes.PRECISION_SAWMILL)
            .withSound(MekanismSounds.PRECISION_SAWMILL)
            .withEnergyConfig(MekanismConfig.usage.precisionSawmill, MekanismConfig.storage.precisionSawmill)
            .withComputerSupport("precisionSawmill")
            .build();
    // Osmium Compressor
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityOsmiumCompressor> OSMIUM_COMPRESSOR = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.OSMIUM_COMPRESSOR, MekanismLang.DESCRIPTION_OSMIUM_COMPRESSOR, FactoryType.COMPRESSING)
            .withGui(() -> MekanismContainerTypes.OSMIUM_COMPRESSOR)
            .withSound(MekanismSounds.OSMIUM_COMPRESSOR)
            .withEnergyConfig(MekanismConfig.usage.osmiumCompressor, MekanismConfig.storage.osmiumCompressor)
            .withComputerSupport("osmiumCompressor")
            .build();
    // Combiner
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityCombiner> COMBINER = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.COMBINER, MekanismLang.DESCRIPTION_COMBINER, FactoryType.COMBINING)
            .withGui(() -> MekanismContainerTypes.COMBINER)
            .withSound(MekanismSounds.COMBINER)
            .withEnergyConfig(MekanismConfig.usage.combiner, MekanismConfig.storage.combiner)
            .withComputerSupport("combiner")
            .build();
    // Metallurgic Infuser
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityMetallurgicInfuser> METALLURGIC_INFUSER = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.METALLURGIC_INFUSER, MekanismLang.DESCRIPTION_METALLURGIC_INFUSER, FactoryType.INFUSING)
            .withGui(() -> MekanismContainerTypes.METALLURGIC_INFUSER)
            .withSound(MekanismSounds.METALLURGIC_INFUSER)
            .withEnergyConfig(MekanismConfig.usage.metallurgicInfuser, MekanismConfig.storage.metallurgicInfuser)
            .withCustomShape(BlockShapes.METALLURGIC_INFUSER)
            .withComputerSupport("metallurgicInfuser")
            .build();
    // Purification Chamber
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityPurificationChamber> PURIFICATION_CHAMBER = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.PURIFICATION_CHAMBER, MekanismLang.DESCRIPTION_PURIFICATION_CHAMBER, FactoryType.PURIFYING)
            .withGui(() -> MekanismContainerTypes.PURIFICATION_CHAMBER)
            .withSound(MekanismSounds.PURIFICATION_CHAMBER)
            .withEnergyConfig(MekanismConfig.usage.purificationChamber, MekanismConfig.storage.purificationChamber)
            .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING, Upgrade.GAS))
            .withComputerSupport("purificationChamber")
            .build();
    // Chemical Injection Chamber
    public static final AdvancedMachine.AdvancedFactoryMachine<TileEntityChemicalInjectionChamber> CHEMICAL_INJECTION_CHAMBER = AdvancedMachine.AdvancedMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_INJECTION_CHAMBER, MekanismLang.DESCRIPTION_CHEMICAL_INJECTION_CHAMBER, FactoryType.INJECTING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_INJECTION_CHAMBER)
            .withSound(MekanismSounds.CHEMICAL_INJECTION_CHAMBER)
            .withEnergyConfig(MekanismConfig.usage.chemicalInjectionChamber, MekanismConfig.storage.chemicalInjectionChamber)
            .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING, Upgrade.GAS))
            .withComputerSupport("chemicalInjectionChamber")
            .build();
    // Radioactive Waste Barrel
    public static final BlockTypeTile<ExtraTileEntityRadioactiveWasteBarrel> EXPAND_RADIOACTIVE_WASTE_BARREL = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> ExtraTileEntityTypes.EXPAND_RADIOACTIVE_WASTE_BARREL, MekanismLang.DESCRIPTION_RADIOACTIVE_WASTE_BARREL)
            .with(Attributes.ACTIVE_LIGHT, new AttributeStateFacing(), Attributes.COMPARATOR)
            .withCustomShape(BlockShapes.RADIOACTIVE_WASTE_BARREL)
            .withComputerSupport("radioactiveWasteBarrel")
            .build();
    // Hard Induction Casing
    public static final BlockTypeTile<TileEntityReinforcedInductionCasing> REINFORCED_INDUCTION_CASING = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> ExtraTileEntityTypes.REINFORCED_INDUCTION_CASING, MekanismLang.DESCRIPTION_INDUCTION_CASING)
            .withGui(() -> ExtraContainerTypes.REINFORCED_INDUCTION_MATRIX, MekanismLang.MATRIX)
            .with(Attributes.INVENTORY, Attributes.COMPARATOR)
            .externalMultiblock()
            .build();
    // Hard Induction Port
    public static final BlockTypeTile<TileEntityReinforcedInductionPort> REINFORCED_INDUCTION_PORT = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> ExtraTileEntityTypes.REINFORCED_INDUCTION_PORT, MekanismLang.DESCRIPTION_INDUCTION_PORT)
            .withGui(() -> ExtraContainerTypes.REINFORCED_INDUCTION_MATRIX, MekanismLang.MATRIX)
            .with(Attributes.INVENTORY, Attributes.COMPARATOR, Attributes.ACTIVE)
            .externalMultiblock()
            .withComputerSupport("reinforcedInductionPort")
            .build();
    // Bin
    public static final Machine<ExtraTileEntityBin> ABSOLUTE_BIN = createBin(BTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_BIN, () -> ExtraBlock.SUPREME_BIN);
    public static final Machine<ExtraTileEntityBin> SUPREME_BIN = createBin(BTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_BIN, () -> ExtraBlock.COSMIC_BIN);
    public static final Machine<ExtraTileEntityBin> COSMIC_BIN = createBin(BTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_BIN, () -> ExtraBlock.INFINITE_BIN);
    public static final Machine<ExtraTileEntityBin> INFINITE_BIN = createBin(BTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_BIN, null);
    // Induction Cells
    public static final BlockTypeTile<ExtraTileEntityInductionCell> ABSOLUTE_INDUCTION_CELL = createInductionCell(ICTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_INDUCTION_CELL);
    public static final BlockTypeTile<ExtraTileEntityInductionCell> SUPREME_INDUCTION_CELL = createInductionCell(ICTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_INDUCTION_CELL);
    public static final BlockTypeTile<ExtraTileEntityInductionCell> COSMIC_INDUCTION_CELL = createInductionCell(ICTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_INDUCTION_CELL);
    public static final BlockTypeTile<ExtraTileEntityInductionCell> INFINITE_INDUCTION_CELL = createInductionCell(ICTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_INDUCTION_CELL);
    // Induction Provide
    public static final BlockTypeTile<ExtraTileEntityInductionProvider> ABSOLUTE_INDUCTION_PROVIDER = createInductionProvider(IPTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_INDUCTION_PROVIDER);
    public static final BlockTypeTile<ExtraTileEntityInductionProvider> SUPREME_INDUCTION_PROVIDER = createInductionProvider(IPTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_INDUCTION_PROVIDER);
    public static final BlockTypeTile<ExtraTileEntityInductionProvider> COSMIC_INDUCTION_PROVIDER = createInductionProvider(IPTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_INDUCTION_PROVIDER);
    public static final BlockTypeTile<ExtraTileEntityInductionProvider> INFINITE_INDUCTION_PROVIDER = createInductionProvider(IPTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_INDUCTION_PROVIDER);
    // Fluid Tank
    public static final Machine<ExtraTileEntityFluidTank> ABSOLUTE_FLUID_TANK = createFluidTank(FTTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_FLUID_TANK, () -> ExtraBlock.SUPREME_FLUID_TANK);
    public static final Machine<ExtraTileEntityFluidTank> SUPREME_FLUID_TANK = createFluidTank(FTTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_FLUID_TANK, () -> ExtraBlock.COSMIC_FLUID_TANK);
    public static final Machine<ExtraTileEntityFluidTank> COSMIC_FLUID_TANK = createFluidTank(FTTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_FLUID_TANK, () -> ExtraBlock.INFINITE_FLUID_TANK);
    public static final Machine<ExtraTileEntityFluidTank> INFINITE_FLUID_TANK = createFluidTank(FTTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_FLUID_TANK, null);
    // Energy Cube
    public static final Machine<ExtraTileEntityEnergyCube> ABSOLUTE_ENERGY_CUBE = createEnergyCube(ECTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_ENERGY_CUBE, () -> ExtraBlock.SUPREME_ENERGY_CUBE);
    public static final Machine<ExtraTileEntityEnergyCube> SUPREME_ENERGY_CUBE = createEnergyCube(ECTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_ENERGY_CUBE, () -> ExtraBlock.COSMIC_ENERGY_CUBE);
    public static final Machine<ExtraTileEntityEnergyCube> COSMIC_ENERGY_CUBE = createEnergyCube(ECTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_ENERGY_CUBE, () -> ExtraBlock.INFINITE_ENERGY_CUBE);
    public static final Machine<ExtraTileEntityEnergyCube> INFINITE_ENERGY_CUBE = createEnergyCube(ECTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_ENERGY_CUBE, null);
    // Chemical Tank
    public static final Machine<ExtraTileEntityChemicalTank> ABSOLUTE_CHEMICAL_TANK = createChemicalTank(CTTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_CHEMICAL_TANK, () -> ExtraBlock.SUPREME_CHEMICAL_TANK);
    public static final Machine<ExtraTileEntityChemicalTank> SUPREME_CHEMICAL_TANK = createChemicalTank(CTTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_CHEMICAL_TANK, () -> ExtraBlock.COSMIC_CHEMICAL_TANK);
    public static final Machine<ExtraTileEntityChemicalTank> COSMIC_CHEMICAL_TANK = createChemicalTank(CTTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_CHEMICAL_TANK, () -> ExtraBlock.INFINITE_CHEMICAL_TANK);
    public static final Machine<ExtraTileEntityChemicalTank> INFINITE_CHEMICAL_TANK = createChemicalTank(CTTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_CHEMICAL_TANK, null);
    // Electric Pump
    public static final Machine<TileEntityAdvancedElectricPump> ADVANCED_ELECTRIC_PUMP = Machine.MachineBuilder
            .createMachine(() -> ExtraTileEntityTypes.ADVANCED_ELECTRIC_PUMP, MekanismLang.DESCRIPTION_ELECTRIC_PUMP)
            .withGui(() -> ExtraContainerTypes.ADVANCED_ELECTRIC_PUMP)
            .withEnergyConfig(LoadConfig.extraUsage.advanceElectricPump, LoadConfig.extraStorage.advanceElectricPump)
            .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.FILTER, ExtraUpgrade.IONIC_MEMBRANE))
            .withCustomShape(BlockShapes.ELECTRIC_PUMP)
            .withComputerSupport("advancedElectricPump")
            .replace(Attributes.ACTIVE)
            .build();
    // Tungsten Casing
    public static final BlockType TUNGSTEN_CASING = BlockTypeBuilder
            .createBlock(ExtraLang.DESCRIPTION_TUNGSTEN_CASING)
            .build();
    // Chemical Reactor Casing
    public static final BlockTypeTile<TileEntityChemicalReactorCasing> CHEMICAL_REACTOR_CASING = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> ExtraTileEntityTypes.CHEMICAL_REACTOR_CASING, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_CASING)
            .externalMultiblock()
            .build();
    // Chemical Reactor Controller
    public static final BlockTypeTile<TileEntityChemicalReactorController> CHEMICAL_REACTOR_CONTROLLER = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> ExtraTileEntityTypes.CHEMICAL_REACTOR_CONTROLLER, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_CONTROLLER)
//            .withGui(() -> ExtraContainerTypes.CHEMICAL_REACTOR)
//            .withSound(ExtraSounds.CHEMICAL_REACTION)
            .with(Attributes.INVENTORY, Attributes.ACTIVE, new AttributeStateFacing(), new AttributeCustomResistance(8))
            .externalMultiblock()
            .build();
    // Chemical Reactor Rotor
    public static final BlockTypeTile<TileEntityChemicalReactorRotor> CHEMICAL_REACTOR_ROTOR = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> ExtraTileEntityTypes.CHEMICAL_REACTOR_ROTOR, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_ROTOR)
            .withCustomShape(mekanism.generators.common.content.blocktype.BlockShapes.TURBINE_ROTOR)
            .internalMultiblock()
            .build();
    // Chemical Inert Glass
    public static final BlockTypeTile<TileEntityChemicalInertGlass> CHEMICAL_INERT_GLASS = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> ExtraTileEntityTypes.CHEMICAL_INERT_GLASS, ExtraLang.DESCRIPTION_CHEMICAL_INERT_CLASS)
            .with(AttributeMultiblock.STRUCTURAL, Attributes.AttributeMobSpawn.NEVER)
            .build();
    // Chemical Reactor Item IO Hatches
    public static final BlockTypeTile<TileEntityChemicalReactorItemIOHatch> BASIC_CHEMICAL_REACTOR_ITEM_IO_HATCH = createItemIOHatch(BasicItemIOHatchTier.BASIC, () -> ExtraTileEntityTypes.BASIC_CHEMICAL_REACTOR_ITEM_IO_HATCH, () -> ExtraBlock.ADVANCED_CHEMICAL_REACTOR_ITEM_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorItemIOHatch> ADVANCED_CHEMICAL_REACTOR_ITEM_IO_HATCH = createItemIOHatch(BasicItemIOHatchTier.ADVANCED, () -> ExtraTileEntityTypes.ADVANCED_CHEMICAL_REACTOR_ITEM_IO_HATCH, () -> ExtraBlock.ELITE_CHEMICAL_REACTOR_ITEM_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorItemIOHatch> ELITE_CHEMICAL_REACTOR_ITEM_IO_HATCH = createItemIOHatch(BasicItemIOHatchTier.ELITE, () -> ExtraTileEntityTypes.ELITE_CHEMICAL_REACTOR_ITEM_IO_HATCH, () -> ExtraBlock.ULTIMATE_CHEMICAL_REACTOR_ITEM_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorItemIOHatch> ULTIMATE_CHEMICAL_REACTOR_ITEM_IO_HATCH = createItemIOHatch(BasicItemIOHatchTier.ULTIMATE, () -> ExtraTileEntityTypes.ULTIMATE_CHEMICAL_REACTOR_ITEM_IO_HATCH, () -> ExtraBlock.ABSOLUTE_CHEMICAL_REACTOR_ITEM_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorItemIOHatch> ABSOLUTE_CHEMICAL_REACTOR_ITEM_IO_HATCH = createItemIOHatch(AdvancedItemIOHatchTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_CHEMICAL_REACTOR_ITEM_IO_HATCH, () -> ExtraBlock.SUPREME_CHEMICAL_REACTOR_ITEM_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorItemIOHatch> SUPREME_CHEMICAL_REACTOR_ITEM_IO_HATCH = createItemIOHatch(AdvancedItemIOHatchTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_CHEMICAL_REACTOR_ITEM_IO_HATCH, () -> ExtraBlock.COSMIC_CHEMICAL_REACTOR_ITEM_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorItemIOHatch> COSMIC_CHEMICAL_REACTOR_ITEM_IO_HATCH = createItemIOHatch(AdvancedItemIOHatchTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_CHEMICAL_REACTOR_ITEM_IO_HATCH, () -> ExtraBlock.INFINITE_CHEMICAL_REACTOR_ITEM_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorItemIOHatch> INFINITE_CHEMICAL_REACTOR_ITEM_IO_HATCH = createItemIOHatch(AdvancedItemIOHatchTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_CHEMICAL_REACTOR_ITEM_IO_HATCH, () -> null);
    // Chemical Reactor Fluid IO Hatches
    public static final BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> BASIC_CHEMICAL_REACTOR_FLUID_IO_HATCH = createFluidIOHatch(BasicFluidIOHatchTier.BASIC, () -> ExtraTileEntityTypes.BASIC_CHEMICAL_REACTOR_FLUID_IO_HATCH, () -> ExtraBlock.ADVANCED_CHEMICAL_REACTOR_FLUID_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> ADVANCED_CHEMICAL_REACTOR_FLUID_IO_HATCH = createFluidIOHatch(BasicFluidIOHatchTier.ADVANCED, () -> ExtraTileEntityTypes.ADVANCED_CHEMICAL_REACTOR_FLUID_IO_HATCH, () -> ExtraBlock.ELITE_CHEMICAL_REACTOR_FLUID_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> ELITE_CHEMICAL_REACTOR_FLUID_IO_HATCH = createFluidIOHatch(BasicFluidIOHatchTier.ELITE, () -> ExtraTileEntityTypes.ELITE_CHEMICAL_REACTOR_FLUID_IO_HATCH, () -> ExtraBlock.ULTIMATE_CHEMICAL_REACTOR_FLUID_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> ULTIMATE_CHEMICAL_REACTOR_FLUID_IO_HATCH = createFluidIOHatch(BasicFluidIOHatchTier.ULTIMATE, () -> ExtraTileEntityTypes.ULTIMATE_CHEMICAL_REACTOR_FLUID_IO_HATCH, () -> ExtraBlock.ABSOLUTE_CHEMICAL_REACTOR_FLUID_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> ABSOLUTE_CHEMICAL_REACTOR_FLUID_IO_HATCH = createFluidIOHatch(AdvancedFluidIOHatchTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_CHEMICAL_REACTOR_FLUID_IO_HATCH, () -> ExtraBlock.SUPREME_CHEMICAL_REACTOR_FLUID_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> SUPREME_CHEMICAL_REACTOR_FLUID_IO_HATCH = createFluidIOHatch(AdvancedFluidIOHatchTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_CHEMICAL_REACTOR_FLUID_IO_HATCH, () -> ExtraBlock.COSMIC_CHEMICAL_REACTOR_FLUID_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> COSMIC_CHEMICAL_REACTOR_FLUID_IO_HATCH = createFluidIOHatch(AdvancedFluidIOHatchTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_CHEMICAL_REACTOR_FLUID_IO_HATCH, () -> ExtraBlock.INFINITE_CHEMICAL_REACTOR_FLUID_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> INFINITE_CHEMICAL_REACTOR_FLUID_IO_HATCH = createFluidIOHatch(AdvancedFluidIOHatchTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_CHEMICAL_REACTOR_FLUID_IO_HATCH, () -> null);
    // Chemical Reactor Chemical IO Hatches
    public static final BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> BASIC_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH = createChemicalIOHatch(BasicChemicalIOHatchTier.BASIC, () -> ExtraTileEntityTypes.BASIC_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH, () -> ExtraBlock.ADVANCED_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> ADVANCED_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH = createChemicalIOHatch(BasicChemicalIOHatchTier.ADVANCED, () -> ExtraTileEntityTypes.ADVANCED_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH, () -> ExtraBlock.ELITE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> ELITE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH = createChemicalIOHatch(BasicChemicalIOHatchTier.ELITE, () -> ExtraTileEntityTypes.ELITE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH, () -> ExtraBlock.ULTIMATE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> ULTIMATE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH = createChemicalIOHatch(BasicChemicalIOHatchTier.ULTIMATE, () -> ExtraTileEntityTypes.ULTIMATE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH, () -> ExtraBlock.ABSOLUTE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> ABSOLUTE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH = createChemicalIOHatch(AdvancedChemicalIOHatchTier.ABSOLUTE, () -> ExtraTileEntityTypes.ABSOLUTE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH, () -> ExtraBlock.SUPREME_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> SUPREME_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH = createChemicalIOHatch(AdvancedChemicalIOHatchTier.SUPREME, () -> ExtraTileEntityTypes.SUPREME_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH, () -> ExtraBlock.COSMIC_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> COSMIC_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH = createChemicalIOHatch(AdvancedChemicalIOHatchTier.COSMIC, () -> ExtraTileEntityTypes.COSMIC_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH, () -> ExtraBlock.INFINITE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH);
    public static final BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> INFINITE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH = createChemicalIOHatch(AdvancedChemicalIOHatchTier.INFINITE, () -> ExtraTileEntityTypes.INFINITE_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH, () -> null);

    static {
        for (AdvancedFactoryTier tier : ExtraEnumUtils.ADVANCED_FACTORY_TIERS) {
            for (FactoryType type : EnumUtils.FACTORY_TYPES) {
                if (Addons.EVOLVEDMEKANISM.isLoaded()) {
                    if (type != EMFactoryType.ALLOYING) {
                        FACTORIES.put(tier, type, AdvancedFactory.AdvancedFactoryBuilder.createFactory(() -> ExtraTileEntityTypes.getAdvancedFactoryTile(tier, type), type, tier).build());
                    }
                } else {
                    FACTORIES.put(tier, type, AdvancedFactory.AdvancedFactoryBuilder.createFactory(() -> ExtraTileEntityTypes.getAdvancedFactoryTile(tier, type), type, tier).build());
                }
//                FACTORIES.put(tier, type, AdvancedFactory.AdvancedFactoryBuilder.createFactory(() -> ExtraTileEntityTypes.getAdvancedFactoryTile(tier, type), type, tier).build());
            }
        }
    }

    public static AdvancedFactory<?> getAdvancedFactory(AdvancedFactoryTier tier, FactoryType type) {
        return FACTORIES.get(tier, type);
    }

    private static <TILE extends ExtraTileEntityInductionCell> BlockTypeTile<TILE> createInductionCell(ICTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, MekanismLang.DESCRIPTION_INDUCTION_CELL)
                .withEnergyConfig(tier::getMaxEnergy)
                .with(new AttributeTier<>(tier))
                .internalMultiblock()
                .build();
    }

    private static <TILE extends ExtraTileEntityInductionProvider> BlockTypeTile<TILE> createInductionProvider(IPTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, MekanismLang.DESCRIPTION_INDUCTION_PROVIDER)
                .with(new AttributeTier<>(tier))
                .internalMultiblock()
                .build();
    }

    private static <TILE extends ExtraTileEntityBin> Machine<TILE> createBin(BTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return Machine.MachineBuilder.createMachine(tile, MekanismLang.DESCRIPTION_BIN)
                .with(new ExtraAttributeTier<>(tier), new ExtraAttributeUpgradeable(upgradeBlock))
                .without(AttributeParticleFX.class, Attributes.AttributeSecurity.class, AttributeUpgradeSupport.class, Attributes.AttributeRedstone.class)
                .withComputerSupport(tier.getAdvancedTier().getLowerName() + "Bin")
                .build();
    }

    private static <TILE extends ExtraTileEntityFluidTank> Machine<TILE> createFluidTank(FTTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return Machine.MachineBuilder.createMachine(tile, MekanismLang.DESCRIPTION_FLUID_TANK)
                .withGui(() -> ExtraContainerTypes.FLUID_TANK)
                .withCustomShape(BlockShapes.FLUID_TANK)
                .with(new ExtraAttributeTier<>(tier), new ExtraAttributeUpgradeable(upgradeBlock))
                .without(AttributeParticleFX.class, AttributeStateFacing.class, Attributes.AttributeRedstone.class, AttributeUpgradeSupport.class)
                .withComputerSupport(tier.getAdvancedTier().getLowerName() + "FluidTank")
                .build();
    }

    private static <TILE extends ExtraTileEntityEnergyCube> Machine<TILE> createEnergyCube(ECTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return Machine.MachineBuilder.createMachine(tile, MekanismLang.DESCRIPTION_ENERGY_CUBE)
                .withGui(() -> ExtraContainerTypes.ENERGY_CUBE)
                .withEnergyConfig(new ExtraFloatingLong(tier.getMaxEnergy()))
                .with(new ExtraAttributeTier<>(tier), new ExtraAttributeUpgradeable(upgradeBlock))
                .without(AttributeParticleFX.class, AttributeStateActive.class, AttributeUpgradeSupport.class)
                .withComputerSupport(tier.getAdvancedTier().getLowerName() +  "EnergyCube")
                .build();
    }

    private static <TILE extends ExtraTileEntityChemicalTank> Machine<TILE> createChemicalTank(CTTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return Machine.MachineBuilder.createMachine(tile, MekanismLang.DESCRIPTION_CHEMICAL_TANK)
                .withGui(() -> ExtraContainerTypes.CHEMICAL_TANK)
                .withCustomShape(BlockShapes.CHEMICAL_TANK)
                .with(new ExtraAttributeTier<>(tier), new ExtraAttributeUpgradeable(upgradeBlock))
                .without(AttributeParticleFX.class, AttributeStateActive.class, AttributeUpgradeSupport.class)
                .withComputerSupport(tier.getAdvancedTier().getLowerName() + "ChemicalTank")
                .build();
    }

    private static BlockTypeTile<TileEntityChemicalReactorItemIOHatch> createItemIOHatch(BasicItemIOHatchTier tier, Supplier<TileEntityTypeRegistryObject<TileEntityChemicalReactorItemIOHatch>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_ITEM_IO_HATCH)
//                .withGui(() -> ExtraContainerTypes.ITEM_IO_HATCH)
//                .withSound(ExtraSounds.CHEMICAL_REACTION)
                .with(new AttributeTier<>(tier), new AttributeUpgradeable(upgradeBlock), Attributes.ACTIVE, Attributes.COMPARATOR, Attributes.INVENTORY)
                .externalMultiblock()
                .build();
    }

    private static BlockTypeTile<TileEntityChemicalReactorItemIOHatch> createItemIOHatch(AdvancedItemIOHatchTier tier, Supplier<TileEntityTypeRegistryObject<TileEntityChemicalReactorItemIOHatch>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_ITEM_IO_HATCH)
//                .withGui(() -> ExtraContainerTypes.ITEM_IO_HATCH)
//                .withSound(ExtraSounds.CHEMICAL_REACTION)
                .with(new ExtraAttributeTier<>(tier), new ExtraAttributeUpgradeable(upgradeBlock), Attributes.ACTIVE, Attributes.COMPARATOR, Attributes.INVENTORY)
                .externalMultiblock()
                .build();
    }

    private static BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> createFluidIOHatch(BasicFluidIOHatchTier tier, Supplier<TileEntityTypeRegistryObject<TileEntityChemicalReactorFluidIOHatch>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_FLUID_IO_HATCH)
//                .withGui(() -> ExtraContainerTypes.FLUID_IO_HATCH)
//                .withSound(ExtraSounds.CHEMICAL_REACTION)
                .with(new AttributeTier<>(tier), new AttributeUpgradeable(upgradeBlock), Attributes.ACTIVE, Attributes.COMPARATOR, Attributes.INVENTORY)
                .externalMultiblock()
                .build();
    }

    private static BlockTypeTile<TileEntityChemicalReactorFluidIOHatch> createFluidIOHatch(AdvancedFluidIOHatchTier tier, Supplier<TileEntityTypeRegistryObject<TileEntityChemicalReactorFluidIOHatch>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_FLUID_IO_HATCH)
//                .withGui(() -> ExtraContainerTypes.FLUID_IO_HATCH)
//                .withSound(ExtraSounds.CHEMICAL_REACTION)
                .with(new ExtraAttributeTier<>(tier), new ExtraAttributeUpgradeable(upgradeBlock), Attributes.ACTIVE, Attributes.COMPARATOR, Attributes.INVENTORY)
                .externalMultiblock()
                .build();
    }

    private static BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> createChemicalIOHatch(BasicChemicalIOHatchTier tier, Supplier<TileEntityTypeRegistryObject<TileEntityChemicalReactorChemicalIOHatch>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH)
//                .withGui(() -> ExtraContainerTypes.CHEMICAL_IO_HATCH)
//                .withSound(ExtraSounds.CHEMICAL_REACTION)
                .with(new AttributeTier<>(tier), new AttributeUpgradeable(upgradeBlock), Attributes.ACTIVE, Attributes.COMPARATOR, Attributes.INVENTORY)
                .externalMultiblock()
                .build();
    }

    private static BlockTypeTile<TileEntityChemicalReactorChemicalIOHatch> createChemicalIOHatch(AdvancedChemicalIOHatchTier tier, Supplier<TileEntityTypeRegistryObject<TileEntityChemicalReactorChemicalIOHatch>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, ExtraLang.DESCRIPTION_CHEMICAL_REACTOR_CHEMICAL_IO_HATCH)
//                .withGui(() -> ExtraContainerTypes.CHEMICAL_IO_HATCH)
//                .withSound(ExtraSounds.CHEMICAL_REACTION)
                .with(new ExtraAttributeTier<>(tier), new ExtraAttributeUpgradeable(upgradeBlock), Attributes.ACTIVE, Attributes.COMPARATOR, Attributes.INVENTORY)
                .externalMultiblock()
                .build();
    }
}
