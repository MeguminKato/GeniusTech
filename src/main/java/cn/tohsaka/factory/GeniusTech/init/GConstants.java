package cn.tohsaka.factory.GeniusTech.init;

import cn.tohsaka.factory.GeniusTech.tiles.TileTechMagicBase;
import codechicken.lib.block.property.unlisted.UnlistedGenericTile;
import net.minecraft.util.ResourceLocation;

public class GConstants {
    public static final UnlistedGenericTile<TileTechMagicBase> TILE_TECHMAGIC = new UnlistedGenericTile<>("tile_techmagic", TileTechMagicBase.class);
    public static final String PATH_GFX = "geniustech:textures/";
    public static final String PATH_GUI = PATH_GFX + "gui/";
    public static final String PATH_MACHINE_GUI = PATH_GUI + "machine/";
}
