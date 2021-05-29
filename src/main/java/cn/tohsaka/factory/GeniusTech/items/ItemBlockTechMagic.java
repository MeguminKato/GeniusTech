package cn.tohsaka.factory.GeniusTech.items;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.tiles.TileTechMagicBase;
import cofh.api.tileentity.IRedstoneControl;
import cofh.core.block.BlockCore;
import cofh.core.util.helpers.*;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.block.tile.mana.TilePool;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockTechMagic extends ItemBlockTEBase
{
    public ItemBlockTechMagic(BlockCore block)
    {
        super(block);
    }

    @Override
    public ItemStack setDefaultTag(ItemStack stack, int level)
    {
        ReconfigurableHelper.setFacing(stack, 3);
        ReconfigurableHelper.setSideCache(stack, TileTechMagicBase.SIDE_CONFIGS[ItemHelper.getItemDamage(stack)].defaultSides);
        RedstoneControlHelper.setControl(stack, IRedstoneControl.ControlMode.DISABLED);
        EnergyHelper.setDefaultEnergyTag(stack, 0);
        stack.getTagCompound().setByte("Level", (byte) level);

        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        SecurityHelper.addOwnerInformation(stack, tooltip);

        /*if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown())
            tooltip.add(StringHelper.shiftForDetails());

        if (!StringHelper.isShiftKeyDown())
            return;
        */

        SecurityHelper.addAccessInformation(stack, tooltip);

        String name = StringHelper.getInfoText("info.geniustech.blocktechmagic." + BlockTechMagic.Type.values()[ItemHelper.getItemDamage(stack)].getName());
        //+StringHelper.getInfoText("info.geniustech.blocktechmagic." + name)

        tooltip.add(name);
    }


}
