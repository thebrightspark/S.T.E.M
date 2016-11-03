package brightspark.stem.tileentity;

import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.init.StemFluids;
import brightspark.stem.util.CommonUtils;
import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
        super(new StemEnergyStorage(1000000, -1), new FluidStack(StemFluids.fluidStem, 8000), 3);
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

        //Liquid progress
        if(active && tank.hasSpace() && energy.getEnergyStored() >= energyPerTick)
        {
            if(!worldObj.isRemote)
            {
                //Increase progress
                progress++;
                if(progress >= maxProgress)
                {
                    //Create STEM
                    tank.fillInternal(1);
                    tank.fillInternal(1000);
                    progress = 0;
                }
                energy.modifyEnergyStored(- energyPerTick);
            }
            markDirty();
            worldObj.scheduleUpdate(getPos(), getBlockType(), 2);
        }

        //Handle slots
        for(int i = 0; i < slots.length; i++)
        {
            ItemStack stack = slots[i];
            if(stack == null)
                continue;
            switch(i)
            {
                case 0: //Energy input
                    if(stack.getItem() instanceof IEnergyContainerItem)
                        ((IEnergyContainerItem) stack.getItem()).extractEnergy(stack, getMaxReceieve(null), false);
                    break;
                case 1: //Bucket input
                    if(stack.getItem().equals(Items.BUCKET) && getFluidAmount() >= 1000 && slots[2] == null)
                    {
                        tank.drainInternal(1000);
                        stack.stackSize--;
                        setInventorySlotContents(2, CommonUtils.createFilledBucket(StemFluids.fluidStem));
                    }
                    break;
            }
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
