package brightspark.stem.tileentity;

import brightspark.stem.init.StemFluids;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        return progress / maxProgress;
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
        //TODO: Something's going on... the GUI is showing that the client TE isn't being updated, but the server one is.
        super.update();
        if(active && tank.hasSpace() && energy.getEnergyStored() >= energyPerTick)
        {
            //Increase progress
            progress++;
            if(progress >= maxProgress)
            {
                //Create STEM
                tank.fill(1);
                progress = 0;
            }
            energy.modifyEnergyStored(-energyPerTick);
            markDirty();
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
}
