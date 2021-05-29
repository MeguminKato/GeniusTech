package cn.tohsaka.factory.GeniusTech.proxy;

import cn.tohsaka.factory.GeniusTech.init.GTextures;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient
{
    public static final EventHandlerClient INSTANCE = new EventHandlerClient();

    @SubscribeEvent
    public void handleTextureStitchPreEvent(TextureStitchEvent.Pre event)
    {
        GTextures.registerTextures(event.getMap());
    }
}
