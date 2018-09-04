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
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class StemTileEntity extends TileEntity implements ISidedInventory
{
    //The ItemStacks stored in this tile
    protected NonNullList<ItemStack> slots;
    private IItemHandler itemHandler;
    //This is used by getSlotsForFace
    private int[] slotsForFaces;
    //This is used in block.getDrops() so that certain data is saved to the ItemStack when a wrench.json is used.
    public boolean usedWrenchToBreak = false;

    private static final String KEY_INVENTORY = "inventory";

    public StemTileEntity(int numSlots)
    {
        slots = NonNullList.withSize(numSlots, ItemStack.EMPTY);
        itemHandler = new InvWrapper(this);
        slotsForFaces = CommonUtils.createAscIntArray(numSlots);
    }

    public void copyDataFrom(StemTileEntity machine)
    {
        //Copy inventory
        for(int i = 0; i < machine.slots.size(); ++ i)
            slots.set(i, machine.slots.get(i));
    }

    /**
     * Writes the tile's data to the ItemStack.
     */
    public void writeDataToStack(ItemStack stack)
    {
        if(!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        //Write inventory
        NBTTagList stackList = new NBTTagList();
        for(int i = 0; i < slots.size(); ++ i)
        {
            if(slots.get(i).isEmpty()) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("slot", (byte) i);
            slots.get(i).writeToNBT(tag);
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
        for(int i = 0; i < stackList.tagCount(); ++ i)
        {
            NBTTagCompound tag = stackList.getCompoundTagAt(i);
            slots.set(tag.getByte("slot"), new ItemStack(tag));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        //Read inventory
        NBTTagList stackList = nbt.getTagList(KEY_INVENTORY, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < stackList.tagCount(); ++ i)
        {
            NBTTagCompound tag = stackList.getCompoundTagAt(i);
            slots.set(tag.getByte("slot"), new ItemStack(tag));
        }

        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        //Write inventory
        NBTTagList stackList = new NBTTagList();
        for(int i = 0; i < slots.size(); ++ i)
        {
            if(slots.get(i).isEmpty()) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("slot", (byte) i);
            slots.get(i).writeToNBT(tag);
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
        return index >= 0 && index < slots.size();
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
        return slots.size();
    }

    @Override
    public boolean isEmpty()
    {
        for(ItemStack stack : slots)
            if(!stack.isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return isValidSlot(index) ? slots.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(slots, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(slots, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        if(isValidSlot(index))
            slots.set(index, stack);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return world.getTileEntity(pos) == this && player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return isValidSlot(index) && !stack.isEmpty();
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
        slots.clear();
    }

    @Override
    public String getName()
    {
        return blockType.getRegistryName().getPath();
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) itemHandler : super.getCapability(capability, facing);
    }
}
