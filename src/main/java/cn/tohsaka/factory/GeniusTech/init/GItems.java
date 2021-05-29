package cn.tohsaka.factory.GeniusTech.init;

import cn.tohsaka.factory.GeniusTech.items.ItemRecordArknights;
import cn.tohsaka.factory.GeniusTech.items.ItemTmAugment;
import cofh.core.util.core.IInitializer;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class GItems {
    public static final GItems INSTANCE = new GItems();
    private static ArrayList<IInitializer> initList = new ArrayList<>();
    public static ItemTmAugment itemTmAugment;
    public static ItemRecordArknights itemRecordArknights_ep8pv;
    public static ItemRecordArknights itemRecordArknights_infacted;
    public static List<ItemRecord> records = new ArrayList<>();
    public static void preInit()
    {
        itemTmAugment = new ItemTmAugment();
        itemRecordArknights_ep8pv = new ItemRecordArknights("ep8pv",GSounds.ep8pv);
        itemRecordArknights_infacted = new ItemRecordArknights("infacted",GSounds.infacted);
        initList.add(itemTmAugment);
        initList.add(itemRecordArknights_ep8pv);
        initList.add(itemRecordArknights_infacted);
        for (IInitializer init : initList){
            init.preInit();
        }
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        for (IInitializer init : initList)
            init.initialize();
    }
}
