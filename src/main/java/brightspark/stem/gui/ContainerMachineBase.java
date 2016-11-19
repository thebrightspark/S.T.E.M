package brightspark.stem.gui;

import brightspark.stem.message.MessageUpdateTile;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.CommonUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMachineBase extends Container
{
    protected TileMachine inventory;
    protected int[] cachedFields;
    protected int slotI = 0;
    protected int invStartX = 8;
    protected int invStartY = 93;

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

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if(cachedFields == null)
            cachedFields = new int[inventory.getFieldCount()];

        for(IContainerListener listener : listeners)
            for(int i = 0; i < inventory.getFieldCount(); i++)
                if(cachedFields[i] != inventory.getField(i))
                {
                    cachedFields[i] = inventory.getField(i);
                    //If the data is bigger than a short, then send over a custom, larger packet.
                    if(cachedFields[i] > Short.MAX_VALUE || cachedFields[i] < Short.MIN_VALUE)
                        CommonUtils.NETWORK.sendTo(new MessageUpdateTile(inventory.getPos(), i, cachedFields[i]), (EntityPlayerMP) listener);
                    else
                        listener.sendProgressBarUpdate(this, i, cachedFields[i]);
                }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        inventory.setField(id, data);
    }

    /**
     * Adds the player's inventory slots to the container. Called after addSlots().
     */
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, invStartX + j * 18, invStartY + i * 18));

        for (int i = 0; i < 9; i++)
            addSlotToContainer(new Slot(inventoryPlayer, i, invStartX + i * 18, invStartY + 18 * 3 + 4));
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

            //If GUI slot
            if (slot < slotI)
            {
                if (!mergeItemStack(stackInSlot, slotI, slotI + 36, true))
                    return null;

                slotObject.onSlotChange(stackInSlot, stack);
            }
            //If slot Inventory
            else if (slot >= slotI && slot <= slotI + 36)
            {
                boolean success = false;
                for(int i = 0; i < slotI; i++)
                {
                    if(inventorySlots.get(i).isItemValid(stackInSlot))
                    {
                        if(mergeItemStack(stackInSlot, i, i + 1, false))
                        {
                            success = true;
                            break;
                        }
                    }
                }
                if(!success)
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
