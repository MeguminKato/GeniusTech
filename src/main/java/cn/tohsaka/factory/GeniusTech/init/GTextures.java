package cn.tohsaka.factory.GeniusTech.init;

import cn.tohsaka.factory.GeniusTech.blocks.BlockTechMagic;
import cn.tohsaka.factory.GeniusTech.render.BakeryTechMagic;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GTextures {
    private static TextureMap textureMap;
    public static Map<Integer,TextureAtlasSprite> MACHINE_FACE = new HashMap<>();
    public static Map<Integer,TextureAtlasSprite> MACHINE_ACTIVE = new HashMap<>();
    public static Map<String,TextureAtlasSprite> mtm = new HashMap<>();
    public static TextureAtlasSprite elven_portal;
    public static TextureAtlasSprite elven_trader_top;
    public static TextureAtlasSprite elven_trader_side;
    public static TextureAtlasSprite elven_trader_face;
    public static TextureAtlasSprite elven_trader_active;
    private static TextureAtlasSprite register(String sprite)
    {
        TextureAtlasSprite t = textureMap.registerSprite(new ResourceLocation(MACHINE_+sprite));
        mtm.put(sprite,t);
        return t;
    }

    private static final String BLOCKS = "geniustech:blocks/";

    private static final String MACHINE_ = BLOCKS + "machine/machine_";

    public static TextureAtlasSprite getTextures(String machine_status){
        if(mtm.containsKey(machine_status)){
            return mtm.get(machine_status);
        }
        TextureAtlasSprite t = register(MACHINE_ + machine_status);
        mtm.put(machine_status,t);
        return t;
    }


    public static void registerTextures(TextureMap map)
    {
        textureMap = map;
        MACHINE_FACE.put(BlockTechMagic.Type.HEATGENERATOR.getMetadata(),register("heatgenerator_face"));
        MACHINE_ACTIVE.put(BlockTechMagic.Type.HEATGENERATOR.getMetadata(), register("heatgenerator_active"));

        MACHINE_FACE.put(BlockTechMagic.Type.TNTGENERATOR.getMetadata(),register("tntgenerator_face"));
        MACHINE_ACTIVE.put(BlockTechMagic.Type.TNTGENERATOR.getMetadata(), register("tntgenerator_active"));

        MACHINE_FACE.put(BlockTechMagic.Type.PUREDAISY.getMetadata(),register("puredaisy_face"));
        MACHINE_ACTIVE.put(BlockTechMagic.Type.PUREDAISY.getMetadata(), register("puredaisy_active"));

        MACHINE_FACE.put(BlockTechMagic.Type.GOURMARYLLIS.getMetadata(),register("gourmaryllis_face"));
        MACHINE_ACTIVE.put(BlockTechMagic.Type.GOURMARYLLIS.getMetadata(), register("gourmaryllis_active"));

        register("manaconverter_sides");
        register("manaconverter_top");


        elven_portal = textureMap.registerSprite(new ResourceLocation("botania:blocks/alfheim_portal_swirl"));
        elven_trader_top = textureMap.registerSprite(new ResourceLocation("botania:blocks/livingwood3"));
        elven_trader_side = textureMap.registerSprite(new ResourceLocation("botania:blocks/livingwood4"));
        elven_trader_face = register("elventrader_face");
        elven_trader_active = register("elventrader_active");
    }

}
