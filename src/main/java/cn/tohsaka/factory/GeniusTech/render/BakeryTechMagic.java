package cn.tohsaka.factory.GeniusTech.render;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.init.GConstants;
import cn.tohsaka.factory.GeniusTech.init.Gblocks;
import cn.tohsaka.factory.GeniusTech.tiles.TileTechMagicBase;
import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.IItemBlockTextureProvider;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.render.CubeBakeryBase;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class BakeryTechMagic extends CubeBakeryBase
{
    public static BakeryTechMagic INSTANCE = new BakeryTechMagic();

    /**
     * Used to get the overlay texture for the given side.
     * This should specifically relate to the level of the machine and not its state.
     *
     * @param face  The face.
     * @param level The level.
     * @return The texture, Null if there is no texture for the face.
     */
    private static TextureAtlasSprite getOverlaySprite(EnumFacing face, int level)
    {
        if (level == 0)
        {
            return null;
        }

        return TETextures.MACHINE_OVERLAY[level];
    }

    /* IBlockBakery */
    @Override
    public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileTechMagicBase machineBase = (TileTechMagicBase) world.getTileEntity(pos);

        if (machineBase == null)
            return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ModelErrorStateProperty.ErrorState.of("Null tile. Position: %s", pos));

        state = state.withProperty(ModelErrorStateProperty.ERROR_STATE, ModelErrorStateProperty.ErrorState.OK);
        state = state.withProperty(GConstants.TILE_TECHMAGIC, machineBase);
        return state;
    }

    /* IItemBakery */
    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack)
    {
        List<BakedQuad> quads = new ArrayList<>();

        if (face != null && !stack.isEmpty())
        {
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            CCRenderState ccrs = CCRenderState.instance();
            buffer.begin(0x07, DefaultVertexFormats.ITEM);
            ccrs.reset();
            ccrs.bind(buffer);

            boolean creative = BlockTechMagic.itemBlock.isCreative(stack);
            int level = BlockTechMagic.itemBlock.getLevel(stack);
            IItemBlockTextureProvider provider = Gblocks.blockTechMagic;
            renderFace(ccrs, face, provider.getTexture(face, stack), 0xFFFFFFFF);

            if (level > 0)
                renderFaceOverlay(ccrs, face, creative ? TETextures.MACHINE_OVERLAY_C : getOverlaySprite(face, level), 0xFFFFFFFF);

            buffer.finishDrawing();
            quads.addAll(buffer.bake());
        }

        return quads;
    }

    /* ILayeredBlockBakery */
    @Override
    public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state)
    {
        List<BakedQuad> quads = new ArrayList<>();

        if (face != null && state != null)
        {
            Block block = state.getBlock();
            IWorldBlockTextureProvider provider = (IWorldBlockTextureProvider) block;
            TileTechMagicBase tile = state.getValue(GConstants.TILE_TECHMAGIC);

            boolean creative = tile.isCreative;
            int level = tile.getLevel();

            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(0x07, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            renderFace(ccrs, face, provider.getTexture(face, state, layer, tile.getWorld(), tile.getPos()), tile.getColorMask(layer, face));

            if (layer == BlockRenderLayer.CUTOUT && level > 0)
                renderFace(ccrs, face, creative ? TETextures.MACHINE_OVERLAY_C : getOverlaySprite(face, level), 0xFFFFFFFF);

            buffer.finishDrawing();
            quads.addAll(buffer.bake());
        }

        return quads;
    }
}
