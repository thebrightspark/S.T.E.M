package brightspark.stem.tileentity;

import brightspark.stem.StemEnergyStorage;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.LogHelper;
import brightspark.stem.util.NBTHelper;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TileMachine extends TileEntity implements IEnergyReceiver, ITickable, ISidedInventory
{
    public enum SideEnergyPerm
    {
        ALL(0),
        INPUT(1),
        OUTPUT(2),
        NONE(3);

        private static SideEnergyPerm[] allPerms = new SideEnergyPerm[4];
        public final int id;

        static
        {
            //Create array of all perms
            for(SideEnergyPerm perm : values())
                allPerms[perm.id] = perm;
        }

        SideEnergyPerm(int id)
        {
            this.id = id;
        }

        @Override
        public String toString()
        {
            return I18n.format("sideEnergyPerm." + name().toLowerCase());
        }

        public static SideEnergyPerm getById(int id)
        {
            return id < 0 || id > allPerms.length - 1 ? null : allPerms[id];
        }

        public SideEnergyPerm getNextPerm()
        {
            return id + 1 > allPerms.length - 1 ? getById(0) : getById(id + 1);
        }

        public boolean canInput()
        {
            return this == INPUT || this == ALL;
        }

        public boolean canOutput()
        {
            return this == OUTPUT || this == ALL;
        }

        public String getChatDisplay(EnumFacing side)
        {
            String sideText = CommonUtils.capitaliseFirstLetter(side.getName());
            return TextFormatting.BLUE + "[" + I18n.format("sideEnergyPerm.mode", sideText) + " " + TextFormatting.DARK_AQUA + toString() + TextFormatting.BLUE + "]" + TextFormatting.RESET;
        }
    }

    protected HashMap<EnumFacing, SideEnergyPerm>  energySides = new HashMap<EnumFacing, SideEnergyPerm>(6);
    protected StemEnergyStorage energy;
    //This is used in block.getDrops() so that the energy is only saved to the ItemStack when a wrench is used.
    public boolean usedWrenchToBreak = false;
    protected ItemStack[] slots;
    //This is used by getSlotsForFace
    protected int[] slotsForFaces;

    public static final String KEY_STACK_ENERGY = "stackEnergy";
    public static final String KEY_SIDE_PERMS = "sidePerms";
    public static final String KEY_INVENTORY = "inventory";

    public TileMachine(int numSlots)
    {
        //Machine default energy
        this(new StemEnergyStorage(100000, 1000), numSlots); //TODO: Later use configs
    }

    public TileMachine(StemEnergyStorage energy, int numSlots)
    {
        this.energy = energy;
        slots = new ItemStack[numSlots];
        for(EnumFacing side : EnumFacing.VALUES)
            energySides.put(side, SideEnergyPerm.ALL);
        slotsForFaces = CommonUtils.createAscIntArray(numSlots);
    }

    public boolean hasEnergy()
    {
        return energy.getEnergyStored() > 0;
    }

    public boolean isEnergyFull()
    {
        return energy.getEnergyStored() >= energy.getMaxEnergyStored();
    }

    public boolean canReceiveEnergy(EnumFacing side)
    {
        return !isEnergyFull() && energySides.get(side).canInput();
    }

    public int getMaxExtract(EnumFacing side)
    {
        if(side == null || energySides.get(side).canOutput())
            return energy.getMaxExtract();
        else
            return 0;
    }

    public int getMaxReceieve(EnumFacing side)
    {
        if(side == null || energySides.get(side).canInput())
            return energy.getMaxReceive();
        else
            return 0;
    }

    /**
     * Gets a float between 0 and 1 of how full the energy energy is (1 being full and 0 empty).
     * @return Value between 0 and 1.
     */
    public float getEnergyPercentFloat()
    {
        return (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
    }

    public String getEnergyPercentString()
    {
        return Math.round(getEnergyPercentFloat() * 100) + "%";
    }

    public void copyDataFrom(TileMachine machine)
    {
        if(machine == null || machine.energy == null)
            return;
        energy.setCapacity(machine.getMaxEnergyStored(null));
        energy.setMaxExtract(machine.getMaxExtract(null));
        energy.setMaxReceive(machine.getMaxReceieve(null));
        energy.setEnergyStored(machine.getEnergyStored(null));
    }

    public void setEnergySidePerm(EnumFacing side, SideEnergyPerm perm)
    {
        energySides.put(side, perm);
    }

    public void nextEnergySidePerm(EnumFacing side)
    {
        SideEnergyPerm perm = SideEnergyPerm.getById(energySides.get(side).id).getNextPerm();
        energySides.put(side, perm);
    }

    public SideEnergyPerm getEnergyPermForSide(EnumFacing side)
    {
        return energySides.get(side);
    }

    /* NBT */

    public void writeEnergyToStack(ItemStack stack)
    {
        LogHelper.info("Machine Energy (write): " + energy.getEnergyStored());
        NBTHelper.setInteger(stack, KEY_STACK_ENERGY, energy.getEnergyStored());
    }

    /**
     * Reads and returns the energy saved to the ItemStack.
     */
    public static int readEnergyFromStack(ItemStack stack)
    {
        return NBTHelper.getInt(stack, KEY_STACK_ENERGY);
    }

    /**
     * Reads and set the energy save to the ItemStack to the energy for this TileMachine.
     */
    public void getEnergyFromStack(ItemStack stack)
    {
        energy.setEnergyStored(NBTHelper.getInt(stack, KEY_STACK_ENERGY));
        LogHelper.info("Machine Energy (read): " + energy.getEnergyStored());
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        //Read energy
        energy.readFromNBT(nbt);

        //Read side permissions
        NBTTagList sideList = nbt.getTagList(KEY_SIDE_PERMS, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < sideList.tagCount(); ++i)
        {
            NBTTagCompound tag = sideList.getCompoundTagAt(i);
            setEnergySidePerm(EnumFacing.getFront(tag.getByte("side")), SideEnergyPerm.getById(tag.getByte("perm")));
        }

        //Read inventory
        NBTTagList stackList = nbt.getTagList(KEY_INVENTORY, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < stackList.tagCount(); ++i)
        {
            NBTTagCompound tag = stackList.getCompoundTagAt(i);
            slots[tag.getByte("slot")] = ItemStack.loadItemStackFromNBT(tag);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        //Write energy
        energy.writeToNBT(nbt);

        //Write side permissions
        NBTTagList sideList = new NBTTagList();
        for(EnumFacing facing : EnumFacing.values())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("side", (byte) facing.getIndex());
            tag.setByte("perm", (byte) getEnergyPermForSide(facing).id);
            sideList.appendTag(tag);
        }
        nbt.setTag(KEY_SIDE_PERMS, sideList);

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
        return nbt;
    }

    /* Overrides */

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

    @Override
    public void update()
    {
        //Draw power from neighbouring blocks if set to
        if(!isEnergyFull())
        {
            int totalExtracted = 0;
            for(EnumFacing side : EnumFacing.VALUES)
            {
                if(getEnergyPermForSide(side) == SideEnergyPerm.INPUT)
                {
                    TileEntity te = worldObj.getTileEntity(pos.offset(side));
                    if(te instanceof IEnergyProvider)
                    {
                        int extracted = ((IEnergyProvider) te).extractEnergy(side.getOpposite(), getMaxReceieve(side), false);
                        if(extracted > 0)
                        {
                            if(totalExtracted + extracted > energy.getMaxReceive())
                            {
                                totalExtracted = energy.getMaxReceive();
                                break;
                            }
                            else totalExtracted += extracted;
                        }
                    }
                }
            }
            energy.modifyEnergyStored(totalExtracted);
        }
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
    {
        if(canReceiveEnergy(from))
            return energy.receiveEnergy(maxReceive, simulate);
        else
            return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from)
    {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from)
    {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from)
    {
        return energySides.get(from) != SideEnergyPerm.NONE;
    }

    private boolean isValidSlot(int index)
    {
        return index >= 0 && index < slots.length;
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
        ItemStack stack = getStackInSlot(index);
        if(stack == null) return null;
        if(count >= stack.stackSize) return removeStackFromSlot(index);
        ItemStack split = stack.splitStack(count);
        slots[index] = stack;
        return split;
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack stack = getStackInSlot(index);
        if(stack == null) return null;
        stack = stack.copy();
        slots[index] = null;
        return stack;
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
        return this.worldObj.getTileEntity(this.pos) == this && player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return index >= 0 && index < slots.length && stack != null;
    }

    //TODO: Use the field for progress
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
}
