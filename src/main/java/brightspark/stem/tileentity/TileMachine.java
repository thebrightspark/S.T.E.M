package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.NBTHelper;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;

public class TileMachine extends StemTileEntity implements IEnergyReceiver, ITickable
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
    //Dependant on redstone input. Redstone signal = machine off //TODO: Make a button to change redstone interactivity
    public boolean active = true;

    public static final String KEY_STACK_ENERGY = "stackEnergy";
    public static final String KEY_SIDE_PERMS = "sidePerms";

    public TileMachine()
    {
        this(null);
    }
    public TileMachine(int numSlots)
    {
        this(null, numSlots);
    }
    public TileMachine(StemEnergyStorage energy)
    {
        this(energy, 0);
    }
    public TileMachine(StemEnergyStorage energy, int numSlots)
    {
        super(numSlots);
        this.energy = energy == null ? new StemEnergyStorage(Config.machineEnergyCapacity, Config.machineEnergyMaxTransfer) : energy;
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

    @Override
    public void copyDataFrom(StemTileEntity machine)
    {
        super.copyDataFrom(machine);
        if(machine == null || !(machine instanceof TileMachine) || ((TileMachine) machine).energy == null)
            return;
        TileMachine tileMachine = (TileMachine) machine;
        //Copy energy
        energy.setCapacity(tileMachine.getMaxEnergyStored(null));
        energy.setMaxExtract(tileMachine.getMaxExtract(null));
        energy.setMaxReceive(tileMachine.getMaxReceieve(null));
        energy.setEnergyStored(tileMachine.getEnergyStored(null));
        //Copy side permissions
        //for(EnumFacing side : EnumFacing.VALUES)
        //    sideConfigs.put(side, tileMachine.getPermForSide(side));
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
    @Override
    public void writeDataToStack(ItemStack stack)
    {
        super.writeDataToStack(stack);

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
    }

    /**
     * Reads and set the energy saved to the ItemStack to the energy for this TileMachine.
     */
    @Override
    public void readDataFromStack(ItemStack stack)
    {
        super.readDataFromStack(stack);

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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
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

        return super.writeToNBT(nbt);
    }

    /* Overrides */

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
}
