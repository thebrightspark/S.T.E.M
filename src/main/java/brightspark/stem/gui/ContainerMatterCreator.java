package brightspark.stem.gui;

import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.tileentity.TileMatterCreator;
import brightspark.stem.util.CommonUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerMatterCreator extends ContainerMachineBase
{
    public ContainerMatterCreator(InventoryPlayer invPlayer, TileMatterCreator machine)
    {
        super(invPlayer, machine);
    }

    @Override
    protected void addSlots()
    {
        //Energy input slot
        //addSlotToContainer(new SlotEnergyInput(inventory, 8, 64));

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
        addSlotToContainer(new SlotLockable((TileMatterCreator) inventory, 79, 39)
        {
            @Override
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return stack != null && stack.getItem() instanceof ItemMemoryChip && !ItemMemoryChip.isMemoryEmpty(stack);
            }
        });

        //Item output slot
        addSlotToContainer(new SlotOutputOnly(inventory, 133, 39));
    }
}
