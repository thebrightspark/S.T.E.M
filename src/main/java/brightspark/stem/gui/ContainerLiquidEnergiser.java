package brightspark.stem.gui;

import brightspark.stem.tileentity.TileLiquidEnergiser;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerLiquidEnergiser extends ContainerMachineBase<TileLiquidEnergiser>
{
    public ContainerLiquidEnergiser(InventoryPlayer invPlayer, TileLiquidEnergiser machine)
    {
        super(invPlayer, machine);
    }

    @Override
    protected void addSlots()
    {
        //Energy Input Slot
        //addSlotToContainer(new SlotEnergyInput(inventory, 27, 65));

        //Bucket In Slot
        addSlotToContainer(new SlotMachine(inventory, 134, 23)
        {
            @Override
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return stack != null && stack.getItem().equals(Items.BUCKET);
            }
        });

        //Bucket Out Slot
        addSlotToContainer(new SlotOutputOnly(inventory, 134, 54));
    }
}
