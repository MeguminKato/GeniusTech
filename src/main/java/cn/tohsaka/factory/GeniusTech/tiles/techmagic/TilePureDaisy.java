package cn.tohsaka.factory.GeniusTech.tiles.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerPureDaisy;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.GuiPureDaisy;
import cn.tohsaka.factory.GeniusTech.tiles.TileTechMagicBase;
import cn.tohsaka.factory.GeniusTech.utils.Utils;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaBlock;
import vazkii.botania.api.recipe.RecipePureDaisy;
import vazkii.botania.client.core.handler.HUDHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;

import static cn.tohsaka.factory.GeniusTech.GeniusTech.MOD_ID;
import static cofh.core.util.core.SideConfig.*;

public class TilePureDaisy extends TileTechMagicBase implements IManaBlock {
    private static final int TYPE = BlockTechMagic.Type.PUREDAISY.getMetadata();
    public static int basePower = 80;

    public static int SLOT_TOOLS_START = 0;
    public static int SLOT_ANIMAL_MORB = 4;
    public static int SLOT_OUTPUT_START = 5;

    public static final int EXPERIENCE_MOD = 40;
    public static final int RANCHER_MOD = 40;
    public static final int PERMAMORB_MOD = 220;
    public static final int EXPERIENCE = 50;
    public static final int SLOT_COUNT = 2;
    public FluidTankCore manaTank = new FluidTankCore(100);
    public static void init()
    {
        SIDE_CONFIGS[TYPE] = new SideConfig();
        SIDE_CONFIGS[TYPE].numConfig = 3;
        SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {0},{1}};
        SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL };
        SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

        SLOT_CONFIGS[TYPE] = new SlotConfig();
        SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[]{true,false};
        SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[]{false,true};
        VALID_AUGMENTS[TYPE] = new HashSet<>();
        /*VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_EXPERIENCE);
        VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_RANCHER);
        VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_PERMAMORB);*/



        GameRegistry.registerTileEntity(TilePureDaisy.class, MOD_ID+"_tile_puredaisy");

        config();
    }
    public static void config()
    {
        ENERGY_CONFIGS[TYPE] = new EnergyConfig();
        ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
        ENERGY_CONFIGS[TYPE].maxPower=0;
        ENERGY_CONFIGS[TYPE].maxEnergy=0;
    }
    public TilePureDaisy()
    {
        super();
        inventory = new ItemStack[SLOT_COUNT];
        Arrays.fill(inventory, ItemStack.EMPTY);
        createAllSlots(inventory.length);
        energyConfig = ENERGY_CONFIGS[TYPE].copy();
        energyStorage = new EnergyStorage(0, 0);
    }

    @Override
    public int getType()
    {
        return TYPE;
    }

    @Override
    public String getRegisterName() {
        return "puredaisy";
    }

    /**
     * Gets the amount of mana currently in this block.
     */
    @Override
    public int getCurrentMana() {
        return manaTank.getFluidAmount();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        String name = StringHelper.localize("tile.geniustech.blocktechmagic."+getRegisterName()+".name");
        int color = 0x00FF33;
        HUDHandler.drawSimpleManaHUD(color, manaTank.getFluidAmount(), manaTank.getCapacity(), name, res);
    }

    @Override
    public Object getGuiClient(InventoryPlayer inventory) {
        return new GuiPureDaisy(inventory,this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory) {
        return new ContainerPureDaisy(inventory,this);
    }
    @Override
    public PacketBase getModePacket()
    {
        return super.getModePacket();
    }

    @Override
    protected void handleModePacket(PacketBase payload)
    {
        super.handleModePacket(payload);
    }

    @Override
    public PacketBase getGuiPacket()
    {
        PacketBase payload = super.getGuiPacket();
        payload.addFluidStack(manaTank.getFluid());
        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketBase payload)
    {
        super.handleGuiPacket(payload);
        manaTank.setFluid(payload.getFluidStack());
    }
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        manaTank.readFromNBT(nbt.getCompoundTag("manaFluid"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NBTTagCompound t = new NBTTagCompound();
        manaTank.writeToNBT(t);
        nbt.setTag("manaFluid",t);
        return nbt;
    }

    @Override
    public FluidTankCore getTank()
    {
        return manaTank;
    }

    @Override
    public FluidStack getTankFluid()
    {
        return manaTank.getFluid();
    }
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing from)
    {
        return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
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
                    FluidTankInfo info = manaTank.getInfo();
                    return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, false, true) };
                }

                @Override
                public int fill(FluidStack resource, boolean doFill)
                {
                    return manaTank.fill(resource,doFill);
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
    protected boolean canStart() {
        if(inventory[0]==ItemStack.EMPTY){
            return false;
        }
        return isValidInput(inventory[0]) && manaTank.getFluidAmount()>0;
    }
    RecipePureDaisy nextRecipe;
    public boolean isValidInput(ItemStack itemStack){
        for(RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes){
            if(recipe.getInput() instanceof String){
                ItemBlock i1 = (ItemBlock) itemStack.getItem();
                IBlockState ibs = i1.getBlock().getDefaultState();
                if(recipe.matches(null,null,null,ibs)){
                    nextRecipe = recipe;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void processStart() {
        processMax=55-((level+1)*5);
        processRem=processMax;
    }

    @Override
    protected void processFinish() {
        if(nextRecipe==null && !canStart()){
            return;
        }
        this.decrStackSize(0,1);
        Utils.distributeOutput(this,new ItemStack(nextRecipe.getOutputState().getBlock(),1),1,2);
    }
    int i = 0 ;
    @Override
    protected int processTick() {
        transferInput();
        if(manaTank.getFluidAmount()<=0){
            processOff();
        }else if (!redstoneControlOrDisable()){
            processOff();
        }else{
            if(i>=40){
                manaTank.drain(1,true);
                i=-1;
            }
        }
        i++;
        processRem--;
        return processRem;
    }


    @Override
    public void update()
    {
        boolean curActive = isActive;
        transferInput();
        if (isActive)
        {
            processTick();

            if (canFinish())
            {
                processFinish();
                transferOutput();
                if (!redstoneControlOrDisable() || !canStart()){
                    processOff();
                }else{
                    processStart();
                }
            }
        }
        else if (redstoneControlOrDisable())
        {
            if (timeCheck())
            {
                transferOutput();
                transferInput();
            }
            boolean cs = canStart();
            if (timeCheckEighth() && canStart())
            {
                processStart();
                processTick();
                isActive = true;
            }
        }
        updateIfChanged(curActive);
    }
    @Override
    protected int getNumAugmentSlots(int level)
    {
        return 0;
    }
}
