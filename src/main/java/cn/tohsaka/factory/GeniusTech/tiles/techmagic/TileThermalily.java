package cn.tohsaka.factory.GeniusTech.tiles.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerThermalily;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.GuiThermalily;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cn.tohsaka.factory.GeniusTech.utils.Utils;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketBase;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

import static cn.tohsaka.factory.GeniusTech.GeniusTech.MOD_ID;
import static cofh.core.util.core.SideConfig.*;

public class TileThermalily extends TileHeatGenerator {
    private static final int TYPE = BlockTechMagic.Type.THERMALILY.getMetadata();
    public FluidTankCore lavaTank = new FluidTankCore(Fluid.BUCKET_VOLUME * 20).setLock(FluidRegistry.LAVA);
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

        GameRegistry.registerTileEntity(TileThermalily.class, MOD_ID+"_tile_thermalily");

        config();
    }
    @Override
    public String getRegisterName() {
        return "thermalily";
    }

    @Override
    protected boolean canStart() {
        if(energyStorage.getEnergyStored()<=0){
            return false;
        }
        if(augment_turbine>0){
            return lavaTank.getFluidAmount()>=1000*augment_turbine;
        }else {
            return lavaTank.getFluidAmount()>=250;
        }
    }

    @Override
    protected boolean canFinish() {
        return processRem<=0;
    }

    @Override
    protected void processStart() {

        if(augment_turbine>0){
            lavaTank.drain(1000*augment_turbine,true);
            processMax=900*augment_turbine;
            mana2fill=1800*augment_turbine;
        }else{
            lavaTank.drain(250,true);
            processMax=225;
            mana2fill=450;
        }
        processRem=processMax;
        sendTilePacket(Side.CLIENT);
    }

    @Override
    protected void processFinish() {
        super.processFinish();
        mana2fill=0;
    }

    @Override
    protected int processTick() {
        if(mana2fill==0){
            processOff();
            return 0;
        }
        processRem-=Math.max(1,level);
        if(augment_speed>0){
            processRem-=augment_speed*5;
        }
        //energyStorage.modifyEnergyStored(-100);
        return 0;
    }
    @Override
    public String getName() {
        return "tile.geniustech.blocktechmagic.thermalily.name";
    }

    @Override
    public Object getGuiClient(InventoryPlayer inventory) {
        return new GuiThermalily(inventory,this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory) {
        return new ContainerThermalily(inventory,this);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing from)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler()
            {
                @Override
                public IFluidTankProperties[] getTankProperties()
                {
                    return new IFluidTankProperties[] {
                            new FluidTankProperties(new FluidStack(FluidRegistry.LAVA,0), lavaTank.getCapacity(), true, false),
                            new FluidTankProperties(new FluidStack(GFluid.fluidBana,0), manaTank.getCapacity(), false, true)
                    };
                }

                @Override
                public int fill(FluidStack resource, boolean doFill)
                {
                    return lavaTank.fill(resource,doFill);
                }

                @Nullable
                @Override
                public FluidStack drain(FluidStack resource, boolean doDrain)
                {
                    return manaTank.drain(resource, doDrain);
                }

                @Nullable
                @Override
                public FluidStack drain(int maxDrain, boolean doDrain)
                {
                    return manaTank.drain(maxDrain, doDrain);
                }
            });
        }

        return super.getCapability(capability, from);
    }

    @Override
    public PacketBase getGuiPacket() {
        PacketBase payload = super.getGuiPacket();
        payload.addFluidStack(lavaTank.getFluid());
        payload.addInt(mana2fill);
        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketBase payload) {
        super.handleGuiPacket(payload);
        lavaTank.setFluid(payload.getFluidStack());
        mana2fill=payload.getInt();
    }

    @Override
    public PacketBase getTilePacket() {
        PacketBase payload = super.getTilePacket();
        payload.addInt(mana2fill);
        return payload;
    }

    @Override
    public void handleTilePacket(PacketBase payload) {
        super.handleTilePacket(payload);
        mana2fill=payload.getInt();
    }

    @Override
    public int getMaxProcessTime() {
        int time = processMax-Math.max(1,level);
        if(augment_speed>0){
            time-=augment_speed*5;
        }
        return time;
    }
}
