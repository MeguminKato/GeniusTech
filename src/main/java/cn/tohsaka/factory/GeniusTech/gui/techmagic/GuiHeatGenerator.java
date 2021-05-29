package cn.tohsaka.factory.GeniusTech.gui.techmagic;

import cn.tohsaka.factory.GeniusTech.containers.ContainerHeatGenerator;
import cn.tohsaka.factory.GeniusTech.init.GConstants;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileHeatGenerator;
import cofh.core.gui.element.*;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiHeatGenerator extends GuiPoweredBase {
    TileHeatGenerator myTile;
    public static final String TEX_PATH = GConstants.PATH_MACHINE_GUI + "tm_heatgenerator.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
    public GuiHeatGenerator(InventoryPlayer inventory, TileHeatGenerator tile)
    {
        super(new ContainerHeatGenerator(inventory, tile),tile, inventory.player, TEXTURE);

        generateInfo("HG");

        myTile = (TileHeatGenerator) tile;
        ySize = 164;
    }
    private ElementFluidTank manaTank;
    private ElementFluid progressFluid;
    private ElementDualScaled progressOverlay;
    @Override
    public void initGui() {
        super.initGui();
        addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));

        manaTank = (ElementFluidTank) addElement(new ElementFluidTank(this,152,9,myTile.manaTank).setGauge(1).setAlwaysShow(true));
        manaTank.setVisible(true);
        progressFluid = (ElementFluid) addElement(new ElementFluid(this, 103, 34).setFluid(new FluidStack(GFluid.fluidBana,1)).setSize(24, 16));
        progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 103, 34).setMode(1).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));

    }
    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();
        progressFluid.setSize(baseTile.getScaledProgress(PROGRESS), 16);
        progressOverlay.setQuantity(baseTile.getScaledProgress(PROGRESS));
    }

}
