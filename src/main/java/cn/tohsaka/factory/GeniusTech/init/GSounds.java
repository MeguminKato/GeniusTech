package cn.tohsaka.factory.GeniusTech.init;

        import cn.tohsaka.factory.GeniusTech.GeniusTech;
        import cofh.thermalexpansion.ThermalExpansion;
        import net.minecraft.client.audio.SoundRegistry;
        import net.minecraft.util.ResourceLocation;
        import net.minecraft.util.SoundEvent;
        import net.minecraftforge.common.MinecraftForge;
        import net.minecraftforge.event.RegistryEvent;
        import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
        import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class GSounds {

    public static final GSounds INSTANCE = new GSounds();

    private GSounds() {

    }

    public static void preInit() {
        ep8pv = registerSoundEvent("ep8pv");
        infacted =registerSoundEvent("infacted");
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        
    }

    /* EVENT HANDLING */
    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {

    }

    private static SoundEvent registerSoundEvent(String id) {

        ResourceLocation loc = new ResourceLocation(GeniusTech.MOD_ID + ":" + id);
        SoundEvent sound = new SoundEvent(loc).setRegistryName(loc);
        ForgeRegistries.SOUND_EVENTS.register(sound);
        return sound;
    }

    public static SoundEvent ep8pv;
    public static SoundEvent infacted;
}