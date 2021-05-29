package cn.tohsaka.factory.GeniusTech.items;

import cn.tohsaka.factory.GeniusTech.GeniusTech;
import cn.tohsaka.factory.GeniusTech.init.GItems;
import cofh.api.item.IAugmentItem;
import cofh.core.item.ItemMulti;
import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ItemRecordArknights extends ItemRecord implements IInitializer, IModelRegister {

    private String name;
    public ItemRecordArknights(String p_i46742_1_, SoundEvent soundIn) {
        super(p_i46742_1_, soundIn);
        name = p_i46742_1_;
        setUnlocalizedName("record."+p_i46742_1_);
        setHasSubtypes(true);
        GItems.records.add(this);
    }

    @Override
    public boolean preInit() {
        ForgeRegistries.ITEMS.register(setRegistryName("itemRecordArknights_"+name));
        GeniusTech.proxy.addIModelRegister(this);
        return true;
    }

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(GeniusTech.MOD_ID+":itemRecordArknights", "type="+ name));
    }

}
