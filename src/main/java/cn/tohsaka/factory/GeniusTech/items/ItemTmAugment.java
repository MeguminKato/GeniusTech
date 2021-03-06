package cn.tohsaka.factory.GeniusTech.items;

import cn.tohsaka.factory.GeniusTech.GeniusTech;
import cofh.api.core.IAugmentable;
import cofh.api.core.ISecurable;
import cofh.api.item.IAugmentItem;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ChatHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.StringHelper;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;
import static cofh.core.util.helpers.RecipeHelper.addShapelessRecipe;

public class ItemTmAugment extends ItemMulti implements IInitializer, IAugmentItem
{
    private TIntObjectHashMap<AugmentEntry> augmentMap = new TIntObjectHashMap<>();

    public static ItemStack tm_speed;
    public static ItemStack tm_turbine;
    public ItemTmAugment()
    {
        super(GeniusTech.MOD_ID);

        setUnlocalizedName("augment_tm");
        setCreativeTab(GeniusTech.TAB_GENIUSTECH);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return StringHelper.localize("info.thermalexpansion.augment.0") + ": " + super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown())
            tooltip.add(StringHelper.shiftForDetails());

        if (!StringHelper.isShiftKeyDown())
            return;

        AugmentType type = getAugmentType(stack);
        String id = getAugmentIdentifier(stack);

        if (id.isEmpty())
            return;

        int i = 0;
        String line = "info.geniustech.augment_tm." + id + "." + i;
        while (StringHelper.canLocalize(line))
        {
            tooltip.add(StringHelper.localize(line));
            i++;
            line = "info.geniustech.augment_tm." + id + "." + i;
        }

        i = 0;
        line = "info.geniustech.augment_tm." + id + ".a." + i;
        while (StringHelper.canLocalize(line))
        {
            tooltip.add(StringHelper.BRIGHT_GREEN + StringHelper.localize(line));
            i++;
            line = "info.geniustech.augment_tm." + id + ".a." + i;
        }

        i = 0;
        line = "info.geniustech.augment_tm." + id + ".b." + i;
        while (StringHelper.canLocalize(line))
        {
            tooltip.add(StringHelper.RED + StringHelper.localize(line));
            i++;
            line = "info.geniustech.augment_tm." + id + ".b." + i;
        }

        i = 0;
        line = "info.geniustech.augment_tm." + id + ".c." + i;
        while (StringHelper.canLocalize(line))
        {
            tooltip.add(StringHelper.YELLOW + StringHelper.localize(line));
            i++;
            line = "info.geniustech.augment_tm." + id + ".c." + i;
        }


        switch (type)
        {
            case ADVANCED:
                // tooltip.add(StringHelper.getNoticeText("info.geniustech.augment_tm.noticeAdvanced"));
                break;
            case MODE:
                tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.augment.noticeMode"));
                break;
            case ENDER:
                tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.augment.noticeEnder"));
                break;
            case CREATIVE:
                tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.augment.noticeCreative"));
                break;
            default:
        }
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!block.hasTileEntity(state))
            return EnumActionResult.PASS;

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof ISecurable && !((ISecurable) tile).canPlayerAccess(player))
            return EnumActionResult.PASS;

        if (tile instanceof IAugmentable)
        {
            if (((IAugmentable) tile).getAugmentSlots().length <= 0)
                return EnumActionResult.PASS;

            if (ServerHelper.isServerWorld(world))
            { // Server
                if (((IAugmentable) tile).installAugment(stack))
                {
                    if (!player.capabilities.isCreativeMode)
                        stack.shrink(1);

                    player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.4F, 0.8F);
                    ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalfoundation.augment.install.success"));
                }
                else
                {
                    ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalfoundation.augment.install.failure"));
                }

                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.PASS;
    }

    /* IAugmentItem */
    @Override
    public AugmentType getAugmentType(ItemStack stack)
    {
        if (!augmentMap.containsKey(ItemHelper.getItemDamage(stack)))
            return AugmentType.CREATIVE;

        return augmentMap.get(ItemHelper.getItemDamage(stack)).type;
    }

    @Override
    public String getAugmentIdentifier(ItemStack stack)
    {
        if (!augmentMap.containsKey(ItemHelper.getItemDamage(stack)))
            return "";

        return augmentMap.get(ItemHelper.getItemDamage(stack)).identifier;
    }

    @Override
    public boolean preInit()
    {
        ForgeRegistries.ITEMS.register(setRegistryName("augment_tm"));
        GeniusTech.proxy.addIModelRegister(this);

        int metadata = 0;
        tm_speed = addAugmentItem(metadata++, "tm_speed", AugmentType.ADVANCED);
        tm_turbine = addAugmentItem(metadata++, "tm_turbine", AugmentType.ADVANCED);
        return true;
    }

    @Override
    public boolean initialize()
    {



        return true;
    }

    public class AugmentEntry
    {
        public final AugmentType type;
        public final String identifier;

        AugmentEntry(AugmentType type, String identifier)
        {
            this.type = type;
            this.identifier = identifier;
        }
    }

    private void addAugmentEntry(int metadata, AugmentType type, String identifier)
    {
        augmentMap.put(metadata, new AugmentEntry(type, identifier));
    }

    private ItemStack addAugmentItem(int metadata, String name)
    {
        addAugmentEntry(metadata, AugmentType.BASIC, name);
        return addItem(metadata, name);
    }

    private ItemStack addAugmentItem(int metadata, String name, EnumRarity rarity)
    {
        addAugmentEntry(metadata, AugmentType.BASIC, name);
        return addItem(metadata, name, rarity);
    }

    private ItemStack addAugmentItem(int metadata, String name, AugmentType type)
    {
        EnumRarity rarity;

        switch (type)
        {
            case ADVANCED:
            case MODE:
                rarity = EnumRarity.UNCOMMON;
                break;
            case ENDER:
                rarity = EnumRarity.RARE;
                break;
            case CREATIVE:
                rarity = EnumRarity.EPIC;
                break;
            default:
                rarity = EnumRarity.COMMON;
        }

        return addAugmentItem(metadata, name, type, rarity);
    }

    private ItemStack addAugmentItem(int metadata, String name, AugmentType type, EnumRarity rarity)
    {
        addAugmentEntry(metadata, type, name);
        return addItem(metadata, name, rarity);
    }
}