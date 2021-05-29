package cn.tohsaka.factory.GeniusTech.gui.techmagic.tab;

import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileHeatGenerator;
import cofh.api.tileentity.IEnergyInfo;
import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.tab.TabBase;
import cofh.core.gui.element.tab.TabEnergy;
import cofh.core.init.CoreTextures;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.block.ModBlocks;

import java.awt.*;
import java.util.List;

public class TabMana extends TabBase {

    public static int defaultSide = 0;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0x000000;
    public static int defaultBackgroundColorOut = 0x00ffff;
    public static int defaultBackgroundColorIn = 0x0a76d0;

    private TileHeatGenerator myContainer;
    private boolean isProducer;
    private boolean displayMax = true;
    private boolean displayStored = true;

    static final String UNIT_INSTANT = " mB";
    static final String UNIT_TIME = " tick";

    public TabMana(GuiContainerCore gui, TileHeatGenerator container, boolean isProducer) {

        this(gui, defaultSide, container, isProducer);
    }

    public TabMana(GuiContainerCore gui, int side, TileHeatGenerator container, boolean producer) {

        super(gui, side);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = producer ? defaultBackgroundColorOut : defaultBackgroundColorIn;

        maxHeight = 92;
        maxWidth = 100;
        myContainer = container;
        isProducer = producer;
    }

    public TabMana isProducer(boolean isProducer) {

        this.isProducer = isProducer;
        return this;
    }

    public TabMana displayMax(boolean displayMax) {

        this.displayMax = displayMax;
        return this;
    }

    public TabMana displayStored(boolean displayStored) {

        this.displayStored = displayStored;
        return this;
    }

    @Override
    protected void drawForeground() {

        ItemStack pool = new ItemStack(ModBlocks.pool, 1, 1);
        RenderHelper.enableGUIStandardItemLighting();
        gui.drawItemStack(pool,sideOffset(),1,false,"");
        RenderHelper.disableStandardItemLighting();

        if (!isFullyOpened()) {
            return;
        }
        String flowDirection = isProducer ? "info.geniustech.tm.tab.energyProduce" : "info.geniustech.tm.tab.energyConsume";

        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.geniustech.tm.mana"), sideOffset() + 20, 6, headerColor);//headerColor
        getFontRenderer().drawStringWithShadow(StringHelper.localize(flowDirection) + ":", sideOffset() + 6, 18, subheaderColor);
        getFontRenderer().drawString(myContainer.mana2fill + UNIT_INSTANT, sideOffset() + 14, 30, textColor);

        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.geniustech.tm.tab.processtime") + ":", sideOffset() + 6, 42, subheaderColor);
        getFontRenderer().drawString(myContainer.getMaxProcessTime() + UNIT_TIME, sideOffset() + 14, 54, textColor);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
    @Override
    public void addTooltip(List<String> list) {

        if (!isFullyOpened()) {
            list.add(myContainer.mana2fill + UNIT_INSTANT);
        }
    }
}
