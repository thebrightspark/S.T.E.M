package brightspark.stem.gui;

import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.tileentity.TileMatterScanner;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerMatterScanner extends ContainerMachineBase<TileMatterScanner>
{
    public ContainerMatterScanner(InventoryPlayer invPlayer, TileMatterScanner machine)
    {
        super(invPlayer, machine);
    }

    @Override
    protected void addSlots()
    {
        //Input Slot
        addSlotToContainer(new SlotLockable(inventory, 44, 39));

        //Memory Chip Slot
        addSlotToContainer(new SlotLockable(inventory, 110, 39)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() instanceof ItemMemoryChip && ItemMemoryChip.isMemoryEmpty(stack);
            }
        });
    }
}
