package brightspark.stem.tileentity;

import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.fluid.LockedFluidTank;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class TileMachineWithFluid extends TileMachine implements IFluidHandler
{
    protected LockedFluidTank tank;

    public TileMachineWithFluid(StemEnergyStorage energy, FluidStack fluid, int numSlots)
    {
        super(energy, numSlots);
        tank = new LockedFluidTank(fluid.getFluid(), fluid.amount, this);
    }

    /**
     * Reads and returns the storedFluid saved to the ItemStack.
     */
    public static FluidStack readFluidFromStack(ItemStack stack)
    {
        return FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
    }

    /**
     * Reads and set the energy saved to the ItemStack to the energy for this TileMachine.
     */
    public void readDataFromStack(ItemStack stack)
    {
        super.readDataFromStack(stack);
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt != null && nbt.hasKey("tankCapacity"))
        {
            tank.readFromNBT(stack.getTagCompound());
            tank.setTile(this);
        }
    }

    /**
     * Writes the tile's data to the ItemStack.
     */
    @Override
    public void writeDataToStack(ItemStack stack)
    {
        super.writeDataToStack(stack);
        tank.writeToNBT(stack.getTagCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        tank.readFromNBT(nbt);
        tank.setTile(this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        tank.writeToNBT(nbt);
        return super.writeToNBT(nbt);
    }

    public Fluid getFluidType()
    {
        return tank.getFluidType();
    }

    public int getFluidAmount()
    {
        return tank.getFluidAmount();
    }

    public int getFluidSpace()
    {
        return tank.getCapacity() - tank.getFluidAmount();
    }

    public float getFluidPercentage()
    {
        return (float) tank.getFluidAmount() / (float) tank.getCapacity();
    }

    public int getFluidGuiHeight(int maxHeight)
    {
        return (int) Math.ceil(getFluidPercentage() * (float) maxHeight);
    }

    public int getFluidTransferRate()
    {
        return tank.transferRate;
    }

    public int getFluidMaxOutput()
    {
        return Math.min(getFluidTransferRate(), getFluidAmount());
    }

    @Override
    public int getField(int id)
    {
        return id == 2 ? tank.getFluidAmount() : super.getField(id);
    }

    @Override
    public void setField(int id, int value)
    {
        if(id == 2)
            tank.setAmount(value);
        else
            super.setField(id, value);
    }

    @Override
    public int getFieldCount()
    {
        return 3;
    }

    //Capability

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) tank;
        return super.getCapability(capability, facing);
    }

    //IFluidHandler

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return tank.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return tank.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return tank.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return tank.drain(maxDrain, doDrain);
    }

    /**
     * Drains a bucket's worth of fluid and returns it.
     */
    public FluidStack drainBucket()
    {
        return tank.drainInternal(Fluid.BUCKET_VOLUME, true);
    }
}
