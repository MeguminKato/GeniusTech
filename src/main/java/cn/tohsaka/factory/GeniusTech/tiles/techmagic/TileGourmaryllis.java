package cn.tohsaka.factory.GeniusTech.tiles.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.utils.Utils;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashSet;
import java.util.List;

import static cn.tohsaka.factory.GeniusTech.GeniusTech.MOD_ID;
import static cofh.core.util.core.SideConfig.*;

public class TileGourmaryllis extends TileHeatGenerator {
    private static final int TYPE = BlockTechMagic.Type.GOURMARYLLIS.getMetadata();
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

        GameRegistry.registerTileEntity(TileGourmaryllis.class, MOD_ID+"_tile_gourmaryllis");

        config();
    }
    @Override
    public String getRegisterName() {
        return "gourmaryllis";
    }

    @Override
    protected boolean canStart() {
        return energyStorage.getEnergyStored()>0 && inventory[0].getItem() instanceof ItemFood;
    }

    @Override
    protected boolean canFinish() {
        return processRem<=0;
    }

    private ItemStack lastFood = ItemStack.EMPTY;
    @Override
    protected void processStart() {
        if(inventory[0].getItem() instanceof ItemFood){
            if(inventory[0].getItem() == lastFood.getItem() && inventory[0].getMetadata()==lastFood.getMetadata()){
                lastFood.setCount(lastFood.getCount()+1);
            }else{
                lastFood = inventory[0].copy();
                lastFood.setCount(1);
            }

            int val = Math.min(12, ((ItemFood) inventory[0].getItem()).getHealAmount(inventory[0]));
            mana2fill = val * val * 7;
            mana2fill *= 1F / lastFood.getCount();
            this.decrStackSize(0,1);
            processMax = val * 15  - Math.max(level,1)*4;
            processRem = processMax;
            if(lastFood.getCount()==1) {
                this.world.playSound((EntityPlayer) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5F, 0.6F);
            }
        }
    }

    @Override
    protected void processFinish() {
        super.processFinish();
        mana2fill=0;
    }

    @Override
    protected int processTick() {
        processRem--;
        return 0;
    }
    @Override
    public String getName() {
        return "tile.geniustech.blocktechmagic.gourmaryllis.name";
    }

    @Override
    protected int getNumAugmentSlots(int level) {
        return 0;
    }
}
