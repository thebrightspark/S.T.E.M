package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.LogHelper;
import cofh.api.energy.IEnergyProvider;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        //TODO: Add slots!
        //Energy Input Slot
        addSlotToContainer(new Slot(inventory, slotI++, 27, 65)
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
    }
}
