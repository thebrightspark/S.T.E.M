package brightspark.stem.gui;

import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.tileentity.TileMatterCreator;
import brightspark.stem.util.CommonUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerMatterCreator extends ContainerMachineBase<TileMatterCreator>
{
    public ContainerMatterCreator(InventoryPlayer invPlayer, TileMatterCreator machine)
    {
        super(invPlayer, machine);
    }

    @Override
    protected void addSlots()
    {
        //Fluid bucket input slot
        addSlotToContainer(new SlotMachine(inventory, 37, 23)
        {
            @Override
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return CommonUtils.isStemBucket(stack);
            }
        });

        //Bucket output slot
        addSlotToContainer(new SlotOutputOnly(inventory, 37, 54));

        //Memory chip slot
        addSlotToContainer(new SlotChip(inventory, 79, 39));

        //Item output slot
        addSlotToContainer(new SlotOutputOnly(inventory, 133, 39));
    }

    public class SlotChip extends SlotMachine
    {
        private TileMatterCreator machine;

        public SlotChip(TileMatterCreator machine, int xPosition, int yPosition)
        {
            super(machine, xPosition, yPosition);
            this.machine = machine;
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack)
        {
            return stack != null && stack.getItem() instanceof ItemMemoryChip && !ItemMemoryChip.isMemoryEmpty(stack);
        }

        @Override
        public int getSlotStackLimit()
        {
            return 1;
        }

        @Override
        public ItemStack onTake(EntityPlayer playerIn, ItemStack stack)
        {
            stack = super.onTake(playerIn, stack);
            //Stop creation and set energy to 0
            machine.stopCreation();
            return stack;
        }
    }
}
