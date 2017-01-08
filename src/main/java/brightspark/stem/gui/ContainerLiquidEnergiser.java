package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMachine;
import cofh.api.energy.IEnergyProvider;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerLiquidEnergiser extends ContainerMachineBase
{
    public ContainerLiquidEnergiser(InventoryPlayer invPlayer, TileMachine machine)
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
