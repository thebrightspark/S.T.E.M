package brightspark.stem.gui;

import brightspark.stem.message.MessageUpdateClientContainer;
import brightspark.stem.tileentity.StemTileEntity;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.CommonUtils;
import cofh.redstoneflux.api.IEnergyProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ContainerMachineBase extends Container
{
    protected StemTileEntity inventory;
    protected int[] cachedFields;
    protected int slotI = 0;
    protected int invStartX = 8;
    protected int invStartY = 93;

    public ContainerMachineBase(InventoryPlayer invPlayer, StemTileEntity machine)
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
        return inventory.isUsableByPlayer(playerIn);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if(cachedFields == null)
        {
            cachedFields = new int[inventory.getFieldCount()];
            //Fill the array with -1s rather than 0s so that a field value of 0 can be detected when the container is opened
            Arrays.fill(cachedFields, -1);
        }

        for(IContainerListener listener : listeners)
            for(int i = 0; i < inventory.getFieldCount(); i++)
                if(cachedFields[i] != inventory.getField(i))
                {
                    cachedFields[i] = inventory.getField(i);
                    //If the data is bigger than a short, then send over a custom, larger packet.
                    if(cachedFields[i] > Short.MAX_VALUE || cachedFields[i] < Short.MIN_VALUE)
                        CommonUtils.NETWORK.sendTo(new MessageUpdateClientContainer(i, cachedFields[i]), (EntityPlayerMP) listener);
                    else
                        listener.sendWindowProperty(this, i, cachedFields[i]);
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
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = this.inventorySlots.get(slot);

        if (slotObject != null && slotObject.getHasStack())
        {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            //If GUI slot
            if (slot < slotI)
            {
                if (!mergeItemStack(stackInSlot, slotI, slotI + 36, true))
                    return ItemStack.EMPTY;

                slotObject.onSlotChange(stackInSlot, stack);
            }
            //If slot Inventory
            else if (slot >= slotI && slot <= slotI + 36)
            {
                boolean success = false;
                for(int i = 0; i < slotI; i++)
                {
                    Slot guiSlot = inventorySlots.get(i);
                    if(guiSlot.isItemValid(stackInSlot))
                    {
                        int slotStackSpace = guiSlot.getStack().isEmpty() ? guiSlot.getSlotStackLimit() : guiSlot.getSlotStackLimit() - guiSlot.getStack().getCount();
                        ItemStack splitStack = stackInSlot.splitStack(slotStackSpace);
                        if(mergeItemStack(splitStack, i, i + 1, false))
                        {
                            success = true;
                            if(splitStack.getCount() <= 0)
                                break;
                        }
                        else
                            stackInSlot.grow(splitStack.getCount());
                    }
                }
                if(!success)
                    return ItemStack.EMPTY;
            }

            if (stackInSlot.getCount() == 0)
                slotObject.putStack(ItemStack.EMPTY);
            else
                slotObject.onSlotChanged();

            if (stackInSlot.getCount() == stack.getCount())
                return ItemStack.EMPTY;

            slotObject.onTake(player, stackInSlot);
        }

        return stack;
    }

    public class SlotMachine extends Slot
    {
        public SlotMachine(IInventory inventoryIn, int xPosition, int yPosition)
        {
            super(inventoryIn, slotI++, xPosition, yPosition);
        }
    }

    public class SlotOutputOnly extends SlotMachine
    {
        public SlotOutputOnly(IInventory inventoryIn, int xPosition, int yPosition)
        {
            super(inventoryIn, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack)
        {
            return false;
        }
    }

    public class SlotEnergyInput extends SlotMachine
    {
        public SlotEnergyInput(IInventory inventoryIn, int xPosition, int yPosition)
        {
            super(inventoryIn, xPosition, yPosition);
        }

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
    }

    public class SlotLockable extends SlotMachine
    {
        private TileMachine machine;

        public SlotLockable(TileMachine machine, int xPosition, int yPosition)
        {
            super(machine, xPosition, yPosition);
            this.machine = machine;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            return !machine.isWorking();
        }

        @Override
        public int getSlotStackLimit()
        {
            return 1;
        }
    }
}
