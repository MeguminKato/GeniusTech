package cn.tohsaka.factory.GeniusTech.proxy;

import cn.tohsaka.factory.GeniusTech.init.GPlugins;
import cofh.core.render.IModelRegister;
import net.minecraftforge.fml.common.event.*;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent e)
    {
    }

    public void init(FMLInitializationEvent e)
    {
    }

    public void postInit(FMLPostInitializationEvent e)
    {

        GPlugins.postInit();
    }

    public boolean addIModelRegister(IModelRegister modelRegister)
    {
        return false;
    }
}
