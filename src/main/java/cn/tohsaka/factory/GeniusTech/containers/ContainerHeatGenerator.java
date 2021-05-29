package cn.tohsaka.factory.GeniusTech.containers;

import cn.tohsaka.factory.GeniusTech.tiles.techmagic.TileHeatGenerator;
import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerHeatGenerator extends ContainerTileAugmentable {
    TileHeatGenerator myTile;

    public ContainerHeatGenerator(InventoryPlayer player, TileHeatGenerator tile)
    {
        super(player, tile);

        myTile = tile;
        addSlotToContainer(new Slot(myTile,0,53,26));
        addSlotToContainer(new SlotEnergy(myTile,1,8,53));
    }

    @Override
    protected int getPlayerInventoryVerticalOffset()
    {
        return 84;
    }
}
