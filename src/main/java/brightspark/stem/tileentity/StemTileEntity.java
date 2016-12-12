package brightspark.stem.tileentity;

import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * Created by Mark on 11/12/2016.
 */
public class StemTileEntity extends TileEntity implements ISidedInventory
{
    //The ItemStacks stored in this tile
    protected ItemStack[] slots;
    //This is used by getSlotsForFace
    protected int[] slotsForFaces;
    //This is used in block.getDrops() so that certain data is saved to the ItemStack when a wrench is used.
    public boolean usedWrenchToBreak = false;

    public static final String KEY_INVENTORY = "inventory";

    public StemTileEntity(int numSlots)
    {
        slots = new ItemStack[numSlots];
        slotsForFaces = CommonUtils.createAscIntArray(numSlots);
    }

    public void copyDataFrom(StemTileEntity machine)
    {
        //Copy inventory
        for(int i = 0; i < machine.slots.length; ++i)
            slots[i] = machine.slots[i];
    }

    /**
     * Writes the tile's data to the ItemStack.
     */
    public void writeDataToStack(ItemStack stack)
    {
        //Write inventory
        NBTTagList stackList = new NBTTagList();
        for(int i = 0; i < slots.length; ++i)
        {
            if(slots[i] == null) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("slot", (byte) i);
            slots[i].writeToNBT(tag);
            stackList.appendTag(tag);
        }
        NBTHelper.setList(stack, KEY_INVENTORY, stackList);
    }

    /**
     * Reads and set the energy saved to the ItemStack to the energy for this TileMachine.
     */
    public void readDataFromStack(ItemStack stack)
    {
        //Read inventory
        NBTTagList stackList = NBTHelper.getList(stack, KEY_INVENTORY);
        for(int i = 0; i < stackList.tagCount(); ++i)
        {
            NBTTagCompound tag = stackList.getCompoundTagAt(i);
            slots[tag.getByte("slot")] = ItemStack.loadItemStackFromNBT(tag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        //Read inventory
        NBTTagList stackList = nbt.getTagList(KEY_INVENTORY, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < stackList.tagCount(); ++i)
        {
            NBTTagCompound tag = stackList.getCompoundTagAt(i);
            slots[tag.getByte("slot")] = ItemStack.loadItemStackFromNBT(tag);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        //Write inventory
        NBTTagList stackList = new NBTTagList();
        for(int i = 0; i < slots.length; ++i)
        {
            if(slots[i] == null) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("slot", (byte) i);
            slots[i].writeToNBT(tag);
            stackList.appendTag(tag);
        }
        nbt.setTag(KEY_INVENTORY, stackList);

        return super.writeToNBT(nbt);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    /**
     * Use this to send data about the block. In this case, the NBTTagCompound.
     */
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    /**
     * Use this to update the block when a packet is received.
     */
    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    private boolean isValidSlot(int index)
    {
        return index >= 0 && index < slots.length;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return slotsForFaces;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction)
    {
        return isValidSlot(index);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return isValidSlot(index);
    }

    @Override
    public int getSizeInventory()
    {
        return slots.length;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index)
    {
        return isValidSlot(index) ? slots[index] : null;
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(slots, index, count);
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(slots, index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack)
    {
        if(isValidSlot(index)) slots[index] = stack;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(pos) == this && player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return isValidSlot(index) && stack != null;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        slots = new ItemStack[slots.length];
    }

    @Override
    public String getName()
    {
        return blockType.getRegistryName().getResourcePath();
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }
}
