package cn.tohsaka.factory.GeniusTech.tiles.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerHeatGenerator;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.GuiHeatGenerator;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cn.tohsaka.factory.GeniusTech.utils.Utils;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.FluidHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static cn.tohsaka.factory.GeniusTech.GeniusTech.MOD_ID;
import static cofh.core.util.core.SideConfig.*;

public class TileTNTGenerator extends TileHeatGenerator {
    private static final int TYPE = BlockTechMagic.Type.TNTGENERATOR.getMetadata();
    public static void init()
    {
        SIDE_CONFIGS[TYPE] = new SideConfig();
        SIDE_CONFIGS[TYPE].numConfig = 3;
        SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {0},{1}};
        SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL };
        SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

        SLOT_CONFIGS[TYPE] = new SlotConfig();
        SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[]{true,true};
        SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[]{false,false};

        VALID_AUGMENTS[TYPE] = new HashSet<>();
        /*VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_EXPERIENCE);
        VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_RANCHER);
        VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_PERMAMORB);*/

        GameRegistry.registerTileEntity(TileTNTGenerator.class, MOD_ID+"_tile_tntgenerator");

        config();
    }
    @Override
    public String getRegisterName() {
        return "tntgenerator";
    }

    @Override
    protected boolean canStart() {
        return energyStorage.getEnergyStored()>0 && inventory[0].getItem() == ItemBlock.getItemFromBlock(Blocks.TNT);
    }

    @Override
    protected boolean canFinish() {
        return processRem<=0;
    }

    @Override
    protected void processStart() {
        BlockPos pos = this.pos;
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        this.world.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
        List<EntityLivingBase> entities = this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class,aabb.grow(3,3,3),EntityLivingBase::isEntityAlive);
        for (EntityLivingBase entity:entities){
            if(entity!=null && entity.isEntityAlive()){
                entity.attackEntityFrom(DamageSource.MAGIC,4*Math.max(augment_turbine,1));
            }
        }

        processMax=1600;
        mana2fill=650;
        if(augment_speed>0){
            processMax=1600-(400*augment_speed);
            mana2fill-=40*augment_speed;
        }

        if(augment_turbine>0){
            if(inventory[0].getCount()>(augment_turbine*4)){
                Utils.drainItemFromInventory(this,new ItemStack[]{new ItemStack(ItemBlock.getItemFromBlock(Blocks.TNT),4)},0,1);
                processMax=1800*4;
                mana2fill *= 4;
            }else{
                Utils.drainItemFromInventory(this,new ItemStack[]{new ItemStack(ItemBlock.getItemFromBlock(Blocks.TNT),inventory[0].getCount())},0,1);
                processMax= 1800*inventory[0].getCount();
                mana2fill *= inventory[0].getCount();
            }
        }else{
            Utils.drainItemFromInventory(this,new ItemStack[]{new ItemStack(ItemBlock.getItemFromBlock(Blocks.TNT))},0,1);
        }


        processRem=processMax;
    }

    @Override
    protected void processFinish() {
        super.processFinish();
        mana2fill=0;
    }

    @Override
    protected int processTick() {
        processRem-=20;
        return 0;
    }
    @Override
    public String getName() {
        return "tile.geniustech.blocktechmagic.tnt_generator.name";
    }

}
