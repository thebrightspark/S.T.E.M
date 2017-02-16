package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.init.StemFluids;
import brightspark.stem.util.CommonUtils;
import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;

public class TileLiquidEnergiser extends TileMachineWithFluid
{
    private int[] pastEnergyInput = new int[40];
    private int index = 0;
    private int lastEnergyAmount = 0;
    private int averageInput = 0;

    public TileLiquidEnergiser()
    {
        super(new StemEnergyStorage(-1, Config.liquidEnergiserMaxEnergyInput), new FluidStack(StemFluids.fluidStem, 8000), 3);
        Arrays.fill(pastEnergyInput, 0);
    }

    @Override
    public int getProgress()
    {
        return Math.min(Math.round(((float) energy.getEnergyStored() / (float) Config.liquidEnergiserEnergyPerMb) * 100f), 100);
    }

    @Override
    public void update()
    {
        super.update();

        //Average input
        //TODO: Maybe later only show average input on WAILA tooltips for powered blocks?
        if(!worldObj.isRemote)
        {
            int lastDiff = energy.getEnergyStored() - lastEnergyAmount;
            pastEnergyInput[index] = lastDiff < 0 ? 0 : lastDiff;
            //lastEnergyAmount = energy.getEnergyStored();
            if(++index > pastEnergyInput.length - 1)
                index = 0;
            averageInput = CommonUtils.average(pastEnergyInput);
        }

        //Liquid progress
        if(active && tank.hasSpace() && energy.getEnergyStored() >= Config.liquidEnergiserEnergyPerMb)
        {
            if(!worldObj.isRemote)
            {
                //Create STEM
                while(energy.getEnergyStored() >= Config.liquidEnergiserEnergyPerMb)
                {
                    tank.fillInternal(1, true);
                    energy.modifyEnergyStored(-Config.liquidEnergiserEnergyPerMb);
                }
            }
            markDirty();
        }

        //Handle slots
        ItemStack slotStack;
        //Energy input
        //if((slotStack = slots[0]) != null && slotStack.getItem() instanceof IEnergyContainerItem)
        //    ((IEnergyContainerItem) slotStack.getItem()).extractEnergy(slotStack, getMaxReceieve(null), false);
        //Bucket input
        if((slotStack = slots[0]) != null && slotStack.getItem().equals(Items.BUCKET) && getFluidAmount() >= Fluid.BUCKET_VOLUME && slots[1] == null)
        {
            tank.drainInternal(Fluid.BUCKET_VOLUME, true);
            slotStack.stackSize--;
            if(slotStack.stackSize <= 0)
                setInventorySlotContents(0, null);
            setInventorySlotContents(1, StemFluids.getStemBucket());
        }

        lastEnergyAmount = energy.getEnergyStored();
    }

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
}
