package cn.tohsaka.factory.GeniusTech.blocks;

import cn.tohsaka.factory.GeniusTech.GeniusTech;
import cn.tohsaka.factory.GeniusTech.init.GConstants;
import cn.tohsaka.factory.GeniusTech.init.GTextures;
import cn.tohsaka.factory.GeniusTech.items.ItemBlockTechMagic;
import cn.tohsaka.factory.GeniusTech.render.BakeryTechMagic;
import cn.tohsaka.factory.GeniusTech.tiles.TileTechMagicBase;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.*;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.core.init.CoreProps;
import cofh.core.render.IModelRegister;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ReconfigurableHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.storage.BlockTank;
import cofh.thermalexpansion.init.TEBlocks;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.item.ItemAugment;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalfoundation.item.ItemFertilizer;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.block.BlockFloatingSpecialFlower;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.subtile.generating.SubTileEndoflame;
import vazkii.botania.common.item.block.ItemBlockFloatingSpecialFlower;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;
import static vazkii.botania.common.lib.LibBlockNames.*;

public class BlockTechMagic extends BlockTEBase implements IModelRegister, IBakeryProvider, IWorldBlockTextureProvider, IWandHUD, IWandable {
    public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("type", Type.class);
    public static ItemBlockTechMagic itemBlock;
    public static ItemStack heat_generator;
    public static ItemStack mana_converter;
    public static ItemStack tnt_generator;
    public static ItemStack puredaisy;
    public static ItemStack gourmaryllis;
    public static ItemStack elventrader;
    public static ItemStack thermalily;

    public enum Type implements IStringSerializable
    {
        HEATGENERATOR(0, "heat_generator"),
        MANACONVERTER(1,"mana_converter"),
        TNTGENERATOR(2,"tnt_generator"),
        PUREDAISY(3,"puredaisy"),
        GOURMARYLLIS(4,"gourmaryllis"),
        THERMALILY(5,"thermalily"),
        ELVENTRADER(6,"elventrader");
        private final int metadata;
        private final String name;

        Type(int metadata, String name)
        {
            this.metadata = metadata;
            this.name = name;
        }

        public int getMetadata()
        {
            return this.metadata;
        }

        @Override
        public String getName()
        {
            return this.name;
        }
    }


    public BlockTechMagic(){
        super(Material.ROCK);
        modName = GeniusTech.MOD_ID;
        setUnlocalizedName("techmagic");
        setCreativeTab(GeniusTech.TAB_GENIUSTECH);
        setHardness(15.0F);
        setResistance(25.0F);
        setDefaultState(getBlockState().getBaseState().withProperty(VARIANT, Type.HEATGENERATOR));
    }
    @Override
    protected BlockStateContainer createBlockState()
    {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        // Listed
        builder.add(VARIANT);
        // UnListed
        builder.add(ModelErrorStateProperty.ERROR_STATE);
        builder.add(GConstants.TILE_TECHMAGIC);
        builder.add(TEProps.BAKERY_WORLD);

        return builder.build();
    }
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        for (int i = 0; i < Type.values().length; i++)
        {
            if (TEProps.creativeTabShowAllBlockLevels)
            {
                for (int j = 0; j <= CoreProps.LEVEL_MAX; j++)
                {
                    items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, i), j));
                }
            }
            else
            {
                items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, i), TEProps.creativeTabLevel));
            }

            if (TEProps.creativeTabShowCreative)
            {
                items.add(itemBlock.setCreativeTag(new ItemStack(this, 1, i)));
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return "tile.geniustech.blocktechmagic." + Type.values()[ItemHelper.getItemDamage(stack)].getName() + ".name";
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(VARIANT, Type.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        int meta = state.getBlock().getMetaFromState(state);
        if (meta >= Type.values().length)
            return null;

        switch (Type.values()[meta])
        {
            case HEATGENERATOR:
                return new TileHeatGenerator();
            case MANACONVERTER:
                return new TileManaConverter();
            case TNTGENERATOR:
                return new TileTNTGenerator();
            case PUREDAISY:
                return new TilePureDaisy();
            case GOURMARYLLIS:
                return new TileGourmaryllis();
            case THERMALILY:
                return new TileThermalily();
            case ELVENTRADER:
                return new TileElvenTrader();
            default:
                return null;
        }
    }

    /* BLOCK METHODS */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack)
    {
        if (stack.getTagCompound() != null)
        {
            TileTechMagicBase tile = (TileTechMagicBase) world.getTileEntity(pos);

            tile.setLevel(stack.getTagCompound().getByte("Level"));
            tile.readAugmentsFromNBT(stack.getTagCompound());
            tile.updateAugmentStatus();
            tile.setEnergyStored(stack.getTagCompound().getInteger("Energy"));

            int facing = BlockHelper.determineXZPlaceFacing(living);
            int storedFacing = ReconfigurableHelper.getFacing(stack);
            byte[] sideCache = ReconfigurableHelper.getSideCache(stack, tile.getDefaultSides());

            tile.sideCache[0] = sideCache[0];
            tile.sideCache[1] = sideCache[1];
            tile.sideCache[facing] = 0;
            tile.sideCache[BlockHelper.getLeftSide(facing)] = sideCache[BlockHelper.getLeftSide(storedFacing)];
            tile.sideCache[BlockHelper.getRightSide(facing)] = sideCache[BlockHelper.getRightSide(storedFacing)];
            tile.sideCache[BlockHelper.getOppositeSide(facing)] = sideCache[BlockHelper.getOppositeSide(storedFacing)];
        }

        super.onBlockPlacedBy(world, pos, state, living, stack);
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    public boolean onBlockActivatedDelegate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileTechMagicBase tile = (TileTechMagicBase) world.getTileEntity(pos);

        if (tile == null || !tile.canPlayerAccess(player))
            return false;

        if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            ItemStack heldItem = player.getHeldItem(hand);
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

            if (FluidHelper.isFluidHandler(heldItem))
            {
                FluidHelper.drainItemToHandler(heldItem, handler, player, hand);
                return true;
            }
        }

        return false;
    }

    /* RENDERING METHODS */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return ModelBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world, pos);
    }

    /* IBakeryProvider */
    @Override
    public IBakery getBakery()
    {
        return BakeryTechMagic.INSTANCE;
    }

    /* IWorldBlockTextureProvider */
    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTexture(EnumFacing side, ItemStack stack)
    {
        if(stack.getMetadata() == Type.ELVENTRADER.getMetadata()){
            switch (side){
                case UP:
                    return GTextures.elven_trader_top;
                case NORTH:
                    return GTextures.elven_trader_face;
                default:
                    return GTextures.elven_trader_side;
            }
        }

        if(stack.getMetadata()==Type.MANACONVERTER.getMetadata()){
            switch (side){
                case UP:
                    return GTextures.getTextures("manaconverter_top");
                default:
                    return GTextures.getTextures("manaconverter_sides");
            }
        }
        if (side == EnumFacing.DOWN){
            return TETextures.MACHINE_BOTTOM;
        }
        else if (side == EnumFacing.UP) {
            return TETextures.MACHINE_TOP;
        }
        return side != EnumFacing.NORTH ? TETextures.MACHINE_SIDE : GTextures.MACHINE_FACE.get(stack.getMetadata());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileTechMagicBase)
        {
            TileTechMagicBase tile = (TileTechMagicBase) tileEntity;

            TextureAtlasSprite texture = tile.getTexture(side.ordinal(), layer == BlockRenderLayer.SOLID ? 0 : 1);

            for (int i = 0; i < TETextures.MACHINE_ACTIVE.length; i++)
                if (texture == TETextures.MACHINE_ACTIVE[i])
                    return GTextures.MACHINE_ACTIVE.get(i);

            for (int i = 0; i < TETextures.MACHINE_FACE.length; i++)
                if (texture == TETextures.MACHINE_FACE[i])
                    return GTextures.MACHINE_FACE.get(i);

            return texture;
        }

        return TextureUtils.getMissingSprite();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels()
    {
        StateMap.Builder stateMap = new StateMap.Builder();
        stateMap.ignore(VARIANT);
        ModelLoader.setCustomStateMapper(this, stateMap.build());

        ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "normal");
        for (Type type : Type.values())
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMetadata(), location);

        ModelRegistryHelper.register(location, new CCBakeryModel());

        ModelBakery.registerBlockKeyGenerator(this, state -> {
            TileTechMagicBase tile = state.getValue(GConstants.TILE_TECHMAGIC);
            StringBuilder builder = new StringBuilder(state.getBlock().getRegistryName() + "|" + state.getBlock().getMetaFromState(state));
            builder.append(",creative=").append(tile.isCreative);
            builder.append(",level=").append(tile.getLevel());
            builder.append(",facing=").append(tile.getFacing());
            builder.append(",active=").append(tile.isActive);
            builder.append(",side_config={");

            for (int i : tile.sideCache)
                builder.append(",").append(i);

            builder.append("}");

            if (tile.hasFluidUnderlay() && tile.isActive)
            {
                FluidStack stack = tile.getRenderFluid();
                builder.append(",fluid=").append(stack != null ? FluidHelper.getFluidHash(stack) : tile.getTexture(tile.getFacing(), 0).getIconName());
            }

            return builder.toString();
        });

        ModelBakery.registerItemKeyGenerator(itemBlock, stack -> ModelBakery.defaultItemKeyGenerator.generateKey(stack) + ",creative=" + itemBlock.isCreative(stack) + ",level=" + itemBlock.getLevel(stack));
    }

    @Override
    public boolean preInit()
    {
        setRegistryName("geniustech_blocktechmagic");
        ForgeRegistries.BLOCKS.register(this);

        itemBlock = new ItemBlockTechMagic(this);
        itemBlock.setRegistryName(getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);

        TileTechMagicBase.config();

        TileHeatGenerator.init();
        TileManaConverter.init();
        TileTNTGenerator.init();
        TilePureDaisy.init();
        TileGourmaryllis.init();
        TileThermalily.init();
        TileElvenTrader.init();
        GeniusTech.proxy.addIModelRegister(this);

        return true;
    }

    @Override
    public boolean initialize()
    {
        heat_generator = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.HEATGENERATOR.getMetadata()));
        tnt_generator = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.TNTGENERATOR.getMetadata()));
        mana_converter = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.MANACONVERTER.getMetadata()));
        puredaisy = itemBlock.setDefaultTag(new ItemStack(this,1,Type.PUREDAISY.getMetadata()));
        thermalily = itemBlock.setDefaultTag(new ItemStack(this,1,Type.THERMALILY.getMetadata()));
        elventrader = itemBlock.setDefaultTag(new ItemStack(this,1,Type.ELVENTRADER.getMetadata()));
        addRecipes();
        addUpgradeRecipes();
        addClassicRecipes();

        return true;
    }

    private void addRecipes()
    {
        addShapedRecipe(heat_generator,
                "NTN",
                "FIF",
                "NVN",
                'N', "ingotTerrasteel",
                'T', BlockTank.itemBlock,
                'F', new ItemStack(ModBlocks.pylon,1,0),
                'I', ItemBlockSpecialFlower.ofType(new ItemStack(ModBlocks.floatingSpecialFlower), SUBTILE_ENDOFLAME),
                'V', ItemFrame.frameCell0
        );
        addShapedRecipe(tnt_generator,
                "NTN",
                "FIF",
                "NVN",
                'N', Blocks.OBSIDIAN,
                'T', BlockTank.itemBlock,
                'F', new ItemStack(ModBlocks.pylon,1,1),
                'I', ItemBlockSpecialFlower.ofType(new ItemStack(ModBlocks.floatingSpecialFlower), SUBTILE_ENTROPINNYUM),
                'V', ItemFrame.frameCell0
        );

        addShapedRecipe(mana_converter,
                "NTN",
                "FIF",
                "NVN",
                'N', "ingotTerrasteel",
                'T', ModBlocks.manaDetector,
                'F', ModBlocks.distributor,
                'I', ItemFrame.frameDevice,
                'V', BlockTank.itemBlock
        );
    }
    private void addUpgradeRecipes()
    {}
    private void addClassicRecipes()
    {}


    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
        ((TileTechMagicBase) world.getTileEntity(pos)).renderHUD(mc, res);
    }

    @Override
    public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        return false;
    }

}
