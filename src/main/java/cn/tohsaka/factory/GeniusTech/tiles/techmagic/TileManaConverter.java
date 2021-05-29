package cn.tohsaka.factory.GeniusTech.tiles.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerHeatGenerator;
import cn.tohsaka.factory.GeniusTech.containers.ContainerManaConverter;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.GuiHeatGenerator;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.GuiManaConverter;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cn.tohsaka.factory.GeniusTech.init.GTextures;
import cn.tohsaka.factory.GeniusTech.tiles.TileTechMagicBase;
import cofh.api.item.IAugmentItem;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.impl.EnergyStorage;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.mana.IManaBlock;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.mana.TilePool;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

import static cn.tohsaka.factory.GeniusTech.GeniusTech.MOD_ID;
import static cofh.core.util.core.SideConfig.*;

public class TileManaConverter extends TileTechMagicBase implements IManaBlock {
    private static final int TYPE = BlockTechMagic.Type.MANACONVERTER.getMetadata();
    public FluidTankCore banaFluidTank = new FluidTankCore(GFluid.fluidBana,0,Fluid.BUCKET_VOLUME * 25);
    public static int basePower = 80;
    @Override
    public int getCurrentMana() {
        return banaFluidTank.getFluidAmount();
    }

    @Override
    public String getName() {
        return "tile.geniustech.blocktechmagic.mana_converter.name";
    }

    public TileManaConverter()
    {

        inventory = new ItemStack[1];
        Arrays.fill(inventory, ItemStack.EMPTY);
        createAllSlots(inventory.length);
        Arrays.fill(augments, ItemStack.EMPTY);
        setDefaultSides();
        sideConfig = SIDE_CONFIGS[TYPE];
        slotConfig = SLOT_CONFIGS[TYPE];
        energyConfig = ENERGY_CONFIGS[TYPE].copy();
        energyStorage = new EnergyStorage(0, 0);

    }
    public static void init()
    {
        SIDE_CONFIGS[TYPE] = new SideConfig();
        SIDE_CONFIGS[TYPE].numConfig = 1;
        SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}};
        SIDE_CONFIGS[TYPE].sideTypes = new int[] { INPUT_ALL };
        SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };
        SLOT_CONFIGS[TYPE] = new SlotConfig();
        SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[]{false};
        SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[]{false};
        VALID_AUGMENTS[TYPE] = new HashSet<>();
        /*VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_EXPERIENCE);
        VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_RANCHER);
        VALID_AUGMENTS[TYPE].add(VMConstants.MACHINE_PERMAMORB);*/

        GameRegistry.registerTileEntity(TileManaConverter.class, MOD_ID+"_tile_manaconverter");

        config();
    }
    public static void config()
    {
        ENERGY_CONFIGS[TYPE] = new EnergyConfig();
        ENERGY_CONFIGS[TYPE].setDefaultParams(0, true);
        ENERGY_CONFIGS[TYPE].maxPower=0;
        ENERGY_CONFIGS[TYPE].maxEnergy=0;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        String name = StringHelper.localize("tile.geniustech.blocktechmagic.mana_converter.name");
        drawHUD(0x00CCCC, banaFluidTank.getFluidAmount(), banaFluidTank.getCapacity(), name, res,20);
    }
    public static void drawHUD(int color, int mana, int maxMana, String name, ScaledResolution res,int yy) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft mc = Minecraft.getMinecraft();
        int x = res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(name) / 2;
        int y = res.getScaledHeight() / 2 + 10+yy;

        mc.fontRenderer.drawStringWithShadow(name, x, y, color);

        x = res.getScaledWidth() / 2 - 51;
        y += 10;

        HUDHandler.renderManaBar(x, y, color, mana < 0 ? 0.5F : 1F, mana, maxMana);

        if(mana < 0) {
            String text = I18n.format("botaniamisc.statusUnknown");
            x = res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(text) / 2;
            y -= 1;
            mc.fontRenderer.drawString(text, x, y, color);
        }

        GlStateManager.disableBlend();
    }

    @Override
    public String getRegisterName() {
        return "manaconverter";
    }
    @Override
    public TextureAtlasSprite getTexture(int side, int pass)
    {

            if (side == 0){
                return TETextures.MACHINE_BOTTOM;
            } else if (side == 1) {
                return GTextures.getTextures("manaconverter_top");
            }
            return GTextures.getTextures("manaconverter_sides");

    }

    @Override
    public Object getGuiClient(InventoryPlayer inventory) {
        return new GuiManaConverter(inventory,this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory) {
        return new ContainerManaConverter(inventory,this);
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
    protected boolean isValidAugment(IAugmentItem.AugmentType type, String id) {
        return false;
    }

    @Override
    public boolean setLevel(int level) {
        banaFluidTank.setCapacity(Fluid.BUCKET_VOLUME * (25*Math.max(level,1)));
        return super.setLevel(level);
    }

    @Override
    public boolean setSide(int side, int config) {
        return super.setSide(side, config);
    }

    @Override
    public PacketBase getGuiPacket()
    {
        PacketBase payload = super.getGuiPacket();
        payload.addFluidStack(banaFluidTank.getFluid());
        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketBase payload)
    {
        super.handleGuiPacket(payload);
        banaFluidTank.setFluid(payload.getFluidStack());
    }
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        banaFluidTank.readFromNBT(nbt.getCompoundTag("banaFluid"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NBTTagCompound t = new NBTTagCompound();
        banaFluidTank.writeToNBT(t);
        nbt.setTag("banaFluid",t);
        return nbt;
    }

    @Override
    public FluidTankCore getTank()
    {
        return banaFluidTank;
    }

    @Override
    public FluidStack getTankFluid()
    {
        return banaFluidTank.getFluid();
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
                    FluidTankInfo info = banaFluidTank.getInfo();
                    return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
                }

                @Override
                public int fill(FluidStack resource, boolean doFill)
                {
                    if(resource.getFluid() != GFluid.fluidBana){
                        return 0;
                    }
                    return banaFluidTank.fill(resource, doFill);
                }

                @Nullable
                @Override
                public FluidStack drain(FluidStack resource, boolean doDrain)
                {
                    return null;
                }

                @Nullable
                @Override
                public FluidStack drain(int maxDrain, boolean doDrain)
                {

                    return null;
                }
            });
        }

        return super.getCapability(capability, from);
    }

    @Override
    protected boolean canStart() {
        if(hasPool()!=null && banaFluidTank.getFluidAmount()>10){
            return true;
        }
        return false;
    }

    private TilePool hasPool(){
        for(int i=1;i<3;i++){
            BlockPos pos = new BlockPos(this.pos.getX(),this.pos.getY()+i,this.pos.getZ());
            TileEntity entity = this.getWorld().getTileEntity(pos);
            if(entity!=null && entity instanceof TilePool){
                return (TilePool)entity;
            }
        }
        return null;
    }



    @Override
    protected void processStart() {
        processMax=1600;
        processRem=processMax;
        tickcount=0;
    }

    int tickcount=0;
    @Override
    protected int processTick() {
        tickcount++;
        if(tickcount==1){
            doFill();
        }
        if(tickcount>=40){
            tickcount=0;
        }
        return tickcount;
    }

    private void doFill() {
        try{
            TilePool pool = hasPool();
            if(pool!=null){
                int size = Math.min(pool.getAvailableSpaceForMana(),banaFluidTank.getFluidAmount());
                if(size>1000){
                    //System.out.println("filling the pool with mana - "+size);
                    pool.recieveMana(new Double((size*10)*0.95).intValue());
                    banaFluidTank.drain(size,true);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public int getType() {
        return TYPE;
    }
    @Override
    public int getLightValue(){
        return 0;
    }
}
