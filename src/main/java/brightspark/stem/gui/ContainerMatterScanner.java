package brightspark.stem.gui;

import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.tileentity.TileMatterScanner;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerMatterScanner extends ContainerMachineBase
{
    public ContainerMatterScanner(InventoryPlayer invPlayer, TileMatterScanner machine)
    {
        super(invPlayer, machine);
    }

    @Override
    protected void addSlots()
    {
        //Energy Input Slot
        //addSlotToContainer(new SlotEnergyInput(inventory, 26, 64));

        //Input Slot
        addSlotToContainer(new SlotLockable((TileMatterScanner) inventory, 44, 39));

        //Memory Chip Slot
        addSlotToContainer(new SlotLockable((TileMatterScanner) inventory, 110, 39)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() instanceof ItemMemoryChip && ItemMemoryChip.isMemoryEmpty(stack);
            }
        });
    }
}
