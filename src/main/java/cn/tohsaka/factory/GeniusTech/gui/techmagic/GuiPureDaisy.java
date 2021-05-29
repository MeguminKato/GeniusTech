package cn.tohsaka.factory.GeniusTech.gui.techmagic;

import cn.tohsaka.factory.GeniusTech.containers.ContainerHeatGenerator;
import cn.tohsaka.factory.GeniusTech.containers.ContainerPureDaisy;
import cn.tohsaka.factory.GeniusTech.init.GConstants;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileHeatGenerator;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TilePureDaisy;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluid;
import cofh.core.gui.element.ElementFluidTank;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GuiPureDaisy extends GuiPoweredBase {
    TilePureDaisy myTile;
    public static final String TEX_PATH = GConstants.PATH_MACHINE_GUI + "tm_puredaisy.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
    public GuiPureDaisy(InventoryPlayer inventory, TilePureDaisy tile)
    {
        super(new ContainerPureDaisy(inventory, tile),tile, inventory.player, TEXTURE);

        generateInfo("HG");

        myTile = (TilePureDaisy) tile;
        ySize = 164;
    }
    private ElementFluidTank manaTank;
    private ElementDualScaled progress;
    @Override
    public void initGui() {
        super.initGui();
        progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 75, 28).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
        manaTank = (ElementFluidTank) addElement(new ElementFluidTank(this,152,9,myTile.manaTank).setGauge(1).setAlwaysShow(true));
        manaTank.setVisible(true);
        augmentTab.setVisible(false);
    }
    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();
        progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
    }
}
