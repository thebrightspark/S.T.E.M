package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.init.StemFluids;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TileLiquidEnergiser extends TileMachineWithFluid
{
    //private int[] pastEnergyInput = new int[40];

    public TileLiquidEnergiser()
    {
        super(new StemEnergyStorage(-1, Config.liquidEnergiserMaxEnergyInput), new FluidStack(StemFluids.fluidStem, 8000), 3);
        //Arrays.fill(pastEnergyInput, 0);
    }

    @Override
    public int getProgress()
    {
        return Math.min(Math.round(((float) energy.getEnergyStored() / (float) Config.liquidEnergiserEnergyPerMb) * 100f), 100);
    }

    @Override
    public boolean canWork()
    {
        return super.canWork() && tank.hasSpace();
    }

    @Override
    public void doWork()
    {
        super.doWork();
        tank.fillInternal(1, true);
    }

    @Override
    public void update()
    {
        super.update();

        //Average input
        //TODO: Maybe later only show average input on WAILA tooltips for powered blocks?
        // Might not even be able to do average energy if I switch to handling energy differently
        /*
        if(!worldObj.isRemote)
        {
            int lastDiff = energy.getEnergyStored() - lastEnergyAmount;
            pastEnergyInput[index] = lastDiff < 0 ? 0 : lastDiff;
            //lastEnergyAmount = energy.getEnergyStored();
            if(++index > pastEnergyInput.length - 1)
                index = 0;
            averageInput = CommonUtils.average(pastEnergyInput);
        }
        */

        //Handle slots
        ItemStack slotStack = slots.get(0);
        //Bucket input
        if(slotStack.getItem().equals(Items.BUCKET) && getFluidAmount() >= Fluid.BUCKET_VOLUME && slots.get(1).isEmpty())
        {
            tank.drainInternal(Fluid.BUCKET_VOLUME, true);
            slotStack.shrink(1);
            if(slotStack.getCount() <= 0)
                setInventorySlotContents(0, ItemStack.EMPTY);
            setInventorySlotContents(1, StemFluids.getStemBucket());
        }

        //lastEnergyAmount = energy.getEnergyStored();
    }

    /*
    public int getAverageInput()
    {
        return averageInput;
    }

    public String getAverageInputString()
    {
        return getAverageInput() + " RF/t";
    }

    @Override
    public int getField(int id)
    {
        return id == 3 ? averageInput : super.getField(id);
    }

    @Override
    public void setField(int id, int value)
    {
        if(id == 3)
            averageInput = value;
        else
            super.setField(id, value);
    }

    @Override
    public int getFieldCount()
    {
        return 4;
    }
    */
}
