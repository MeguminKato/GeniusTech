package cn.tohsaka.factory.GeniusTech;

import cn.tohsaka.factory.GeniusTech.gui.GuiHandler;
import cn.tohsaka.factory.GeniusTech.init.*;
import cn.tohsaka.factory.GeniusTech.proxy.CommonProxy;
import cofh.core.gui.CreativeTabCore;
import cofh.core.util.ConfigHandler;
import cofh.thermalexpansion.init.TEItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = GeniusTech.MOD_ID, version = GeniusTech.VERSION, name = GeniusTech.NAME, dependencies = "required-after:redstoneflux;after:cofhcore;after:thermalfoundation;after:thermalexpansion;after:botania;", guiFactory = "cn.tohsaka.factory.GeniusTech.gui.GuiConfigVMFactory")
public class GeniusTech
{
    public static final String MOD_ID = "geniustech";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "GeniusTech";
    @Mod.Instance(MOD_ID)
    public static GeniusTech INSTANCE;

    @SidedProxy(serverSide = "cn.tohsaka.factory.GeniusTech.proxy.CommonProxy", clientSide = "cn.tohsaka.factory.GeniusTech.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static final ConfigHandler CONFIG = new ConfigHandler(VERSION);
    public static final Logger LOGGER = LogManager.getLogger(GeniusTech.NAME);
    public static final GuiHandler GUI_HANDLER = new GuiHandler();
    public GeniusTech(){

    }
    public static CreativeTabs TAB_GENIUSTECH = new CreativeTabCore(MOD_ID)
    {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack()
        {

            return new ItemStack(TEItems.itemFrame);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        GSounds.preInit();
        GFluid.preInit();
        Gblocks.preInit();
        GItems.preInit();
        GPlugins.preInit();

        registerHandlers();
        proxy.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        proxy.postInit(e);

    }

    private void registerHandlers()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, GUI_HANDLER);
    }

}
//https://github.com/ExtraMeteorP/Extra-Botany/blob/master/src/main/java/com/meteor/extrabotany/common/block/tile/TileManaGenerator.java
//https://github.com/ExtraMeteorP/Extra-Botany/blob/master/build.gradle
//https://forums.minecraftforge.net/topic/24842-helpsystem-to-register-fluids/