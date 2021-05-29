package cn.tohsaka.factory.GeniusTech.gui.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerManaConverter;
import cn.tohsaka.factory.GeniusTech.containers.ContainerThermalily;
import cn.tohsaka.factory.GeniusTech.gui.techmagic.tab.TabMana;
import cn.tohsaka.factory.GeniusTech.init.GConstants;
import cn.tohsaka.factory.GeniusTech.init.Gblocks;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileManaConverter;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileThermalily;
import cofh.core.gui.container.IAugmentableContainer;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.gui.element.tab.TabAugment;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.common.block.ModBlocks;

public class GuiThermalily extends GuiPoweredBase {
    TileThermalily myTile;
    public static final String TEX_PATH = GConstants.PATH_MACHINE_GUI + "tm_thermalily.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
    public GuiThermalily(InventoryPlayer inventory, TileThermalily tile)
    {
        super(new ContainerThermalily(inventory, tile),tile, inventory.player, TEXTURE);

        generateInfo("MC");

        myTile = (TileThermalily) tile;
        ySize = 166;
    }
    private ElementFluidTank manaTank;
    private ElementFluidTank lavaTank;
    private ElementDualScaled progress;
    @Override
    public void initGui() {
        super.initGui();
        addTab(new TabMana(this, myTile, true));
        progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 75, 24).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
        manaTank = (ElementFluidTank) addElement(new ElementFluidTank(this,152,9,myTile.manaTank).setGauge(1).setAlwaysShow(true));
        manaTank.setVisible(true);
        lavaTank = (ElementFluidTank) addElement(new ElementFluidTank(this,8,9,myTile.lavaTank).setGauge(1).setAlwaysShow(true));
        lavaTank.setVisible(true);

        progress.setQuantity(0);
        augmentTab.setVisible(true);
        configTab.setVisible(true);
    }
    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();
        progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);

    }
}
