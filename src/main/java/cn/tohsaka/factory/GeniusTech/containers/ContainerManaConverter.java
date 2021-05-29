package cn.tohsaka.factory.GeniusTech.containers;

import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileHeatGenerator;
import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileManaConverter;
import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.SlotEnergy;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerManaConverter  extends ContainerTileAugmentable {
    TileManaConverter myTile;

    public ContainerManaConverter(InventoryPlayer player, TileManaConverter tile)
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

