package cn.tohsaka.factory.GeniusTech.tiles.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerElvenTrader;
import cn.tohsaka.factory.GeniusTech.containers.ContainerManaConverter;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.GuiElvenTrader;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.GuiManaConverter;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cn.tohsaka.factory.GeniusTech.init.GItems;
import cn.tohsaka.factory.GeniusTech.init.GTextures;
import cn.tohsaka.factory.GeniusTech.tiles.TileTechMagicBase;
import cn.tohsaka.factory.GeniusTech.utils.Utils;
import cofh.api.item.IAugmentItem;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.impl.EnergyStorage;
import cofh.thermalexpansion.init.TETextures;
import crafttweaker.mc1120.entity.EntityDrop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaBlock;
import vazkii.botania.api.recipe.RecipeElvenTrade;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.mana.TilePool;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;

import static cn.tohsaka.factory.GeniusTech.GeniusTech.MOD_ID;
import static cofh.core.util.core.SideConfig.*;

public class TileElvenTrader extends TileTechMagicBase implements IManaBlock {
    private static final int TYPE = BlockTechMagic.Type.ELVENTRADER.getMetadata();
    public FluidTankCore banaFluidTank = new FluidTankCore(GFluid.fluidBana,0,1100);
    @Override
    public int getCurrentMana() {
        return banaFluidTank.getFluidAmount();
    }

    @Override
    public String getName() {
        return "tile.geniustech.blocktechmagic.elventrader.name";
    }

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_INPUT_START = 0;
    private static final int SLOT_OUTPUT_START = 9;
    public TileElvenTrader()
    {

        inventory = new ItemStack[SLOT_SIZE];
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
        SIDE_CONFIGS[TYPE].numConfig = 3;
        SIDE_CONFIGS[TYPE].slotGroups = new int[][] {{}, IntStream.range(0,8).toArray(),IntStream.range(9,17).toArray()};
        SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE,INPUT_ALL,OUTPUT_ALL };
        SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };
        SLOT_CONFIGS[TYPE] = new SlotConfig();
        SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[]{true,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false};
        SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[]{false,false,false,false,false,false,false,false,true,true,true,true,true,true,true,true,true};
        VALID_AUGMENTS[TYPE] = new HashSet<>();

        GameRegistry.registerTileEntity(TileElvenTrader.class, MOD_ID+"_tile_elventrader");

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
        String name = StringHelper.localize("tile.geniustech.blocktechmagic.elventrader.name");
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
        return "elventrader";
    }


    @Override
    public Object getGuiClient(InventoryPlayer inventory) {
        return new GuiElvenTrader(inventory,this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory) {
        return new ContainerElvenTrader(inventory,this);
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
    public int getType() {
        return TYPE;
    }
    @Override
    public int getLightValue(){
        return 0;
    }
    int p = 0;
    @Override
    public void update() {
        if(banaFluidTank.getFluidAmount()<=0){
            return;
        }
        if(!isActive && banaFluidTank.getFluidAmount()>1000){
            banaFluidTank.drain(1000,true);
            isActive = true;
        }
        if(trades ==null || trades.size()==0){
            buildTradeList();
        }
        if(p==Math.abs(level-5)*20){
            doTrade();
            p=-1;
        }
        p++;
    }
    int trade = 0;
    private void doTrade(){
        if(Utils.checkInvHasItem(this,SLOT_INPUT_START,9, new ItemStack(Items.BREAD,1))){
            makeExplode();
            banaFluidTank.drain(100,true);
            Utils.drainItemFromInventory(this,new ItemStack[]{new ItemStack(Items.BREAD,64)},SLOT_INPUT_START,9);
            return;
        }
        for(ElvenTrade t:trades){
            if(containsItem(t.input)){
                Utils.drainItemFromInventory(this,Collections.unmodifiableCollection(t.input).toArray(new ItemStack[]{}),SLOT_INPUT_START,9);
                for(ItemStack i : Collections.unmodifiableList(t.output)){
                    Utils.distributeOutput(this,i.copy(),SLOT_OUTPUT_START,18);
                }
                trade++;
                if(trade==10){
                    banaFluidTank.drain(1,true);
                    trade=0;
                }
                break;
            }
        }
    }
    private void makeExplode(){
        BlockPos pos = this.pos;
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        this.world.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 10.0F, 1.0F);
        List<EntityLivingBase> entities = this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class,aabb.grow(16,16,16),EntityLivingBase::isEntityAlive);
        for (EntityLivingBase entity:entities){
            if(entity!=null && entity.isEntityAlive()){
                entity.attackEntityFrom(DamageSource.MAGIC,40);
            }
            if(entity!=null && entity instanceof EntityPlayer){
                ((EntityPlayer)entity).sendMessage(new TextComponentString(StringHelper.localize("info.geniustech.tips.elventrader_explode")));
            }
        }

        EntityItem item = new EntityItem(this.world, pos.getX(), pos.getY()+3, pos.getZ(), new ItemStack(GItems.records.get(this.world.rand.nextInt(GItems.records.size())), 1));
        this.world.spawnEntity(item);
    }
    private boolean containsItem(List<ItemStack> items){
        for(ItemStack item:items){
            if(!Utils.checkInvHasItem(this,SLOT_INPUT_START,9,item)){
                return false;
            }
        }
        return true;
    }
    public static class ElvenTrade{
        public List<ItemStack> input;
        public List<ItemStack> output;
        public ElvenTrade(List<ItemStack> in,List<ItemStack> out){
            input=in;
            output=out;
        }
    }
    private static Collection<ElvenTrade> trades;
    public static void buildTradeList(){
        List<ElvenTrade> t= new ArrayList<>();
        for(RecipeElvenTrade recipe : Collections.unmodifiableList(BotaniaAPI.elvenTradeRecipes)) {
            List<ItemStack> input = new ArrayList<>();
            for(Object o : recipe.getInputs()) {
                if(o instanceof String) {
                    for(ItemStack item:OreDictionary.getOres((String) o)){
                        if(input.size()>0) {
                            boolean find = false;
                            for (ItemStack ii : input) {
                                if (ii.isItemEqual(item)) {
                                    ii.setCount(ii.getCount() + item.getCount());
                                    find=true;
                                }
                            }
                            if(find==false){
                                input.add(item);
                            }
                        }else{
                            input.add(item);
                        }
                    }
                } else if(o instanceof ItemStack) {

                    if(input.size()>0) {
                        for (ItemStack ii : input) {
                            boolean find=false;
                            if (ii.isItemEqual((ItemStack) o)) {
                                ii.setCount(ii.getCount() + ((ItemStack) o).getCount());
                                find=true;
                            }
                            if(find==false){
                                input.add(((ItemStack) o));
                            }
                        }
                    }else{
                        input.add(((ItemStack) o));
                    }
                }
            }
            t.add(new ElvenTrade(input,recipe.getOutputs()));
        }
        trades = Collections.unmodifiableCollection(t);
    }

    @Override
    @SideOnly (Side.CLIENT)
    public TextureAtlasSprite getTexture(int side, int pass) {

        if (pass == 0) {
            if (side == 0) {
                return GTextures.elven_trader_side;
            } else if (side == 1) {
                return GTextures.elven_trader_top;
            }
            //return side != facing ? TETextures.MACHINE_SIDE : RenderHelper.getFluidTexture(FluidRegistry.LAVA);
            return side != facing ? GTextures.elven_trader_side : isActive ? GTextures.elven_portal : GTextures.elven_trader_face;
        } else if (side < 6) {
            return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? GTextures.elven_trader_active : GTextures.elven_trader_active;
        }
        return GTextures.elven_trader_side;
    }

    @Override
    protected int getNumAugmentSlots(int level) {
        return 0;
    }
}
