package com.jerry.generator_extras.common.genregistry;

import com.jerry.mekanism_extras.MekanismExtras;
import mekanism.common.registration.impl.SoundEventDeferredRegister;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ExtraGenSounds {

    private ExtraGenSounds() {}

    public static final SoundEventDeferredRegister EXTRA_GEN_SOUNDS = new SoundEventDeferredRegister(MekanismExtras.MODID);

    public static final SoundEventRegistryObject<SoundEvent> PLASMA_EVAPORATION = EXTRA_GEN_SOUNDS.register("tile.machine.plasma_evaporation");

    public static void register(IEventBus bus) {
        EXTRA_GEN_SOUNDS.register(bus);
    }
}
