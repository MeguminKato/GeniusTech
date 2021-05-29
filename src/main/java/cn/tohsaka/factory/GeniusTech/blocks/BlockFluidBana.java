package cn.tohsaka.factory.GeniusTech.blocks;

import cn.tohsaka.factory.GeniusTech.GeniusTech;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cofh.core.fluid.BlockFluidInteractive;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalfoundation.ThermalFoundation;
import cofh.thermalfoundation.block.BlockOre;
import cofh.thermalfoundation.block.BlockStorage;
import cofh.thermalfoundation.init.TFBlocks;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

public class BlockFluidBana extends BlockFluidInteractive {

    public static final int LEVELS = 6;
    public static final Material materialFluidBana = new MaterialLiquid(MapColor.CYAN);

    private static boolean effect = true;
    private static boolean enableSourceFall = true;

    public BlockFluidBana(Fluid fluid) {

        super(fluid, materialFluidBana, GeniusTech.MOD_ID, "bana");
        setQuantaPerBlock(LEVELS);
        setTickRate(10);
        setHardness(2000F);
        setLightOpacity(2);
        setParticleColor(0.2F, 0.0F, 0.4F);
    }



    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

        return GFluid.fluidBana.getLuminosity();
    }




    /* IInitializer */
    @Override
    public boolean preInit() {
        this.setRegistryName("fluid_bana");
        ForgeRegistries.BLOCKS.register(this);
        ItemBlock itemBlock = new ItemBlock(this);
        itemBlock.setRegistryName(this.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);
        return true;
    }

}