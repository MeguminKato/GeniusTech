package cn.tohsaka.factory.GeniusTech.tiles;

import cn.tohsaka.factory.GeniusTech.GeniusTech;
import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.init.GTextures;
import cofh.api.core.IAccelerable;
import cofh.api.item.IAugmentItem;
import cofh.api.item.IUpgradeItem;
import cofh.core.block.TilePowered;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.TimeTracker;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.redstoneflux.impl.EnergyStorage;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Arrays;
import java.util.HashSet;

public class TileTechMagicBase extends TilePowered implements IAccelerable, ITickable {
    public static final SideConfig[] SIDE_CONFIGS = new SideConfig[BlockTechMagic.Type.values().length];
    public static final SlotConfig[] SLOT_CONFIGS = new SlotConfig[BlockTechMagic.Type.values().length];
    public static final EnergyConfig[] ENERGY_CONFIGS = new EnergyConfig[BlockTechMagic.Type.values().length];
    public static final HashSet<String>[] VALID_AUGMENTS = new HashSet[BlockTechMagic.Type.values().length];
    public static final int MIN_BASE_POWER = 10;
    public static final int MAX_BASE_POWER = 200;
    public static final int[] POWER_SCALING = { 100, 150, 200, 250, 300 };
    public static int[] ENERGY_SCALING = { 100, 100, 100, 100, 100 };
    public static byte[] NUM_AUGMENTS = { 0, 1, 1, 1, 2 };
    protected static final int POWER_BASE = 100;
    protected static final int ENERGY_BASE = 100;
    protected static boolean enableSecurity = true;
    protected static boolean enableUpgrades = true;
    protected static boolean smallStorage = false;
    protected static final HashSet<String> VALID_AUGMENTS_BASE = new HashSet<>();
    static
    {
        VALID_AUGMENTS_BASE.add(TEProps.MACHINE_POWER);
    }
    public static void config()
    {

    }
    protected int processMax;
    protected int processRem;
    public EnergyConfig energyConfig;
    protected TimeTracker tracker = new TimeTracker();

    protected int energyMod = ENERGY_BASE;
    protected int reuseChance = 0;
    public TileTechMagicBase(){
        sideConfig = SIDE_CONFIGS[getType()];
        slotConfig = SLOT_CONFIGS[getType()];
        energyConfig = ENERGY_CONFIGS[getType()].copy();
        energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 4);
        Arrays.fill(augments, ItemStack.EMPTY);
        setDefaultSides();
    }
    @Override
    protected Object getMod()
    {
        return GeniusTech.INSTANCE;
    }

    @Override
    protected String getModVersion()
    {
        return GeniusTech.VERSION;
    }

    @Override
    public String getTileName()
    {
        return "tile.geniustech.blocktechmagic." + BlockTechMagic.Type.values()[getType()].getName() + ".name";
    }

    @Override
    protected int getLevelAutoInput()
    {
        return TEProps.levelAutoInput;
    }

    @Override
    protected int getLevelAutoOutput()
    {
        return TEProps.levelAutoOutput;
    }

    @Override
    protected int getLevelRSControl()
    {
        return TEProps.levelRedstoneControl;
    }

    @Override
    public int getLightValue()
    {
        return isActive ? 15 : 0;
    }

    @Override
    public boolean enableSecurity()
    {
        return enableSecurity;
    }

    @Override
    public boolean sendRedstoneUpdates()
    {
        return true;
    }







































    @Override
    public void onRedstoneUpdate()
    {
        boolean curActive = isActive;
        if (!redstoneControlOrDisable())
            isActive = false;

        updateIfChanged(curActive);
    }

    /* IUpgradeable */
    @Override
    public boolean canUpgrade(ItemStack upgrade)
    {
        if (!AugmentHelper.isUpgradeItem(upgrade))
            return false;

        IUpgradeItem.UpgradeType uType = ((IUpgradeItem) upgrade.getItem()).getUpgradeType(upgrade);
        int uLevel = ((IUpgradeItem) upgrade.getItem()).getUpgradeLevel(upgrade);

        switch (uType)
        {
            case INCREMENTAL:
                if (uLevel == level + 1)
                    return !BlockMachine.enableClassicRecipes;
                break;
            case FULL:
                if (uLevel > level)
                    return !BlockMachine.enableClassicRecipes;
                break;
            case CREATIVE:
                return false;
        }

        return false;
    }

    @Override
    public boolean smallStorage()
    {
        return smallStorage;
    }

    /* Actually a problem in the original code */
    @Override
    public boolean setLevel(int level)
    {
        if (super.setLevel(level))
        {
            if(energyConfig.maxEnergy>0){
                energyConfig.setDefaultParams(getBasePower(level), smallStorage);
                energyStorage.setCapacity(energyConfig.maxEnergy).setMaxTransfer(energyConfig.maxPower * 4);
            }
            return true;
        }

        return false;
    }

    @Override
    protected int getNumAugmentSlots(int level)
    {
        return NUM_AUGMENTS[MathHelper.clamp(level, CoreProps.LEVEL_MIN, CoreProps.LEVEL_MAX)];
    }


    @Override
    public void update()
    {
        boolean curActive = isActive;
        transferInput();
        //transferOutput();
        if (isActive)
        {
            processTick();

            if (canFinish())
            {
                processFinish();
                transferOutput();

                energyStorage.modifyEnergyStored(-processRem);

                if (!redstoneControlOrDisable() || !canStart())
                    processOff();
                else
                    processStart();
            }
            else if (energyStorage.getEnergyStored() <= 0)
            {
                processOff();
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
        chargeEnergy();
    }

    /* COMMON METHODS */
    protected int getBasePower(int level)
    {
        return ENERGY_CONFIGS[getType()].maxPower * POWER_SCALING[MathHelper.clamp(level, CoreProps.LEVEL_MIN, CoreProps.LEVEL_MAX)] / POWER_BASE;
    }

    protected int getBaseEnergy(int level)
    {
        return ENERGY_SCALING[MathHelper.clamp(level, CoreProps.LEVEL_MIN, CoreProps.LEVEL_MAX)] / ENERGY_BASE;
    }

    protected int calcEnergy()
    {
        if (energyStorage.getEnergyStored() >= energyConfig.maxPowerLevel)
            return energyConfig.maxPower;

        if (energyStorage.getEnergyStored() < energyConfig.minPowerLevel)
            return Math.min(energyConfig.minPower, energyStorage.getEnergyStored());

        return energyStorage.getEnergyStored() / energyConfig.energyRamp;
    }

    protected int getMaxInputSlot()
    {
        return 0;
    }

    protected boolean canStart()
    {
        return false;
    }

    protected boolean canFinish()
    {
        return processRem <= 0 && hasValidInput();
    }

    protected boolean hasValidInput()
    {
        return true;
    }

    protected void clearRecipe() {}

    protected void getRecipe() {}

    protected void processStart() {}

    protected void processFinish() {}

    protected void processOff()
    {
        processRem = 0;
        isActive = false;
        wasActive = true;

        if (world != null)
            tracker.markTime(world);
    }

    protected int processTick()
    {
        if (processRem <= 0)
            return 0;

        int energy = calcEnergy();
        energyStorage.modifyEnergyStored(-energy);
        processRem -= energy;

        return energy;
    }

    protected void transferInput() {}

    protected void transferOutput() {}

    protected void updateIfChanged(boolean curActive)
    {
        if (wasActive && tracker.hasDelayPassed(world, CoreProps.tileUpdateDelay))
        {
            wasActive = false;

        }
        sendTilePacket(Side.CLIENT);
    }

    /* GUI METHODS */
    @Override
    public int getScaledProgress(int scale)
    {
        if (!isActive || processMax <= 0 || processRem <= 0)
            return 0;

        return scale * (processMax - processRem) / processMax;
    }

    @Override
    public int getScaledSpeed(int scale)
    {
        if (!isActive)
            return 0;

        double power = energyStorage.getEnergyStored() / energyConfig.energyRamp;
        power = MathHelper.clip(power, energyConfig.minPower, energyConfig.maxPower);
        return MathHelper.round(scale * power / energyConfig.maxPower);
    }

    /* NBT METHODS */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        processMax = nbt.getInteger("ProcMax");
        processRem = nbt.getInteger("ProcRem");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setInteger("ProcMax", processMax);
        nbt.setInteger("ProcRem", processRem);
        return nbt;
    }

    /* NETWORK METHODS */

    /* SERVER -> CLIENT */
    @Override
    public PacketBase getGuiPacket() {
        PacketBase payload = super.getGuiPacket();

        payload.addInt(processMax);
        payload.addInt(processRem);

        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketBase payload)
    {
        super.handleGuiPacket(payload);

        processMax = payload.getInt();
        processRem = payload.getInt();
    }


    /* IAccelerable */
    @Override
    public int updateAccelerable()
    {
        return processTick();
    }

    /* IInventory */
    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        ItemStack stack = super.decrStackSize(slot, amount);

        if (ServerHelper.isServerWorld(world) && slot <= getMaxInputSlot())
            if (isActive && (inventory[slot].isEmpty() || !hasValidInput()))
                processOff();

        onInventoryChanged(slot, stack);

        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (ServerHelper.isServerWorld(world) && slot <= getMaxInputSlot()) {
            if (isActive && !inventory[slot].isEmpty()){
                if (stack.isEmpty() || !stack.isItemEqual(inventory[slot]) || !hasValidInput()){
                    processOff();
                }
            }
        }
        onInventoryChanged(slot, stack);

        super.setInventorySlotContents(slot, stack);
    }

    public void onInventoryChanged(int slot, ItemStack stack) {

    }

    @Override
    public void markDirty()
    {
        if (isActive && !hasValidInput())
            processOff();

        super.markDirty();
    }

    /* IEnergyInfo */
    @Override
    public int getInfoEnergyPerTick()
    {
        if (!isActive)
            return 0;

        return calcEnergy();
    }

    @Override
    public int getInfoMaxEnergyPerTick()
    {
        return energyConfig.maxPower;
    }

    /* IReconfigurableFacing */
    @Override
    public boolean setFacing(int side, boolean alternate)
    {
        if (side < 2 || side > 5)
            return false;

        sideCache[side] = 0;
        facing = (byte) side;
        markChunkDirty();
        sendTilePacket(Side.CLIENT);
        return true;
    }

    /* ISidedTexture */
    @Override
    public int getNumPasses()
    {
        return 2;
    }

    @Override
    public TextureAtlasSprite getTexture(int side, int pass)
    {
        if (pass == 0)
        {
            if (side == 0)
                return TETextures.MACHINE_BOTTOM;
            else if (side == 1)
                return TETextures.MACHINE_TOP;
            return side != facing ? TETextures.MACHINE_SIDE : isActive ? GTextures.getTextures(getRegisterName()+"_active") : GTextures.getTextures(getRegisterName()+"_face");
        }
        else if (side < 6)
        {
            return TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]];
        }

        return TETextures.MACHINE_SIDE;
    }



    /* RENDERING */
    public boolean hasFluidUnderlay()
    {
        return false;
    }

    public FluidStack getRenderFluid()
    {
        return null;
    }

    public int getColorMask(BlockRenderLayer layer, EnumFacing side)
    {
        return 0xFFFFFFFF;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing from) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, from);
    }
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing from) {
        int TYPE = this.getBlockMetadata();
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            IItemHandler inv = super.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null);
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new IItemHandler() {
                @Override
                public int getSlots() {
                    return inventory.length;
                }


                @Override
                public ItemStack getStackInSlot(int slot) {
                    return inventory[slot];
                }


                @Override
                public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                    if(!Arrays.stream(new int[]{1,5,6,7}).anyMatch( e -> e==sideCache[from.getIndex()])){
                        return ItemStack.EMPTY;
                    }
                    if(SLOT_CONFIGS[TYPE].allowInsertionSlot.length<slot){
                        return stack;
                    }
                    if(SLOT_CONFIGS[TYPE].allowInsertionSlot[slot]){
                        return inv.insertItem(slot,stack,simulate);
                    }
                    return stack;
                }


                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate) {
                    if(!Arrays.stream(new int[]{2,3,4,7}).anyMatch( e -> e==sideCache[from.getIndex()])){
                        return ItemStack.EMPTY;
                    }
                    if(SLOT_CONFIGS[TYPE].allowExtractionSlot.length<slot){
                        return ItemStack.EMPTY;
                    }
                    if(SLOT_CONFIGS[TYPE].allowExtractionSlot[slot]){
                        return inv.extractItem(slot,amount,simulate);
                    }
                    return ItemStack.EMPTY;
                }

                @Override
                public int getSlotLimit(int slot) {
                    return inv.getSlotLimit(slot);
                }
            });
        } else {
            return super.getCapability(capability, from);
        }
    }

    public String getRegisterName() {
        return "noname";
    }

    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
    }
}
