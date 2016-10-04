package brightspark.stem.tileentity;

import brightspark.stem.init.StemFluids;
import net.minecraftforge.fluids.FluidStack;

public class TileLiquidEnergiser extends TileMachine
{
    private FluidStack stemFluid;
    private int maxFluid = 8000;
    private int energyPerTick = 100; //TODO: add config for energy consumption rate
    //This is the creation progress in ticks
    private int progress;
    private int maxProgress = 200; //TODO: add config for max progress

    public TileLiquidEnergiser()
    {
        super(0);
        stemFluid = new FluidStack(StemFluids.fluidStem, 0);
    }

    @Override
    public void update()
    {
        if(stemFluid.amount < maxFluid && energy.getEnergyStored() >= energyPerTick)
        {
            //Increase progress
            progress++;
            if(progress >= maxProgress)
            {
                //Create STEM
                stemFluid.amount++;
                progress = 0;
            }
            energy.modifyEnergyStored(-energyPerTick);
            markDirty();
        }
    }
}
