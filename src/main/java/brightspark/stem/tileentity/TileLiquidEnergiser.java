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
    private int lastEnergyAmount = 0;

    public TileLiquidEnergiser()
    {
        super(new StemEnergyStorage(-1, Config.liquidEnergiserMaxEnergyInput), new FluidStack(StemFluids.fluidStem, 8000), 3);
        Arrays.fill(pastEnergyInput, 0);
    }

    @Override
    public void update()
    {
        super.update();

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
        if((slotStack = slots[0]) != null && slotStack.getItem() instanceof IEnergyContainerItem)
            ((IEnergyContainerItem) slotStack.getItem()).extractEnergy(slotStack, getMaxReceieve(null), false);
        //Bucket input
        if((slotStack = slots[1]) != null && slotStack.getItem().equals(Items.BUCKET) && getFluidAmount() >= Fluid.BUCKET_VOLUME && slots[2] == null)
        {
            tank.drainInternal(Fluid.BUCKET_VOLUME, true);
            slotStack.stackSize--;
            if(slotStack.stackSize <= 0)
                setInventorySlotContents(1, null);
            setInventorySlotContents(2, StemFluids.getStemBucket());
        }

        //Average input
        for(int i = 0; i < pastEnergyInput.length - 1; i++)
            pastEnergyInput[i] = pastEnergyInput[i + 1];
        int lastDiff = energy.getEnergyStored() - lastEnergyAmount;
        pastEnergyInput[pastEnergyInput.length - 1] = lastDiff < 0 ? 0 : lastDiff;
        lastEnergyAmount = energy.getEnergyStored();
    }

    public int getAverageInput()
    {
        return CommonUtils.average(pastEnergyInput);
    }

    public String getAverageInputString()
    {
        return getAverageInput() + " RF/t";
    }
}
