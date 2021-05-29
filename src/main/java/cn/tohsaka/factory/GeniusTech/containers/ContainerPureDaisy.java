package cn.tohsaka.factory.GeniusTech.containers;

import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileManaConverter;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TilePureDaisy;
import cofh.core.gui.container.ContainerTileAugmentable;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerPureDaisy   extends ContainerTileAugmentable {
    TilePureDaisy myTile;

    public ContainerPureDaisy(InventoryPlayer player, TilePureDaisy tile)
    {
        super(player, tile);
        myTile = tile;
        addSlotToContainer(new Slot(myTile,0,44,28));
        addSlotToContainer(new Slot(myTile,1,116,28));
    }

    @Override
    protected int getPlayerInventoryVerticalOffset()
    {
        return 84;
    }
}
