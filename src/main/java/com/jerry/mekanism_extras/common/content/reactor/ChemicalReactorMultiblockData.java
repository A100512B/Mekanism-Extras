package com.jerry.mekanism_extras.common.content.reactor;

import com.jerry.mekanism_extras.MekanismExtras;
import com.jerry.mekanism_extras.api.ExtraNBTConstants;
import com.jerry.mekanism_extras.common.ExtraLang;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe.ChemicalReactionRecipeInput;
import com.jerry.mekanism_extras.common.recipes.ChemicalReactionRecipe.ChemicalReactionRecipeJEIOutput;
import com.jerry.mekanism_extras.common.recipes.lookup.ChemicalReactionRecipeLookup;
import com.jerry.mekanism_extras.common.registries.ExtraRecipeTypes;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorCasing;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorChemicalIOHatch;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorFluidIOHatch;
import com.jerry.mekanism_extras.common.tile.multiblock.reactor.TileEntityChemicalReactorItemIOHatch;
import com.jerry.mekanism_extras.common.util.ExtraTransferUtils;
import it.unimi.dsi.fastutil.objects.*;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.Coord4D;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.MathUtils;
import mekanism.api.radiation.IRadiationManager;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.ILangEntry;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.lib.inventory.HashedItem;
import mekanism.common.lib.multiblock.IValveHandler;
import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class ChemicalReactorMultiblockData
        extends MultiblockData
        implements IValveHandler {  // Too complex! We shouldn't implement IRecipeLookupHandler

    // Basic attributes
    @ContainerSync
    public int maxParallel;
    @ContainerSync
    public double speedMultiplier;
    @ContainerSync
    public int circuitType;
    @ContainerSync
    public boolean running;
    @ContainerSync
    public EnumSet<ReactionCondition> supportedConditions;
    @ContainerSync
    public List<BlockPos> inputItemHatches;
    @ContainerSync
    public List<BlockPos> inputFluidHatches;
    @ContainerSync
    public List<BlockPos> inputChemicalHatches;
    @ContainerSync
    public List<BlockPos> outputItemHatches;
    @ContainerSync
    public List<BlockPos> outputFluidHatches;
    @ContainerSync
    public List<BlockPos> outputChemicalHatches;
    @ContainerSync
    public IEnergyContainer energyContainer;
    // States and some constants
    public WorkingError error = WorkingError.WELL;
    public boolean ignoreRisk = false;
    public long progress = 0;
    boolean recipeQueried;
    boolean shouldRequeryRecipe;
    @Nullable
    @ContainerSync
    public ChemicalReactionRecipe queriedRecipe;
    private double reactorDamage = 0;
    private int currentParallel = 0;
    private static final double MAX_DAMAGE = 100.0;
    private static final double DAMAGE_RATE_PER_PARALLEL = 0.001;
    private static final double EXPLOSION_CHANCE = 1D / 64_000;
    private static final float EXPLOSION_RADIUS_BASE = 8.0F;
    // TPS-friendly things
    private final ChemicalReactionRecipeLookup recipeLookup = ChemicalReactionRecipeLookup.getInstanceForWorld(getWorld());
    // Item may have NBT differences, so use HashedItem instead of Item
    public final InputItemIndexManager itemIndex = new InputItemIndexManager(this, getWorld());
    // Fluid stacks may also have NBT differences, but ignore since it's really a rare case
    public final FluidIndexManager fluidIndex = new FluidIndexManager(this, getWorld());
    public final InputChemicalIndexManager chemicalIndex = new InputChemicalIndexManager(this, getWorld());
    @ContainerSync
    public boolean recipeLocked;
    @Nullable
    @ContainerSync
    public ChemicalReactionRecipe lockedRecipe;
    @Nullable
    private ChemicalReactionRecipeInput inputToConsume;
    private final Map<ResourceLocation, ChemicalReactionRecipe> idToRecipeMap = new HashMap<>();

    // Common routines
    public ChemicalReactorMultiblockData(TileEntityChemicalReactorCasing tile) {
        super(tile);
        energyContainers.add(energyContainer = VariableCapacityEnergyContainer.create(
                this::getMaxEnergy,
                automationType -> automationType == AutomationType.INTERNAL && isFormed(),
                automationType -> isFormed(),
                this
        ));
        getWorld().getRecipeManager().getAllRecipesFor(ExtraRecipeTypes.CHEMICAL_REACTION.getRecipeType())
                .forEach(recipe -> idToRecipeMap.put(recipe.getId(), recipe));
    }

    public FloatingLong getMaxEnergy() {
        return FloatingLong.ZERO;
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        initIndex();
        recipeQueried = false;
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);

        ChemicalReactionRecipeInput input = new ChemicalReactionRecipeInput(
                itemIndex.getInputItems(),
                fluidIndex.getInputFluids(),
                chemicalIndex.getInputGases(),
                chemicalIndex.getInputInfusions(),
                chemicalIndex.getInputPigments(),
                chemicalIndex.getInputSlurries()
        );

        if (progress > 0) {
            progress--;
            handleDamage();

            if (progress == 0 && queriedRecipe != null) {
                ChemicalReactionRecipeJEIOutput output = queriedRecipe.getOutput(
                        Set.of(), Set.of(), Set.of(), Set.of(), Set.of(), Set.of()
                );
                List<MergedChemicalTank> allMergedTanks = outputChemicalHatches.stream()
                        .map(pos -> WorldUtils.getTileEntity(TileEntityChemicalReactorChemicalIOHatch.class, getWorld(), pos, true))
                        .filter(Objects::nonNull)
                        .flatMap(hatch -> hatch.getCachedChemicalTanks().stream())
                        .toList();

                if (simulateOutputItems(output.outputItems()) &&
                        simulateOutputFluids(output.outputFluids()) &&
                        ExtraTransferUtils.simulateChemicals(allMergedTanks,
                                output.outputGases(),
                                output.outputInfusions(),
                                output.outputPigments(),
                                output.outputSlurries())) {

                    queriedRecipe = null;
                    inputToConsume = null;
                    shouldRequeryRecipe = true;
                    error = WorkingError.WELL;
                } else {
                    error = WorkingError.OUTPUT_FULL;
                    progress = 1; // Save progress. We'll try to output in the next tick.
                }
            }
            return needsPacket;
        }

        if (shouldRequeryRecipe || queriedRecipe == null) {
            recheckRecipe();
            shouldRequeryRecipe = false;
        }
        if (queriedRecipe == null || inputToConsume == null || inputToConsume.invalid()) {
            error = WorkingError.INSUFFICIENT_INPUT;
            return needsPacket;
        }

        long maxPossibleParallel = calculateMaxParallel();
        long targetParallel = Math.min(maxParallel, maxPossibleParallel);
        if (targetParallel == 0) {
            error = WorkingError.INSUFFICIENT_INPUT;
            return needsPacket;
        } else if (targetParallel < maxParallel) {
            error = WorkingError.INSUFFICIENT_INPUT_FOR_MAX_PARALLEL;
        } else {
            error = WorkingError.WELL;
        }

        if (queriedRecipe != null && !supportedConditions.containsAll(queriedRecipe.getConditions())) {
            if (!ignoreRisk) {
                error = WorkingError.CONDITION_NOT_MATCHED;
                return needsPacket;
            }
        }

        if (queriedRecipe != null && !supportedConditions.containsAll(queriedRecipe.getConditions())) {
            Set<ReactionCondition> missing = EnumSet.copyOf(queriedRecipe.getConditions());
            missing.removeAll(supportedConditions);
            if (missing.contains(ReactionCondition.SPACE_STABILITY)) {
                meltdownHappened(world);
                return needsPacket;
            }
        }

        FloatingLong energyPerOperation = queriedRecipe.getEnergyRequired();
        FloatingLong availableEnergy = energyContainer.getEnergy();
        long energyParallel = availableEnergy.divide(energyPerOperation).longValue();
        long actualParallel = Math.min(maxParallel, Math.min(maxPossibleParallel, energyParallel));

        if (actualParallel == 0) {
            if (maxPossibleParallel == 0) {
                error = WorkingError.INSUFFICIENT_INPUT;
            } else {
                error = WorkingError.INSUFFICIENT_ENERGY;
            }
            return needsPacket;
        }
        if (actualParallel < maxParallel) {
            if (actualParallel == maxPossibleParallel) {
                error = WorkingError.INSUFFICIENT_INPUT_FOR_MAX_PARALLEL;
            }
            else if (actualParallel == energyParallel) {
                error = WorkingError.INSUFFICIENT_ENERGY_FOR_MAX_PARALLEL;
            }
            else {
                if (maxPossibleParallel < energyParallel) {
                    error = WorkingError.INSUFFICIENT_INPUT_FOR_MAX_PARALLEL;
                } else {
                    error = WorkingError.INSUFFICIENT_ENERGY_FOR_MAX_PARALLEL;
                }
            }
        } else {
            error = WorkingError.WELL;
        }

        input.itemStacks().forEach(stack -> itemIndex.consume(stack, stack.getCount() * actualParallel));
        input.fluidStacks().forEach(stack -> fluidIndex.consume(stack, stack.getAmount() * actualParallel));
        input.gasStacks().forEach(stack -> chemicalIndex.consume(stack, stack.getAmount() * actualParallel));
        input.infusionStacks().forEach(stack -> chemicalIndex.consume(stack, stack.getAmount() * actualParallel));
        input.pigmentStacks().forEach(stack -> chemicalIndex.consume(stack, stack.getAmount() * actualParallel));
        input.slurryStacks().forEach(stack -> chemicalIndex.consume(stack, stack.getAmount() * actualParallel));
        FloatingLong energyRequired = energyPerOperation.multiply(targetParallel);
        energyContainer.extract(energyRequired, Action.EXECUTE, AutomationType.INTERNAL);
        progress = calculateProgress();
        running = true;
        markDirty();

        return needsPacket;
    }

    private long calculateMaxParallel() {
        if (inputToConsume == null || inputToConsume.invalid()) return 0;

        long maxByItems = inputToConsume.itemStacks().stream()
                .map(stack -> itemIndex.getCount(HashedItem.create(stack)) / stack.getCount())
                .reduce(0L, Math::min);
        long maxByFluids = inputToConsume.fluidStacks().stream()
                .map(stack -> fluidIndex.getCount(stack.getFluid()) / stack.getAmount())
                .reduce(0L, Math::min);
        long maxByGases = inputToConsume.gasStacks().stream()
                .map(stack -> chemicalIndex.getCount(stack.getType()) / stack.getAmount())
                .reduce(0L, Math::min);
        long maxByInfusions = inputToConsume.infusionStacks().stream()
                .map(stack -> chemicalIndex.getCount(stack.getType()) / stack.getAmount())
                .reduce(0L, Math::min);
        long maxByPigments = inputToConsume.pigmentStacks().stream()
                .map(stack -> chemicalIndex.getCount(stack.getType()) / stack.getAmount())
                .reduce(0L, Math::min);
        long maxBySlurries = inputToConsume.slurryStacks().stream()
                .map(stack -> chemicalIndex.getCount(stack.getType()) / stack.getAmount())
                .reduce(0L, Math::min);
        // Hope we can have a min method for a variable count of parameters...
        return Math.min(Math.min(Math.min(maxByItems, maxByFluids),
                Math.min(maxByGases, maxByInfusions)),
                Math.min(maxByPigments, maxBySlurries));
    }

    private boolean simulateOutputItems(Set<ItemStack> outputs) {
        if (outputs.isEmpty()) return true;
        List<IInventorySlot> slots = outputItemHatches.stream().map(pos -> WorldUtils.getTileEntity(TileEntityChemicalReactorItemIOHatch.class, getWorld(), pos, true))
                .filter(Objects::nonNull)
                .flatMap(tile -> tile.getInventorySlots(null).stream())
                .toList();
        return !slots.isEmpty() && ExtraTransferUtils.simulateItems(slots, outputs);
    }

    private boolean simulateOutputFluids(Set<FluidStack> outputs) {
        if (outputs.isEmpty()) return true;
        List<IExtendedFluidTank> tanks = outputFluidHatches.stream().map(pos -> WorldUtils.getTileEntity(TileEntityChemicalReactorFluidIOHatch.class, getWorld(), pos, true))
                .filter(Objects::nonNull)
                .flatMap(tile -> tile.getFluidTanks(null).stream())
                .toList();
        return !tanks.isEmpty() && ExtraTransferUtils.simulateFluids(tanks, outputs);
    }

    private void recheckRecipe() {
        ChemicalReactionRecipeInput input = new ChemicalReactionRecipeInput(
                itemIndex.getInputItems(),
                fluidIndex.getInputFluids(),
                chemicalIndex.getInputGases(),
                chemicalIndex.getInputInfusions(),
                chemicalIndex.getInputPigments(),
                chemicalIndex.getInputSlurries()
        );

        if (input.invalid()) {
            queriedRecipe = null;
            inputToConsume = null;
            error = WorkingError.INSUFFICIENT_INPUT;
            return;
        }

        if (recipeLocked && lockedRecipe != null) {
            ChemicalReactionRecipeInput testInput = recipeLookup.testRecipe(lockedRecipe, input);
            if (testInput.invalid()) {
                queriedRecipe = null;
                inputToConsume = null;
                error = WorkingError.INSUFFICIENT_INPUT;
                return;
            }
            boolean conditionsMet = supportedConditions.containsAll(lockedRecipe.getConditions());
            if (!conditionsMet && !ignoreRisk) {
                queriedRecipe = null;
                inputToConsume = null;
                error = WorkingError.CONDITION_NOT_MATCHED;
                return;
            }
            queriedRecipe = lockedRecipe;
            inputToConsume = testInput;
            recipeQueried = true;
            error = WorkingError.WELL;
            return;
        }

        Predicate<ChemicalReactionRecipe> conditionFilter;
        if (ignoreRisk) {
            conditionFilter = recipe -> true;
        } else {
            conditionFilter = recipe -> supportedConditions.containsAll(recipe.getConditions());
        }

        var result = recipeLookup.lookupFirstRecipe(circuitType, input, conditionFilter);

        if (result.isPresent()) {
            var pair = result.get();
            queriedRecipe = pair.left();
            inputToConsume = pair.right();
            recipeQueried = true;
            error = WorkingError.WELL;
        } else {
            queriedRecipe = null;
            inputToConsume = null;
            var anyResult = recipeLookup.lookupFirstRecipe(circuitType, input, recipe -> true);
            if (anyResult.isPresent()) {
                error = WorkingError.CONDITION_NOT_MATCHED;
            } else {
                error = WorkingError.INSUFFICIENT_INPUT;
            }
        }
    }


    @Override
    public void readUpdateTag(CompoundTag tag) {
        super.readUpdateTag(tag);
        if (tag.contains(ExtraNBTConstants.ERROR, Tag.TAG_INT)) {
            error = WorkingError.byIndexStatic(tag.getInt(ExtraNBTConstants.ERROR));
        }
        if (tag.contains(NBTConstants.RUNNING, Tag.TAG_BYTE)) {
            running = tag.getBoolean(NBTConstants.RUNNING);
        }
        if (tag.contains(NBTConstants.REACTOR_DAMAGE, Tag.TAG_DOUBLE)) {
            reactorDamage = tag.getDouble(NBTConstants.REACTOR_DAMAGE);
        }
        if (tag.contains(NBTConstants.PROGRESS, Tag.TAG_LONG)) {
            progress = tag.getLong(NBTConstants.PROGRESS);
        }
        if (tag.contains(ExtraNBTConstants.RECIPE_LOCKED, Tag.TAG_BYTE)) {
            recipeLocked = tag.getBoolean(ExtraNBTConstants.RECIPE_LOCKED);
        }
        if (tag.contains(ExtraNBTConstants.LOCKED_RECIPE, Tag.TAG_STRING)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString(ExtraNBTConstants.LOCKED_RECIPE));
            if (id != null) {
                lockedRecipe = idToRecipeMap.get(id);
            } else {
                lockedRecipe = null;
            }
        } else {
            lockedRecipe = null;
        }
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        super.writeUpdateTag(tag);
        tag.putInt(ExtraNBTConstants.ERROR, error.ordinal());
        tag.putBoolean(NBTConstants.RUNNING, running);
        tag.putDouble(NBTConstants.REACTOR_DAMAGE, reactorDamage);
        tag.putLong(NBTConstants.PROGRESS, progress);
        tag.putBoolean(ExtraNBTConstants.RECIPE_LOCKED, recipeLocked);
        if (lockedRecipe != null) {
            tag.putString(ExtraNBTConstants.LOCKED_RECIPE, lockedRecipe.getId().toString());
        }
    }

    @Override
    public void remove(Level world) {
        itemIndex.clear();
        super.remove(world);
    }

    private int calculateProgress() {
        if (queriedRecipe == null) return 0;
        return (int) (queriedRecipe.getDuration() / speedMultiplier);
    }

    private void handleDamage() {
        if (queriedRecipe != null && !supportedConditions.containsAll(queriedRecipe.getConditions())
                && !supportedConditions.contains(ReactionCondition.PRESSURIZED)) {
            double damageRate = DAMAGE_RATE_PER_PARALLEL * currentParallel * (1.0 / speedMultiplier);
            reactorDamage += damageRate;
            if (reactorDamage > MAX_DAMAGE) reactorDamage = MAX_DAMAGE;
        } else {
            if (reactorDamage > 0) {
                reactorDamage = Math.max(0, reactorDamage - 0.01);
            }
        }

        if (reactorDamage >= MAX_DAMAGE) {
            meltdownHappened(getWorld());
        }
    }

    public void initIndex() {
        itemIndex.init(inputItemHatches);
        fluidIndex.init(inputFluidHatches);
        chemicalIndex.init(inputChemicalHatches);
        shouldRequeryRecipe = true;
        markDirty();
    }

    public void meltdownHappened(Level world) {
        if (!isFormed()) return;

        double magnitude = reactorDamage * currentParallel * speedMultiplier;
        float radius = EXPLOSION_RADIUS_BASE * (1.0F + (float) (magnitude / 16F));
        RadiationManager.get().createMeltdown(world, getBounds().getMinPos(), getBounds().getMaxPos(),
                magnitude, EXPLOSION_CHANCE, radius, inventoryID);

        IRadiationManager radiationManager = IRadiationManager.INSTANCE;
        if (radiationManager.isRadiationEnabled()) {
            radiationManager.radiate(new Coord4D(getBounds().getCenter(), world),
                    inputChemicalHatches.stream().map(pos -> WorldUtils.getTileEntity(TileEntityChemicalReactorChemicalIOHatch.class, getWorld(), pos, true))
                    .filter(Objects::nonNull)
                    .flatMap(tile -> tile.getGasTanks(null).stream())
                    .filter(tank -> !tank.isEmpty())
                    .map(tank -> tank.getType().get(GasAttributes.Radiation.class))
                    .filter(Objects::nonNull)
                    .map(GasAttributes.Radiation::getRadioactivity)
                    .reduce(Double::sum)
                    .orElse(0D));
        }

        clearAllContents(world);
        running = false;
        queriedRecipe = null;
        inputToConsume = null;
        progress = 0;
        reactorDamage = 0;
        currentParallel = 0;
        energyContainer.setEmpty();

        MultiblockCache<ChemicalReactorMultiblockData> cache = MekanismExtras.chemicalReactorManager.getCache(inventoryID);
        if (cache != null) {
            cache.sync(this);
        }
    }

    private void clearAllContents(Level world) {
        for (BlockPos pos : inputItemHatches) {
            TileEntityChemicalReactorItemIOHatch hatch = WorldUtils.getTileEntity(TileEntityChemicalReactorItemIOHatch.class, world, pos, true);
            if (hatch != null) {
                for (IInventorySlot slot : hatch.getInventorySlots(null)) {
                    slot.setEmpty();
                }
            }
        }
        for (BlockPos pos : outputItemHatches) {
            TileEntityChemicalReactorItemIOHatch hatch = WorldUtils.getTileEntity(TileEntityChemicalReactorItemIOHatch.class, world, pos, true);
            if (hatch != null) {
                for (IInventorySlot slot : hatch.getInventorySlots(null)) {
                    slot.setEmpty();
                }
            }
        }
        for (BlockPos pos : inputFluidHatches) {
            TileEntityChemicalReactorFluidIOHatch hatch = WorldUtils.getTileEntity(TileEntityChemicalReactorFluidIOHatch.class, world, pos, true);
            if (hatch != null) {
                for (IExtendedFluidTank tank : hatch.getFluidTanks(null)) {
                    tank.setEmpty();
                }
            }
        }
        for (BlockPos pos : outputFluidHatches) {
            TileEntityChemicalReactorFluidIOHatch hatch = WorldUtils.getTileEntity(TileEntityChemicalReactorFluidIOHatch.class, world, pos, true);
            if (hatch != null) {
                for (IExtendedFluidTank tank : hatch.getFluidTanks(null)) {
                    tank.setEmpty();
                }
            }
        }
        for (BlockPos pos : inputChemicalHatches) {
            TileEntityChemicalReactorChemicalIOHatch hatch = WorldUtils.getTileEntity(TileEntityChemicalReactorChemicalIOHatch.class, world, pos, true);
            if (hatch != null) {
                for (MergedChemicalTank tank : hatch.getCachedChemicalTanks()) {
                    tank.getGasTank().setEmpty();
                    tank.getInfusionTank().setEmpty();
                    tank.getPigmentTank().setEmpty();
                    tank.getSlurryTank().setEmpty();
                }
            }
        }
        for (BlockPos pos : outputChemicalHatches) {
            TileEntityChemicalReactorChemicalIOHatch hatch = WorldUtils.getTileEntity(TileEntityChemicalReactorChemicalIOHatch.class, world, pos, true);
            if (hatch != null) {
                for (MergedChemicalTank tank : hatch.getCachedChemicalTanks()) {
                    tank.getGasTank().setEmpty();
                    tank.getInfusionTank().setEmpty();
                    tank.getPigmentTank().setEmpty();
                    tank.getSlurryTank().setEmpty();
                }
            }
        }
    }

    // TODO: make this registerable and move it out
    public enum ReactionCondition implements StringRepresentable {

        ULTRACLEAN("ultraclean"),
        PRESSURIZED("pressurized"),
        HIGH_TEMPERATURE("high_temperature"),
        LOW_TEMPERATURE("low_temperature"),
        HIGH_VOLTAGE("high_voltage"),
        RADIATIONPROOF("radiationproof"),
        SPACE_STABILITY("space_stability"),
        ;

        private static final Map<String, ReactionCondition> nameToCondition;

        static {
            ReactionCondition[] values = values();
            nameToCondition = new Object2ObjectArrayMap<>(values.length);
            for (ReactionCondition condition : values) {
                nameToCondition.put(condition.getSerializedName(), condition);
            }
        }

        public final String id;

        ReactionCondition(String id) {
            this.id = id;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return id;
        }

        public static ReactionCondition byId(String id) {
            return nameToCondition.get(id);
        }
    }

    @NothingNullByDefault
    public enum WorkingError implements IHasTextComponent {
        INSUFFICIENT_INPUT(ExtraLang.CHEMICAL_REACTOR_ERROR_INSUFFICIENT_INPUT),
        INSUFFICIENT_INPUT_FOR_MAX_PARALLEL(ExtraLang.CHEMICAL_REACTOR_ERROR_INSUFFICIENT_INPUT_FOR_MAX_PARALLEL),
        INSUFFICIENT_ENERGY(ExtraLang.CHEMICAL_REACTOR_ERROR_INSUFFICIENT_ENERGY),
        INSUFFICIENT_ENERGY_FOR_MAX_PARALLEL(ExtraLang.CHEMICAL_REACTOR_ERROR_INSUFFICIENT_INPUT_FOR_MAX_PARALLEL),
        CONDITION_NOT_MATCHED(ExtraLang.CHEMICAL_REACTOR_ERROR_CONDITION_NOT_MATCHED),
        OUTPUT_FULL(ExtraLang.CHEMICAL_REACTOR_ERROR_OUTPUT_FULL),
        WELL(ExtraLang.CHEMICAL_REACTOR_ERROR_WELL);

        private static final WorkingError[] ERRORS = values();

        private final ILangEntry langEntry;

        WorkingError(ILangEntry langEntry) {
            this.langEntry = langEntry;
        }

        @Override
        public Component getTextComponent() {
            return langEntry.translate();
        }

        public static WorkingError byIndexStatic(int index) {
            return MathUtils.getByIndexMod(ERRORS, index);
        }
    }
}
