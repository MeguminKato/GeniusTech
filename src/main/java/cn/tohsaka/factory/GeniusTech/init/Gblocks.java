package cn.tohsaka.factory.GeniusTech.init;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cofh.core.util.core.IInitializer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class Gblocks {
    public static final Gblocks INSTANCE = new Gblocks();

    private Gblocks() {}

    private static ArrayList<IInitializer> initList = new ArrayList<>();

    public static BlockTechMagic blockTechMagic;

    public static void preInit()
    {
        blockTechMagic = new BlockTechMagic();

        initList.add(blockTechMagic);

        for (IInitializer init : initList)
            init.preInit();

        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        for (IInitializer init : initList)
            init.initialize();
    }
}
