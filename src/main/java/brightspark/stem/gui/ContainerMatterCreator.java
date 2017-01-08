package brightspark.stem.gui;

import brightspark.stem.tileentity.StemTileEntity;
import brightspark.stem.util.CommonUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

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

        //Energy input slot
        addSlotToContainer(new SlotEnergyInput(inventory, 8, 64));

        //Fluid bucket input slot
        addSlotToContainer(new SlotMachine(inventory, 62, 23)
        {
            @Override
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return CommonUtils.isStemBucket(stack);
            }
        });

        //TODO: Finish adding slots
    }
}
