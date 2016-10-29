package brightspark.stem.tileentity;

import brightspark.stem.init.StemFluids;
import brightspark.stem.util.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class TileLiquidEnergiser extends TileMachineWithFluid
{
    private int energyPerTick = 100; //TODO: add config for energy consumption rate
    //This is the creation progress in ticks
    private int progress;
    private static int maxProgress = 200; //TODO: add config for max progress

    public static final String KEY_PROGRESS = "progress";

    public TileLiquidEnergiser()
    {
        super(new FluidStack(StemFluids.fluidStem, 8000));
    }

    public float getProgressPercentFloat()
    {
        return (float) progress / (float) maxProgress;
    }

    public String getProgressPercentString()
    {
        return Math.round(getProgressPercentFloat() * 100) + "%";
    }

    @Override
    public void copyDataFrom(TileMachine machine)
    {
        super.copyDataFrom(machine);
        if(machine instanceof TileLiquidEnergiser)
            progress = ((TileLiquidEnergiser) machine).progress;
    }

    @Override
    public void update()
    {
        super.update();
        if(active && tank.hasSpace() && energy.getEnergyStored() >= energyPerTick)
        {
            if(!worldObj.isRemote)
            {
                //Increase progress
                progress++;
                if(progress >= maxProgress)
                {
                    //Create STEM
                    tank.fill(1);
                    progress = 0;
                }
                energy.modifyEnergyStored(- energyPerTick);
            }
            markDirty();
            worldObj.scheduleUpdate(getPos(), getBlockType(), 2);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        progress = nbt.getInteger(KEY_PROGRESS);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger(KEY_PROGRESS, progress);
        return super.writeToNBT(nbt);
    }

    @Override
    public int getField(int id)
    {
        return id == 2 ? progress : super.getField(id);
    }

    @Override
    public void setField(int id, int value)
    {
        if(id == 2)
            progress = value;
        else
            super.setField(id, value);
    }

    @Override
    public int getFieldCount()
    {
        return 3;
    }
}
