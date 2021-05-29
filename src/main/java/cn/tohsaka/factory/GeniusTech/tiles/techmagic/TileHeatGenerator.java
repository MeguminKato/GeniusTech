package cn.tohsaka.factory.GeniusTech.tiles.techmagic;

import cn.tohsaka.factory.GeniusTech.GeniusTech;
import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerHeatGenerator;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.GuiHeatGenerator;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cn.tohsaka.factory.GeniusTech.init.GItems;
import cn.tohsaka.factory.GeniusTech.init.Gblocks;
import cn.tohsaka.factory.GeniusTech.items.ItemTmAugment;
import cn.tohsaka.factory.GeniusTech.tiles.TileTechMagicBase;
import cn.tohsaka.factory.GeniusTech.utils.Utils;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoSteam;
import cofh.thermalexpansion.block.machine.TileFurnace;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.ForgeEventFactory;
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
import org.lwjgl.opengl.GL11;
import scala.Int;
import vazkii.botania.api.mana.IManaBlock;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ItemManaTablet;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

import static cn.tohsaka.factory.GeniusTech.GeniusTech.MOD_ID;
import static cofh.core.util.core.SideConfig.*;

public class TileHeatGenerator extends TileTechMagicBase implements IManaBlock {
    private static final int TYPE = BlockTechMagic.Type.HEATGENERATOR.getMetadata();
    public static int basePower = 80;

    public static int SLOT_TOOLS_START = 0;
    public static int SLOT_ANIMAL_MORB = 4;
    public static int SLOT_OUTPUT_START = 5;

    public static final int EXPERIENCE_MOD = 40;
    public static final int RANCHER_MOD = 40;
    public static final int PERMAMORB_MOD = 220;
    public static final int EXPERIENCE = 50;
    public static final int SLOT_COUNT = 2;
    public FluidTankCore manaTank = new FluidTankCore(Fluid.BUCKET_VOLUME * 100);
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

        GameRegistry.registerTileEntity(TileHeatGenerator.class, MOD_ID+"_tile_heatgenerator");

        config();
    }
    public static void config()
    {
        ENERGY_CONFIGS[TYPE] = new EnergyConfig();
        ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
    }
    public TileHeatGenerator()
    {
        super();
        inventory = new ItemStack[SLOT_COUNT];
        Arrays.fill(inventory, ItemStack.EMPTY);
        createAllSlots(inventory.length);
    }

    @Override
    public int getType()
    {
        return TYPE;
    }

    @Override
    public String getRegisterName() {
        return "heatgenerator";
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
        String name = StringHelper.localize("tile.geniustech.blocktechmagic.heat_generator.name");
        int color = 0x00FF33;
        HUDHandler.drawSimpleManaHUD(color, manaTank.getFluidAmount(), manaTank.getCapacity(), name, res);
    }

    @Override
    public Object getGuiClient(InventoryPlayer inventory) {
        return new GuiHeatGenerator(inventory,this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory) {
        return new ContainerHeatGenerator(inventory,this);
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
                    return 0;
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
        ItemStack fuel = this.inventory[0];
        return (fuel!=ItemStack.EMPTY && TileEntityFurnace.getItemBurnTime(fuel)>0 && manaTank.getFluidAmount()<manaTank.getCapacity() && energyStorage.getEnergyStored()>=basePower);
    }

    @Override
    protected boolean canFinish() {
        return super.canFinish();
    }
    int burntime = 0;
    public int mana2fill = 0;
    @Override
    protected void processStart() {
        burntime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(0));
        mana2fill = new Double(burntime * 0.075).intValue();
        if(augment_turbine>0){
            if(inventory[0].getCount()>=augment_turbine*4){
                burntime = burntime*4;
                mana2fill*=3;
                inventory[0].setCount(inventory[0].getCount()-4);
            }else{
                burntime = burntime * inventory[0].getCount();
                mana2fill=new Double((mana2fill*0.75)*inventory[0].getCount()).intValue();
                inventory[0] = ItemStack.EMPTY;
            }

        }else{
            inventory[0].setCount(inventory[0].getCount()-1);
        }
        if(augment_speed>0){
            processMax = new Double(burntime*(1-(0.4*augment_speed))).intValue();
            mana2fill = new Double(mana2fill*(1-(0.2*augment_speed))).intValue();
        }else{
            processMax = burntime;
        }
        processRem = processMax;
    }

    @Override
    protected void processFinish() {
        manaTank.fill(new FluidStack(GFluid.fluidBana,mana2fill),true);
        transferOutputFluid();
        burntime = 0;
    }

    int i=0;
    @Override
    protected int processTick() {
        if(i>=100){
            transferOutputFluid();
            i=-1;
        }
        i++;

        processRem-=100;
        energyStorage.modifyEnergyStored(-calcEnergy());
        if(energyStorage.getEnergyStored()<=0){
            processOff();
            return 0;
        }
        return energyStorage.getEnergyStored();
    }
    @Override
    protected void transferInput() {
        super.transferInput();
    }


    protected static final int FLUID_TRANSFER2[] = new int[] { 8000, 15000, 30000, 50000, 100000 };
    private void transferOutputFluid()
    {
        if (manaTank.getFluidAmount() <= 0)
            return;

        int side;
        FluidStack output = new FluidStack(manaTank.getFluid(), Math.min(manaTank.getFluidAmount(), FLUID_TRANSFER2[level]));

        for(int i=0;i<6;i++){

            if (Utils.canSideOutput(sideCache[i])) {
                int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[i], output, true);
                if (toDrain > 0)
                {
                    manaTank.drain(toDrain, true);
                    break;
                }
            }
        }
        return;
    }

    @Override
    public String getName() {
        return "tile.geniustech.blocktechmagic.heat_generator.name";
    }
    int augment_speed=0;
    int augment_turbine=0;

    @Override
    protected void preAugmentInstall()
    {
        super.preAugmentInstall();
        augment_speed = 0;
        augment_turbine = 0;
    }

    @Override
    protected boolean installAugmentToSlot(int slot)
    {
        switch (augments[slot].getMetadata()){
            case 0:
                augment_speed++;
                break;
            case 1:
                augment_turbine++;
                break;
        }
        return super.installAugmentToSlot(slot);
    }

    @Override
    public boolean isValidAugment(ItemStack augment) {
        return augment.getItem() instanceof ItemTmAugment && augment.getMetadata()<=1;
    }
    public int getMaxProcessTime(){
        return processMax;
    }
}
