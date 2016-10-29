package brightspark.stem.tileentity;

import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.fluid.IHaveFluid;
import brightspark.stem.fluid.LockedFluidTank;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class TileMachineWithFluid extends TileMachine implements IHaveFluid
{
    protected LockedFluidTank tank;

    public static final String KEY_FLUID = "fluid";

    public TileMachineWithFluid(FluidStack fluid)
    {
        super();
        initTank(fluid);
    }

    public TileMachineWithFluid(StemEnergyStorage energy, FluidStack fluid)
    {
        super(energy);
        initTank(fluid);
    }

    private void initTank(FluidStack fluid)
    {
        tank = new LockedFluidTank(fluid.getFluid(), fluid.amount, this);
    }

    private boolean isFluidEqual(FluidStack fluid)
    {
        return isFluidEqual(fluid.getFluid());
    }

    private boolean isFluidEqual(Fluid fluid)
    {
        return tank.liquid.equals(fluid);
    }

    /**
     * Reads and returns the fluid saved to the ItemStack.
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
        //Read fluid
        if(tank == null)
            initTank(null);
        tank.setFluid(FluidStack.loadFluidStackFromNBT(stack.getTagCompound()));
    }

    /**
     * Writes the tile's data to the ItemStack.
     */
    public void writeDataToStack(ItemStack stack)
    {
        super.writeDataToStack(stack);
        //Write fluid
        if(tank != null && tank.getFluid() != null && stack != null)
        {
            if (!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            tank.getFluid().writeToNBT(stack.getTagCompound());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if(tank == null)
            initTank(null);
        tank.setFluid(FluidStack.loadFluidStackFromNBT(nbt));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if(tank != null && tank.getFluid() != null)
            tank.getFluid().writeToNBT(nbt);
        return super.writeToNBT(nbt);
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new IFluidTankProperties[0];
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if(!isFluidEqual(resource))
            return 0;
        if(!doFill)
            return tank.getFluidAmount() + resource.amount > tank.getCapacity() ? tank.getCapacity() - resource.amount : resource.amount;
        return tank.fill(resource.amount);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if(!isFluidEqual(resource))
            return null;
        if(!doDrain)
        {
            int amount = tank.getFluidAmount() - resource.amount < 0 ? tank.getFluidAmount() : resource.amount;
            return new FluidStack(tank.liquid, amount);
        }
        return tank.drain(resource.amount);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return drain(new FluidStack(tank.liquid, maxDrain), doDrain);
    }

    @Override
    public Fluid getFluidType()
    {
        return tank.liquid;
    }

    @Override
    public int getFluidAmount()
    {
        return tank.getFluidAmount();
    }

    @Override
    public int getFluidTransferRate()
    {
        return tank.transferRate;
    }

    @Override
    public int getFluidMaxOutput()
    {
        return Math.min(getFluidTransferRate(), getFluidAmount());
    }

    @Override
    public int getField(int id)
    {
        return id == 1 ? tank.getFluidAmount() : super.getField(id);
    }

    @Override
    public void setField(int id, int value)
    {
        if(id == 1)
            tank.setAmount(value);
        else
            super.setField(id, value);
    }

    @Override
    public int getFieldCount()
    {
        return 2;
    }
}
