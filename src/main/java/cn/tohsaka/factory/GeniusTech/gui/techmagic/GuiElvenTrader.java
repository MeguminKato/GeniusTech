package cn.tohsaka.factory.GeniusTech.gui.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerElvenTrader;
import cn.tohsaka.factory.GeniusTech.containers.ContainerManaConverter;
import cn.tohsaka.factory.GeniusTech.init.GConstants;
import cn.tohsaka.factory.GeniusTech.init.Gblocks;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileElvenTrader;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileManaConverter;
import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.common.block.ModBlocks;

import java.util.Arrays;

public class GuiElvenTrader extends GuiPoweredBase {
    TileElvenTrader myTile;
    public static final String TEX_PATH = GConstants.PATH_MACHINE_GUI + "tm_elventrader.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
    public GuiElvenTrader(InventoryPlayer inventory, TileElvenTrader tile)
    {
        super(new ContainerElvenTrader(inventory, tile),tile, inventory.player, TEXTURE);

        generateInfo("MC");

        myTile = (TileElvenTrader) tile;
        ySize = 166;
    }
    private ElementFluidTank manaTank;
    private ElementDualScaled progress;
    @Override
    public void initGui() {
        super.initGui();
        progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 65, 30).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
        progress.setQuantity(24);
        manaTank = (ElementFluidTank) addElement(new ElementFluidTank(this,152,9,myTile.banaFluidTank).setGauge(1).setAlwaysShow(true));
        manaTank.setVisible(true);


    }
    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();
        redstoneTab.setVisible(false);
        augmentTab.setVisible(false);
    }

}
