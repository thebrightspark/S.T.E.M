package brightspark.stem.tileentity;

import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.NBTHelper;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
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

public class TileMachine extends TileEntity implements IEnergyReceiver, ITickable, ISidedInventory
{
    public enum EnumSidePerm
    {
        ALL(0),
        INPUT(1),
        OUTPUT(2),
        NONE(3);

        public final int id;

        EnumSidePerm(int id)
        {
            this.id = id;
        }

        @Override
        public String toString()
        {
            return I18n.format("sideEnergyPerm." + name().toLowerCase());
        }

        public static EnumSidePerm getById(int id)
        {
            return id < 0 || id > EnumSidePerm.values().length - 1 ? null : EnumSidePerm.values()[id];
        }

        public EnumSidePerm getNextPerm()
        {
            return id + 1 > EnumSidePerm.values().length - 1 ? getById(0) : getById(id + 1);
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
            return TextFormatting.BLUE + "[" + I18n.format("sideEnergyPerm.mode", sideText) + " " + TextFormatting.DARK_AQUA + toString() + TextFormatting.BLUE + "] " + TextFormatting.RESET;
        }
    }

    //The facings are relative to the block's front.
    //TODO: Uncomment machine side permissions!
    //protected HashMap<EnumFacing, EnumSidePerm> sideConfigs = new HashMap<EnumFacing, EnumSidePerm>(6);
    protected StemEnergyStorage energy;
    //This is used in block.getDrops() so that the energy is only saved to the ItemStack when a wrench is used.
    public boolean usedWrenchToBreak = false;
    //The ItemStacks stored in this tile
    protected ItemStack[] slots;
    //This is used by getSlotsForFace
    protected int[] slotsForFaces;
    //Dependant on redstone input. Redstone signal = machine off //TODO: Make a button to change redstone interactivity
    public boolean active = true;

    public static final String KEY_STACK_ENERGY = "stackEnergy";
    public static final String KEY_SIDE_PERMS = "sidePerms";
    public static final String KEY_INVENTORY = "inventory";

    public TileMachine()
    {
        this(new StemEnergyStorage(100000, 1000)); //TODO: Later use configs
    }

    public TileMachine(StemEnergyStorage energy)
    {
        this(energy, 0);
    }

    public TileMachine(StemEnergyStorage energy, int numSlots)
    {
        this.energy = energy;
        slots = new ItemStack[numSlots];
        slotsForFaces = CommonUtils.createAscIntArray(numSlots);
        //for(EnumFacing side : EnumFacing.VALUES)
        //    sideConfigs.put(side, EnumSidePerm.ALL);
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
        return !isEnergyFull(); //&& sideConfigs.get(side).canInput();
    }

    public int getMaxExtract(EnumFacing side)
    {
        //if(side == null || sideConfigs.get(side).canOutput())
            return energy.getMaxExtract();
        //else
        //    return 0;
    }

    public int getMaxReceieve(EnumFacing side)
    {
        //if(side == null || sideConfigs.get(side).canInput())
            return energy.getMaxReceive();
        //else
        //    return 0;
    }

    /**
     * Gets a float between 0 and 1 of how full the energy is (1 being full and 0 empty).
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
        //Copy energy
        energy.setCapacity(machine.getMaxEnergyStored(null));
        energy.setMaxExtract(machine.getMaxExtract(null));
        energy.setMaxReceive(machine.getMaxReceieve(null));
        energy.setEnergyStored(machine.getEnergyStored(null));
        //Copy side permissions
        //for(EnumFacing side : EnumFacing.VALUES)
        //    sideConfigs.put(side, machine.getPermForSide(side));
        //Copy inventory
        for(int i = 0; i < machine.slots.length; ++i)
            slots[i] = machine.slots[i];
    }

    /**
     * Gets the relative side from the block's front and the absolute side.
     * If the block isn't an instance of AbstractBlockMachineDirectional then it'll just return the given side.
     */
    /*
    private EnumFacing getRelativeSide(IBlockState state, EnumFacing side)
    {
        if(state.getBlock() instanceof AbstractBlockMachineDirectional)
            return CommonUtils.getRelativeSide(side, state.getValue(AbstractBlockMachineDirectional.FACING));
        else
            return side;
    }
    */

    /**
     * Gets the absolute side from the block's front and the relative side.
     * If the block isn't an instance of AbstractBlockMachineDirectional then it'll just return the given side.
     */
    /*
    private EnumFacing getAbsoluteSide(IBlockState state, EnumFacing side)
    {
        if(state.getBlock() instanceof AbstractBlockMachineDirectional)
            return CommonUtils.getAbsoluteSide(side, state.getValue(AbstractBlockMachineDirectional.FACING));
        else
            return side;
    }
    */

    /**
     * Sets the permission for an absolute side.
     */
    /*
    public void setSidePerm(IBlockState state, EnumFacing side, EnumSidePerm perm)
    {
        sideConfigs.put(getRelativeSide(state, side), perm);
    }
    */

    /**
     * Changes the absolute side to the next permission.
     */
    /*
    public void nextSidePerm(IBlockState state, EnumFacing side)
    {
        EnumFacing relSide = getRelativeSide(state, side);
        EnumSidePerm nextPerm = sideConfigs.get(relSide).getNextPerm();
        sideConfigs.put(relSide, nextPerm);
    }
    */

    /**
     * Gets the permission for an absolute side.
     */
    /*
    public EnumSidePerm getPermForSide(IBlockState state, EnumFacing side)
    {
        return sideConfigs.get(getRelativeSide(state, side));
    }
    */

    /**
     * Gets the permission saved for the given side.
     * Note: The value returned is the tile's relative side directly from the stored value!
     */
    /*
    public EnumSidePerm getPermForSide(EnumFacing side)
    {
        return sideConfigs.get(side);
    }
    */

    /* NBT */

    /**
     * Writes the tile's data to the ItemStack.
     */
    public void writeDataToStack(ItemStack stack)
    {
        //Write energy
        //LogHelper.info("Machine Energy (write): " + energy.getEnergyStored());
        NBTHelper.setInteger(stack, KEY_STACK_ENERGY, energy.getEnergyStored());

        //Write side permissions
        /*
        NBTTagList sideList = new NBTTagList();
        for(EnumFacing facing : EnumFacing.values())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("side", (byte) facing.getIndex());
            tag.setByte("perm", (byte) sideConfigs.get(facing).id);
            sideList.appendTag(tag);
        }
        NBTHelper.setList(stack, KEY_SIDE_PERMS, sideList);
        */

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
        //Read energy
        energy.setEnergyStored(NBTHelper.getInt(stack, KEY_STACK_ENERGY));
        //LogHelper.info("Machine Energy (read): " + energy.getEnergyStored());

        //Read side permissions
        /*
        NBTTagList sideList = NBTHelper.getList(stack, KEY_SIDE_PERMS);
        for(int i = 0; i < sideList.tagCount(); ++i)
        {
            NBTTagCompound tag = sideList.getCompoundTagAt(i);
            sideConfigs.put(EnumFacing.getFront(tag.getByte("side")), EnumSidePerm.getById(tag.getByte("perm")));
        }
        */

        //Read inventory
        NBTTagList stackList = NBTHelper.getList(stack, KEY_INVENTORY);
        for(int i = 0; i < stackList.tagCount(); ++i)
        {
            NBTTagCompound tag = stackList.getCompoundTagAt(i);
            slots[tag.getByte("slot")] = ItemStack.loadItemStackFromNBT(tag);
        }
    }

    /**
     * Reads and returns the energy saved to the ItemStack.
     */
    public static int readEnergyFromStack(ItemStack stack)
    {
        return NBTHelper.getInt(stack, KEY_STACK_ENERGY);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        //Read energy
        energy.readFromNBT(nbt);

        //Read side permissions
        /*
        NBTTagList sideList = nbt.getTagList(KEY_SIDE_PERMS, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < sideList.tagCount(); ++i)
        {
            NBTTagCompound tag = sideList.getCompoundTagAt(i);
            sideConfigs.put(EnumFacing.getFront(tag.getByte("side")), EnumSidePerm.getById(tag.getByte("perm")));
        }
        */

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
        super.writeToNBT(nbt);

        //Write energy
        energy.writeToNBT(nbt);

        //Write side permissions
        /*
        NBTTagList sideList = new NBTTagList();
        for(EnumFacing facing : EnumFacing.values())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("side", (byte) facing.getIndex());
            tag.setByte("perm", (byte) sideConfigs.get(facing).id);
            sideList.appendTag(tag);
        }
        nbt.setTag(KEY_SIDE_PERMS, sideList);
        */

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
        /*
        IHaveFluid fluidTE = this instanceof IHaveFluid ? (IHaveFluid) this : null;
        IBlockState state = worldObj.getBlockState(pos);

        //Transfer energy/liquid with adjacent blocks
        for(EnumFacing side : EnumFacing.VALUES)
        {
            TileEntity te = worldObj.getTileEntity(pos.offset(side));
            switch(getPermForSide(state, side))
            {
                case INPUT:
                    if(!isEnergyFull() && te instanceof IEnergyProvider)
                    {
                        int extracted = ((IEnergyProvider) te).extractEnergy(side.getOpposite(), getMaxReceieve(side), false);
                        energy.modifyEnergyStored(extracted);
                    }
                    break;
                case OUTPUT:
                    if(fluidTE != null && te instanceof IFluidHandler && fluidTE.getFluidAmount() > 0)
                    {
                        FluidStack extracted = ((IFluidHandler) te).drain(new FluidStack(fluidTE.getFluidType(), fluidTE.getFluidTransferRate()), true);
                        //TO-DO: Finish!
                    }
            }
        }
        */
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
        return true; //sideConfigs.get(from) != EnumSidePerm.NONE;
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
        return ItemStackHelper.getAndSplit(slots, index, count);
        /*
        ItemStack stack = getStackInSlot(index);
        if(stack == null) return null;
        if(count >= stack.stackSize) return removeStackFromSlot(index);
        ItemStack split = stack.splitStack(count);
        slots[index] = stack;
        return split;
        */
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(slots, index);
        /*
        ItemStack stack = getStackInSlot(index);
        if(stack == null) return null;
        stack = stack.copy();
        slots[index] = null;
        return stack;
        */
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
        return isValidSlot(index) && stack != null;
    }

    @Override
    public int getField(int id)
    {
        return id == 0 ? energy.getEnergyStored() : 0;
    }

    @Override
    public void setField(int id, int value)
    {
        if(id == 0)
            energy.setEnergyStored(value);
    }

    @Override
    public int getFieldCount()
    {
        return 1;
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
