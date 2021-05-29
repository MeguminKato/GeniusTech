package cn.tohsaka.factory.GeniusTech.init;

import cn.tohsaka.factory.GeniusTech.GeniusTech;
import cn.tohsaka.factory.GeniusTech.blocks.BlockFluidBana;
import cofh.core.fluid.BlockFluidCore;
import cofh.core.fluid.FluidCore;
import cofh.core.util.core.IInitializer;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;

public class GFluid {
    public static final GFluid INSTANCE = new GFluid();
    public static void preInit() {

        registerAllFluids();
        registerAllFluidBlocks();
        createBuckets();
        refreshReferences();

        for (IInitializer init : initList) {
            init.preInit();
        }
    }

    public static void registerAllFluids() {
        fluidBana = new FluidCore("bana", GeniusTech.MOD_ID).setLuminosity(15).setDensity(600).setViscosity(6000).setTemperature(350).setRarity(EnumRarity.EPIC);

        FluidRegistry.registerFluid(fluidBana);
    }

    public static void registerAllFluidBlocks() {
        blockFluidBana = new BlockFluidBana(fluidBana);

        initList.add(blockFluidBana);

        GeniusTech.proxy.addIModelRegister(blockFluidBana);
    }

    public static void createBuckets() {
        FluidRegistry.addBucketForFluid(fluidBana);
    }

    public static void refreshReferences() {
        fluidBana = FluidRegistry.getFluid("bana");
    }


    private static ArrayList<IInitializer> initList = new ArrayList<>();

    public static Fluid fluidBana;
    public static BlockFluidCore blockFluidBana;
}
