package brightspark.stem.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * This tank will get locked to only accept the given liquid once created.
 * Also adds some helpful methods and adds a transfer limit.
 */
public class LockedFluidTank extends FluidTank
{
    //Named "liquid" to avoid conflict with FluidTank#fluid
    public final Fluid liquid;
    public int transferRate = 100;

    public LockedFluidTank(Fluid fluid, int capacity, TileEntity tile)
    {
        this(fluid, capacity);
        setTileEntity(tile);
    }

    public LockedFluidTank(Fluid fluid, int capacity)
    {
        super(fluid, 0, capacity);
        this.liquid = fluid;
    }

    public LockedFluidTank setTransferRate(int rate)
    {
        transferRate = rate;
        return this;
    }

    public Fluid getAcceptableFluid()
    {
        return liquid;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        return fluid.getFluid().equals(this.liquid) && canFill();
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluid)
    {
        return fluid != null && fluid.getFluid().equals(this.liquid) && canDrain();
    }

    public boolean hasSpace()
    {
        return getFluidAmount() < getCapacity();
    }

    /**
     * Will return the max amount that can be inputted at once.
     * This will either be the max transfer rate or the remaining free storage if less than the transfer rate.
     */
    public int getMaxInput()
    {
        return Math.min(capacity - getFluidAmount(), transferRate);
    }

    public int fill(int amount)
    {
        return fillInternal(new FluidStack(liquid, Math.min(amount, transferRate)), true);
    }

    public int fillInternal(int amount)
    {
        return fillInternal(new FluidStack(liquid, amount), true);
    }

    public FluidStack drain(int amount)
    {
        return drainInternal(new FluidStack(liquid, Math.min(amount, transferRate)), true);
    }

    public FluidStack drainInternal(int amount)
    {
        return drainInternal(new FluidStack(liquid, amount), true);
    }

    /**
     * Directly sets the amount of fluid.
     */
    public void setAmount(int amount)
    {
        if(fluid == null)
            return;
        if(amount < 0)
            amount = 0;
        else if(amount > capacity)
            amount = capacity;
        fluid.amount = amount;
    }
}
