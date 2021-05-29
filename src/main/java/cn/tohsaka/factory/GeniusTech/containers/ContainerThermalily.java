package cn.tohsaka.factory.GeniusTech.containers;

import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileManaConverter;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileThermalily;
import cofh.core.gui.container.ContainerTileAugmentable;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerThermalily extends ContainerTileAugmentable {
    TileThermalily myTile;

    public ContainerThermalily(InventoryPlayer player, TileThermalily tile)
    {
        super(player, tile);
        myTile = tile;
        //addSlotToContainer(new SlotEnergy(myTile,0,8,53));
    }

    @Override
    protected int getPlayerInventoryVerticalOffset()
    {
        return 84;
    }
}

