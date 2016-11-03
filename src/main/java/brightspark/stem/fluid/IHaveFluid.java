package brightspark.stem.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IHaveFluid extends IFluidHandler
{
    Fluid getFluidType();

    int getFluidAmount();

    int getFluidSpace();

    int getFluidTransferRate();

    /**
     * Returns the max transfer rate or the amount stored if less than the transfer rate.
     */
    int getFluidMaxOutput();
}
