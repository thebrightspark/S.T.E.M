package brightspark.stem.gui;

import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.tileentity.TileMatterScanner;
import cofh.api.energy.IEnergyProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
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
        addSlotToContainer(new Slot(inventory, slotI++, 26, 64)
        {
            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }

            @Override
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return stack != null && stack.getItem() instanceof IEnergyProvider;
            }
        });

        //Input Slot
        addSlotToContainer(new SlotLockable((TileMatterScanner) inventory, slotI++, 66, 35));

        //Memory Chip Slot
        addSlotToContainer(new SlotLockable((TileMatterScanner) inventory, slotI++, 132, 35)
        {
            @Override
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return stack != null && stack.getItem() instanceof ItemMemoryChip && ItemMemoryChip.isMemoryEmpty(stack);
            }
        });
    }

    private class SlotLockable extends Slot
    {
        private TileMatterScanner machine;

        public SlotLockable(TileMatterScanner matterScanner, int index, int xPosition, int yPosition)
        {
            super(matterScanner, index, xPosition, yPosition);
            machine = matterScanner;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            return !machine.isScanning();
        }

        @Override
        public int getSlotStackLimit()
        {
            return 1;
        }
    }
}
