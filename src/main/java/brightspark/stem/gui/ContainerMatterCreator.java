package brightspark.stem.gui;

import brightspark.stem.tileentity.StemTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerMatterCreator extends ContainerMachineBase
{
    public ContainerMatterCreator(InventoryPlayer invPlayer, StemTileEntity machine)
    {
        super(invPlayer, machine);
    }

    @Override
    protected void addSlots()
    {
        //Slot 0 -> Energy Input Stack
        //Slot 1 -> Fluid Input Stack
        //Slot 2 -> Bucket Output Stack
        //Slot 3 -> Memory Chip Slot
        //Slot 4 -> Item Creation Output


    }
}
