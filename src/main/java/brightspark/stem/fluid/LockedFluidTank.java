package brightspark.stem.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * This tank will get locked to only accept the given liquid once created.
 * Also adds some helpful methods and adds a transfer limit.
 */
public class LockedFluidTank implements IFluidTank, IFluidHandler
{
    protected FluidStack storedFluid;
    protected int capacity;
    public int transferRate = 100;
    protected TileEntity tile;
    protected IFluidTankProperties[] tankProperties;

    public LockedFluidTank(Fluid fluid, int capacity, TileEntity tile)
    {
        this(fluid, capacity);
        this.tile = tile;
    }

    public LockedFluidTank(Fluid fluid, int capacity)
    {
        storedFluid = new FluidStack(fluid, 0);
        this.capacity = capacity;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        storedFluid = FluidStack.loadFluidStackFromNBT(nbt);
        capacity = nbt.getInteger("tankCapacity");
        transferRate = nbt.getInteger("tankTransferRate");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        storedFluid.writeToNBT(nbt);
        nbt.setInteger("tankCapacity", capacity);
        nbt.setInteger("tankTransferRate", transferRate);
        return nbt;
    }

    public LockedFluidTank setTransferRate(int rate)
    {
        transferRate = rate;
        return this;
    }

    public void setTile(TileEntity te)
    {
        tile = te;
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        return storedFluid.isFluidEqual(fluid);
    }

    public boolean canDrainFluidType(FluidStack fluid)
    {
        return canFillFluidType(fluid);
    }

    public boolean hasSpace()
    {
        return getFluidAmount() < getCapacity();
    }

    public int getSpace()
    {
        return getCapacity() - getFluidAmount();
    }

    /**
     * Will return the max amount that can be inputted at once.
     * This will either be the max transfer rate or the remaining free storage if less than the transfer rate.
     */
    public int getMaxInput()
    {
        return Math.min(getSpace(), transferRate);
    }

    /**
     * Will return the max amount that can be outputted at once.
     * This will either be the max transfer rate or the remaining fluid stored if less than the transfer rate.
     */
    public int getMaxOutput()
    {
        return Math.min(getFluidAmount(), transferRate);
    }

    /**
     * Directly sets the amount of storedFluid.
     */
    public void setAmount(int amount)
    {
        if(storedFluid == null)
            return;
        if(amount < 0)
            amount = 0;
        else if(amount > capacity)
            amount = capacity;
        storedFluid.amount = amount;
    }

    @Override
    public FluidStack getFluid()
    {
        return storedFluid;
    }

    public Fluid getFluidType()
    {
        return storedFluid.getFluid();
    }

    @Override
    public int getFluidAmount()
    {
        return storedFluid.amount;
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return new FluidTankInfo(this);
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        if (tankProperties == null)
            tankProperties = new IFluidTankProperties[] { new IFluidTankProperties()
            {
                @Nullable
                @Override
                public FluidStack getContents()
                {
                    return storedFluid.copy();
                }

                @Override
                public int getCapacity()
                {
                    return capacity;
                }

                @Override
                public boolean canFill()
                {
                    return true;
                }

                @Override
                public boolean canDrain()
                {
                    return true;
                }

                @Override
                public boolean canFillFluidType(FluidStack fluidStack)
                {
                    return LockedFluidTank.this.canFillFluidType(fluidStack);
                }

                @Override
                public boolean canDrainFluidType(FluidStack fluidStack)
                {
                    return LockedFluidTank.this.canDrainFluidType(fluidStack);
                }
            } };
        return tankProperties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return canFillFluidType(resource) ? fillInternal(Math.min(resource.amount, getMaxInput()), doFill) : 0;
    }

    /**
     * Fills ignoring transfer rate.
     */
    public int fillInternal(int amount, boolean doFill)
    {
        if(amount <= 0)
            return 0;

        int toFill = Math.min(amount, getSpace());

        if(doFill)
        {
            storedFluid.amount += toFill;
            if(tile != null)
                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(storedFluid, tile.getWorld(), tile.getPos(), this, toFill));
        }

        return toFill;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return canDrainFluidType(resource) ? drain(resource.amount, doDrain) : null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return drainInternal(Math.min(maxDrain, getMaxOutput()), doDrain);
    }

    /**
     * Drains ignoring transfer rate.
     */
    public FluidStack drainInternal(int amount, boolean doDrain)
    {
        if(amount <= 0)
            return null;

        int toDrain = Math.min(amount, getFluidAmount());

        if(doDrain)
        {
            storedFluid.amount -= toDrain;
            if(tile != null)
                FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(storedFluid, tile.getWorld(), tile.getPos(), this, toDrain));
        }

        return new FluidStack(storedFluid.getFluid(), toDrain);
    }
}
