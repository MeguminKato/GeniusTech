package cn.tohsaka.factory.GeniusTech.containers;

import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileElvenTrader;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileManaConverter;
import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.element.ElementBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerElvenTrader extends ContainerTileAugmentable {
    TileElvenTrader myTile;

    public ContainerElvenTrader(InventoryPlayer player, TileElvenTrader tile)
    {
        super(player, tile);
        myTile = tile;
        int k=0;
        for(int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                addSlotToContainer( new Slot(myTile,k,8+i*18,17+j*18));
                k++;
            }
        }

        for(int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                addSlotToContainer( new Slot(myTile,k,94+i*18,17+j*18));
                k++;
            }
        }
    }

    @Override
    protected int getPlayerInventoryVerticalOffset()
    {
        return 84;
    }
}

