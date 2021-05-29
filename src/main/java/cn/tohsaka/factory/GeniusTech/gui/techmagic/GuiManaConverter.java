package cn.tohsaka.factory.GeniusTech.gui.techmagic;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.containers.ContainerHeatGenerator;
import cn.tohsaka.factory.GeniusTech.containers.ContainerManaConverter;
import cn.tohsaka.factory.GeniusTech.init.GConstants;
import cn.tohsaka.factory.GeniusTech.init.GFluid;
import cn.tohsaka.factory.GeniusTech.init.Gblocks;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileHeatGenerator;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileManaConverter;
import cofh.core.gui.element.*;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.common.block.ModBlocks;

public class GuiManaConverter extends GuiPoweredBase {
    TileManaConverter myTile;
    public static final String TEX_PATH = GConstants.PATH_MACHINE_GUI + "tm_manaconverter.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
    public GuiManaConverter(InventoryPlayer inventory, TileManaConverter tile)
    {
        super(new ContainerManaConverter(inventory, tile),tile, inventory.player, TEXTURE);

        generateInfo("MC");

        myTile = (TileManaConverter) tile;
        ySize = 166;
    }
    private ElementFluidTank manaTank;
    private ElementDualScaled progress;
    @Override
    public void initGui() {
        super.initGui();
        progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 75, 24).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
        manaTank = (ElementFluidTank) addElement(new ElementFluidTank(this,152,9,myTile.banaFluidTank).setGauge(1).setAlwaysShow(true));
        manaTank.setVisible(true);
        progress.setQuantity(0);
        augmentTab.setVisible(false);
        configTab.setVisible(false);
    }
    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();
        if(myTile.isActive){
            progress.setQuantity(24);
        }else{
            progress.setQuantity(0);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack pool = new ItemStack(ModBlocks.pool, 1, 1);
        ItemStack converter = new ItemStack(Gblocks.blockTechMagic, 1, BlockTechMagic.Type.MANACONVERTER.getMetadata());
        RenderHelper.enableGUIStandardItemLighting();

        mc.getRenderItem().renderItemAndEffectIntoGUI(converter, 45, 24);
        mc.getRenderItem().renderItemAndEffectIntoGUI(pool, 115, 24);

        mc.fontRenderer.drawString(StringHelper.localize("status.geniustech.blocktechmagic.mana_converter."+(myTile.isActive?"active":"deactive")),74,60,0x000000);

        RenderHelper.disableStandardItemLighting();
    }
}
