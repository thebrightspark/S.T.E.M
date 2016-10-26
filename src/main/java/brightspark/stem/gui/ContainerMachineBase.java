package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMachineBase extends Container
{
    protected TileMachine inventory;
    protected int slotInvStart = 1;
    protected int invStartX = 8;
    protected int invStartY = 84;

    public ContainerMachineBase(InventoryPlayer invPlayer, TileMachine machine)
    {
        inventory = machine;
        init();
        addSlots();
        bindPlayerInventory(invPlayer);
    }

    /**
     * Called first in the constructor for anything which cannot be done in the constructor.
     */
    protected void init() {}

    /**
     * Called after init() to add slots to the container.
     */
    protected void addSlots() {}

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return inventory.isUseableByPlayer(playerIn);
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, inventory);
    }

    /**
     * Adds the player's inventory slots to the container. Called after addSlots().
     */
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
    {
        slotInvStart = inventorySlots.size();

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, invStartX + j * 18, invStartY + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventoryPlayer, i, invStartX + i * 18, invStartY + 18 * 3 + 4));
        }
    }

    /**
     * What happens when you shift-click a slot.
     * This implementation will work with a container with 1 slot with id 0.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot)
    {
        ItemStack stack = null;
        Slot slotObject = this.inventorySlots.get(slot);

        if (slotObject != null && slotObject.getHasStack())
        {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            //TODO: Gonna need to do something with this and the way slots are used
            //If slot 0 (input)
            if (slot == 0)
            {
                if (!this.mergeItemStack(stackInSlot, slotInvStart, slotInvStart+36, true))
                    return null;

                slotObject.onSlotChange(stackInSlot, stack);
            }
            //If slot Inventory
            else if (slot >= slotInvStart && slot <= slotInvStart+36 && inventory.isItemValidForSlot(slot, stackInSlot))
            {
                if (!this.mergeItemStack(stackInSlot, 0, 1, false))
                    return null;
            }

            if (stackInSlot.stackSize == 0)
                slotObject.putStack(null);
            else
                slotObject.onSlotChanged();

            if (stackInSlot.stackSize == stack.stackSize)
                return null;

            slotObject.onPickupFromSlot(player, stackInSlot);
        }

        return stack;
    }
}
