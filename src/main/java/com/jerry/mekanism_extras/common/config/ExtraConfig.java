package com.jerry.mekanism_extras.common.config;

import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.common.tier.BTier;
import com.jerry.mekanism_extras.common.tier.CTTier;
import com.jerry.mekanism_extras.common.tier.ECTier;
import com.jerry.mekanism_extras.common.tier.FTTier;
import com.jerry.mekanism_extras.common.tier.ICTier;
import com.jerry.mekanism_extras.common.tier.IPTier;
import com.jerry.mekanism_extras.common.util.ExtraEnumUtils;
import mekanism.api.heat.HeatAPI;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Locale;

public class ExtraConfig extends BaseMekanismConfig {
    private final ForgeConfigSpec configSpec;
    public final CachedBooleanValue transmitterAlloyUpgrade;
    public final CachedFloatingLongValue absoluteUniversalCableCapacity;
    public final CachedFloatingLongValue supremeUniversalCableCapacity;
    public final CachedFloatingLongValue cosmicUniversalCableCapacity;
    public final CachedFloatingLongValue infiniteUniversalCableCapacity;

    public final CachedFloatingLongValue absoluteMechanicalPipeCapacity;
    public final CachedFloatingLongValue absoluteMechanicalPipePullAmount;
    public final CachedFloatingLongValue supremeMechanicalPipeCapacity;
    public final CachedFloatingLongValue supremeMechanicalPipePullAmount;
    public final CachedFloatingLongValue cosmicMechanicalPipeCapacity;
    public final CachedFloatingLongValue cosmicMechanicalPipePullAmount;
    public final CachedFloatingLongValue infiniteMechanicalPipeCapacity;
    public final CachedFloatingLongValue infiniteMechanicalPipePullAmount;

    public final CachedFloatingLongValue absolutePressurizedTubeCapacity;
    public final CachedFloatingLongValue absolutePressurizedTubePullAmount;
    public final CachedFloatingLongValue supremePressurizedTubeCapacity;
    public final CachedFloatingLongValue supremePressurizedTubePullAmount;
    public final CachedFloatingLongValue cosmicPressurizedTubeCapacity;
    public final CachedFloatingLongValue cosmicPressurizedTubePullAmount;
    public final CachedFloatingLongValue infinitePressurizedTubeCapacity;
    public final CachedFloatingLongValue infinitePressurizedTubePullAmount;

    public final CachedFloatingLongValue absoluteLogisticalTransporterSpeed;
    public final CachedFloatingLongValue absoluteLogisticalTransporterPullAmount;
    public final CachedFloatingLongValue supremeLogisticalTransporterSpeed;
    public final CachedFloatingLongValue supremeLogisticalTransporterPullAmount;
    public final CachedFloatingLongValue cosmicLogisticalTransporterSpeed;
    public final CachedFloatingLongValue cosmicLogisticalTransporterPullAmount;
    public final CachedFloatingLongValue infiniteLogisticalTransporterSpeed;
    public final CachedFloatingLongValue infiniteLogisticalTransporterPullAmount;

    public final CachedFloatingLongValue absoluteThermodynamicConductorConduction;
    public final CachedFloatingLongValue absoluteThermodynamicConductornCapacity;
    public final CachedFloatingLongValue absoluteThermodynamicConductornInsulation;
    public final CachedFloatingLongValue supremeThermodynamicConductorConduction;
    public final CachedFloatingLongValue supremeThermodynamicConductornCapacity;
    public final CachedFloatingLongValue supremeThermodynamicConductornInsulation;
    public final CachedFloatingLongValue cosmicThermodynamicConductorConduction;
    public final CachedFloatingLongValue cosmicThermodynamicConductornCapacity;
    public final CachedFloatingLongValue cosmicThermodynamicConductornInsulation;
    public final CachedFloatingLongValue infiniteThermodynamicConductorConduction;
    public final CachedFloatingLongValue infiniteThermodynamicConductornCapacity;
    public final CachedFloatingLongValue infiniteThermodynamicConductornInsulation;
    //Radiation
    public final CachedLongValue radioactiveWasteBarrelMaxGas;
    public final CachedIntValue radioactiveWasteBarrelProcessTicks;
    public final CachedLongValue radioactiveWasteBarrelDecayAmount;
    //Pump
    public final CachedIntValue pumpHeavyWaterAmount;
    // Force Field Generator
//    public final CachedFloatingLongValue forcefieldGenerator;

    public ExtraConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Config").push(MekanismExtras.MODID);

        final String noteUC = "Internal buffer in Joules of each 'TIER' universal cable.(long)";
        builder.comment("Universal Cables").push("universal cables");
        this.absoluteUniversalCableCapacity = CachedFloatingLongValue.define(this, builder, noteUC, "absoluteUniversalCable", FloatingLong.createConst(65536000L));
        this.supremeUniversalCableCapacity = CachedFloatingLongValue.define(this, builder, noteUC, "supremeUniversalCable", FloatingLong.createConst(524288000L));
        this.cosmicUniversalCableCapacity = CachedFloatingLongValue.define(this, builder, noteUC, "cosmicUniversalCable", FloatingLong.createConst(4194304000L));
        this.infiniteUniversalCableCapacity = CachedFloatingLongValue.define(this, builder, noteUC, "infiniteUniversalCable", FloatingLong.createConst(33554432000L));
        builder.pop();

        // PullAmount实际为int类型参数
        final String noteMP = "Capacity of 'TIER' mechanical pipes in mB.(long)";
        final String noteMP2 = "Pump rate of 'TIER' mechanical pipes in mB/t.(int)";
        builder.comment("Mechanical Pipes").push("mechanical pipes");
        this.absoluteMechanicalPipeCapacity = CachedFloatingLongValue.define(this, builder, noteMP, "absoluteMechanicalPipesCapacity", FloatingLong.createConst(1_024_000));// x8
        this.absoluteMechanicalPipePullAmount = CachedFloatingLongValue.define(this, builder, noteMP2, "absoluteMechanicalPipesPullAmount", FloatingLong.createConst(256_000));// x8
        this.supremeMechanicalPipeCapacity = CachedFloatingLongValue.define(this, builder, noteMP, "supremeMechanicalPipesCapacity", FloatingLong.createConst(8_192_000));// x8
        this.supremeMechanicalPipePullAmount = CachedFloatingLongValue.define(this, builder, noteMP2, "supremeMechanicalPipesPullAmount", FloatingLong.createConst(2_048_000));// x8
        this.cosmicMechanicalPipeCapacity = CachedFloatingLongValue.define(this, builder, noteMP, "cosmicMechanicalPipesCapacity", FloatingLong.createConst(65_536_000));// x8
        this.cosmicMechanicalPipePullAmount = CachedFloatingLongValue.define(this, builder, noteMP2, "cosmicMechanicalPipesPullAmount", FloatingLong.createConst(16_384_000));// x8
        this.infiniteMechanicalPipeCapacity = CachedFloatingLongValue.define(this, builder, noteMP, "infiniteMechanicalPipesCapacity", FloatingLong.createConst(524_288_000));// x8
        this.infiniteMechanicalPipePullAmount = CachedFloatingLongValue.define(this, builder, noteMP2, "infiniteMechanicalPipesPullAmount", FloatingLong.createConst(131_072_000));// x8
        builder.pop();

        final String notePT = "Capacity of 'TIER' pressurized tubes in mB.(long)";
        final String notePT2 = "Pump rate of 'TIER' pressurized tubes in mB/t.(long)";
        builder.comment("Pressurized Tubes").push("pressurized tubes");
        this.absolutePressurizedTubeCapacity = CachedFloatingLongValue.define(this, builder, notePT, "absolutePressurizedTubesCapacity", FloatingLong.createConst(8_192_000));// x8
        this.absolutePressurizedTubePullAmount = CachedFloatingLongValue.define(this, builder, notePT2, "absolutePressurizedTubesPullAmount", FloatingLong.createConst(2_048_000));// x8
        this.supremePressurizedTubeCapacity = CachedFloatingLongValue.define(this, builder, notePT, "supremePressurizedTubesCapacity", FloatingLong.createConst(65_536_000));// x8
        this.supremePressurizedTubePullAmount = CachedFloatingLongValue.define(this, builder, notePT2, "supremePressurizedTubesPullAmount", FloatingLong.createConst(16_384_000));// x8
        this.cosmicPressurizedTubeCapacity = CachedFloatingLongValue.define(this, builder, notePT, "cosmicPressurizedTubesCapacity", FloatingLong.createConst(524_288_000));// x8
        this.cosmicPressurizedTubePullAmount = CachedFloatingLongValue.define(this, builder, notePT2, "cosmicPressurizedTubesPullAmount", FloatingLong.createConst(131_072_000));// x8
        this.infinitePressurizedTubeCapacity = CachedFloatingLongValue.define(this, builder, notePT, "infinitePressurizedTubesCapacity", FloatingLong.createConst(4_194_304_000L));// x8
        this.infinitePressurizedTubePullAmount = CachedFloatingLongValue.define(this, builder, notePT2, "infinitePressurizedTubesPullAmount", FloatingLong.createConst(1_048_576_000));// x8
        builder.pop();

        final String noteLT = "Five times the travel speed in m/s of 'TIER' logistical transporter.(int)";
        final String noteLT2 = "Item throughput rate of 'TIER' logistical transporters in items/half second.(int)";
        builder.comment("Logistical Transporters").push("logistical transporters");
        this.absoluteLogisticalTransporterSpeed = CachedFloatingLongValue.define(this, builder, noteLT, "absoluteLogisticalTransporterSpeed", FloatingLong.createConst(55));
        this.absoluteLogisticalTransporterPullAmount = CachedFloatingLongValue.define(this, builder, noteLT2, "absoluteLogisticalTransporterPullAmount", FloatingLong.createConst(128));
        this.supremeLogisticalTransporterSpeed = CachedFloatingLongValue.define(this, builder, noteLT, "supremeLogisticalTransporterSpeed", FloatingLong.createConst(60));
        this.supremeLogisticalTransporterPullAmount = CachedFloatingLongValue.define(this, builder, noteLT2, "supremeLogisticalTransporterPullAmount", FloatingLong.createConst(256));
        this.cosmicLogisticalTransporterSpeed = CachedFloatingLongValue.define(this, builder, noteLT, "cosmicLogisticalTransporterSpeed", FloatingLong.createConst(70));
        this.cosmicLogisticalTransporterPullAmount = CachedFloatingLongValue.define(this, builder, noteLT2, "cosmicLogisticalTransporterPullAmount", FloatingLong.createConst(512));
        this.infiniteLogisticalTransporterSpeed = CachedFloatingLongValue.define(this, builder, noteLT, "infiniteLogisticalTransporterSpeed", FloatingLong.createConst(100));
        this.infiniteLogisticalTransporterPullAmount = CachedFloatingLongValue.define(this, builder, noteLT2, "infiniteLogisticalTransporterPullAmount", FloatingLong.createConst(1024));
        builder.pop();

        final String noteTC = "Conduction value of 'TIER' thermodynamic conductors.(long)";//热导
        final String noteTC2 = "Heat capacity of 'TIER' thermodynamic conductors.(long)";//热容
        final String noteTC3 = "Insulation value of 'TIER' thermodynamic conductor(long).";//热阻
        builder.comment("Thermodynamic Conductors").push("thermodynamic conductors");
        this.absoluteThermodynamicConductorConduction = CachedFloatingLongValue.define(this, builder, noteTC, "absoluteThermodynamicConductorConduction", FloatingLong.createConst(10L));
        this.absoluteThermodynamicConductornCapacity = CachedFloatingLongValue.define(this, builder, noteTC2, "absoluteThermodynamicConductornCapacity", FloatingLong.createConst(HeatAPI.DEFAULT_HEAT_CAPACITY));
        this.absoluteThermodynamicConductornInsulation = CachedFloatingLongValue.define(this, builder, noteTC3, "absoluteThermodynamicConductornInsulation", FloatingLong.createConst(400000L));
        this.supremeThermodynamicConductorConduction = CachedFloatingLongValue.define(this, builder, noteTC, "supremeThermodynamicConductorConduction", FloatingLong.createConst(15L));
        this.supremeThermodynamicConductornCapacity = CachedFloatingLongValue.define(this, builder, noteTC2, "supremeThermodynamicConductornCapacity", FloatingLong.createConst(HeatAPI.DEFAULT_HEAT_CAPACITY));
        this.supremeThermodynamicConductornInsulation = CachedFloatingLongValue.define(this, builder, noteTC3, "supremeThermodynamicConductornInsulation", FloatingLong.createConst(800000L));
        this.cosmicThermodynamicConductorConduction = CachedFloatingLongValue.define(this, builder, noteTC, "cosmicThermodynamicConductorConduction", FloatingLong.createConst(20L));
        this.cosmicThermodynamicConductornCapacity = CachedFloatingLongValue.define(this, builder, noteTC2, "cosmicThermodynamicConductornCapacity", FloatingLong.createConst(HeatAPI.DEFAULT_HEAT_CAPACITY));
        this.cosmicThermodynamicConductornInsulation = CachedFloatingLongValue.define(this, builder, noteTC3, "cosmicThermodynamicConductornInsulation", FloatingLong.createConst(1000000L));
        this.infiniteThermodynamicConductorConduction = CachedFloatingLongValue.define(this, builder, noteTC, "infiniteThermodynamicConductorConduction", FloatingLong.createConst(25L));
        this.infiniteThermodynamicConductornCapacity = CachedFloatingLongValue.define(this, builder, noteTC2, "infiniteThermodynamicConductornCapacity", FloatingLong.createConst(HeatAPI.DEFAULT_HEAT_CAPACITY));
        this.infiniteThermodynamicConductornInsulation = CachedFloatingLongValue.define(this, builder, noteTC3, "infiniteThermodynamicConductornInsulation", FloatingLong.createConst(4000000L));
        builder.pop();

        builder.comment("Expand Radioactive Waste Barrel").push("expand radioactive waste barrel");
        this.radioactiveWasteBarrelMaxGas = CachedLongValue.wrap(this, builder.comment("Amount of gas (mB) that can be stored in a Radioactive Waste Barrel.")
                .defineInRange("radioactiveWasteBarrelMaxGas", 2_048_000, 1, Long.MAX_VALUE));
        this.radioactiveWasteBarrelProcessTicks = CachedIntValue.wrap(this, builder.comment("Number of ticks required for radioactive gas stored in a Radioactive Waste Barrel to decay radioactiveWasteBarrelDecayAmount mB.")
                .defineInRange("radioactiveWasteBarrelProcessTicks", 5, 1, Integer.MAX_VALUE));
        this.radioactiveWasteBarrelDecayAmount = CachedLongValue.wrap(this, builder.comment("Number of mB of gas that decay every radioactiveWasteBarrelProcessTicks ticks when stored in a Radioactive Waste Barrel. Set to zero to disable decay all together. (Gases in the mekanism:waste_barrel_decay_blacklist tag will not decay).")
                .defineInRange("radioactiveWasteBarrelDecayAmount", 4, 0, Long.MAX_VALUE));
        builder.pop();

        builder.comment("Alloy Upgrade").push("alloy upgrade");
        this.transmitterAlloyUpgrade = CachedBooleanValue.wrap(this, builder.comment("Allow right clicking on Cables/Pipes/Tubes with alloys to upgrade the tier.")
                .define("transmitterAlloyUpgrade", true));
        builder.pop();

        builder.comment("Advance Electric Pump").push("advance electric pump");
        this.pumpHeavyWaterAmount = CachedIntValue.wrap(this, builder.comment("mB of Heavy Water that is extracted per block of Water by the Electric Pump with a Filter Upgrade.")
                .defineInRange("pumpHeavyWaterAmount", FluidType.BUCKET_VOLUME , 1, FluidType.BUCKET_VOLUME));
        builder.pop();

        addEnergyCubeCategory(builder);
        addFluidTankCategory(builder);
        addGasTankCategory(builder);
        addBinCategory(builder);
        addInductionCategory(builder);
        builder.pop();

        this.configSpec = builder.build();
    }

    private void addEnergyCubeCategory(ForgeConfigSpec.Builder builder) {
        builder.comment("Energy Cubes").push("energy cube");
        for (ECTier tier : ExtraEnumUtils.ENERGY_CUBE_TIERS) {
            String tierName = tier.getAdvanceTier().getSimpleName();
            CachedFloatingLongValue storageReference = CachedFloatingLongValue.define(this, builder,
                    "Maximum number of Joules " + tierName + " energy cubes can store.", tier.toString().toLowerCase() + "Storage", tier.getAdvanceMaxEnergy(),
                    CachedFloatingLongValue.POSITIVE);
            CachedFloatingLongValue outputReference = CachedFloatingLongValue.define(this, builder,
                    "Output rate in Joules of " + tierName + " energy cubes.", tier.toString().toLowerCase() + "Output", tier.getAdvanceOutput(),
                    CachedFloatingLongValue.POSITIVE);
            tier.setConfigReference(storageReference, outputReference);
        }
        builder.pop();
    }

    private void addFluidTankCategory(ForgeConfigSpec.Builder builder) {
        builder.comment("Fluid Tanks").push("fluid tanks");
        for (FTTier tier : ExtraEnumUtils.FLUID_TANK_TIERS) {
            String tierName = tier.getAdvanceTier().getSimpleName();
            CachedIntValue storageReference = CachedIntValue.wrap(this, builder.comment("Storage size of " + tier.toString().toLowerCase() + " fluid tanks in mB.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Storage", tier.getAdvanceStorage(), 1, Integer.MAX_VALUE));
            CachedIntValue outputReference = CachedIntValue.wrap(this, builder.comment("Output rate of " + tier.toString().toLowerCase() + " fluid tanks in mB.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Output", tier.getAdvanceOutput(), 1, Integer.MAX_VALUE));
            tier.setConfigReference(storageReference, outputReference);
        }
        builder.pop();
    }

    private void addGasTankCategory(ForgeConfigSpec.Builder builder) {
        builder.comment("Chemical Tanks").push("chemical tanks");
        for (CTTier tier : ExtraEnumUtils.CHEMICAL_TANK_TIERS) {
            String tierName = tier.getAdvanceTier().getSimpleName();
            CachedLongValue storageReference = CachedLongValue.wrap(this, builder.comment("Storage size of " + tier.toString().toLowerCase() + " chemical tanks in mB.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Storage", tier.getAdvanceStorage(), 1, Long.MAX_VALUE));
            CachedLongValue outputReference = CachedLongValue.wrap(this, builder.comment("Output rate of " + tier.toString().toLowerCase() + " chemical tanks in mB.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Output", tier.getAdvanceOutput(), 1, Long.MAX_VALUE));
            tier.setConfigReference(storageReference, outputReference);
        }
        builder.pop();
    }

    private void addBinCategory(ForgeConfigSpec.Builder builder) {
        builder.comment("Bins").push("bins");
        for (BTier tier : ExtraEnumUtils.BIN_TIERS) {
            String tierName = tier.getAdvanceTier().getSimpleName();
            CachedIntValue storageReference = CachedIntValue.wrap(this, builder.comment("The number of items " + tier.toString().toLowerCase() + " bins can store.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Storage", tier.getAdvanceStorage(), 1, Integer.MAX_VALUE));
            tier.setConfigReference(storageReference);
        }
        builder.pop();
    }

    private void addInductionCategory(ForgeConfigSpec.Builder builder) {
        builder.comment("Induction").push("induction");
        for (ICTier tier : ExtraEnumUtils.INDUCTION_CELL_TIERS) {
            String tierName = tier.getBaseTier().getSimpleName();
            CachedFloatingLongValue storageReference = CachedFloatingLongValue.define(this, builder, "Maximum number of Joules " + tier.toString().toLowerCase() + " induction cells can store.",
                    tierName.toLowerCase(Locale.ROOT) + "Storage", tier.getBaseMaxEnergy(), CachedFloatingLongValue.POSITIVE);
            tier.setConfigReference(storageReference);
        }
        for (IPTier tier : ExtraEnumUtils.INDUCTION_PROVIDER_TIERS) {
            String tierName = tier.getBaseTier().getSimpleName();
            CachedFloatingLongValue outputReference = CachedFloatingLongValue.define(this, builder, "Maximum number of Joules " + tier.toString().toLowerCase() + " induction providers can output or accept.",
                    tierName.toLowerCase(Locale.ROOT) + "Output", tier.getBaseOutput(), CachedFloatingLongValue.POSITIVE);
            tier.setConfigReference(outputReference);
        }
        builder.pop();
    }

    public String getFileName() {
        return MekanismExtras.MODID;
    }

    public ForgeConfigSpec getConfigSpec() {
        return this.configSpec;
    }

    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }
}
