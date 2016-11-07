package brightspark.stem;

/**
 * Created by Mark on 07/11/2016.
 */
public class Config
{
    //Liquid Energiser

    /** Use this to limit the energy input. If <= 0, then it'll accept infinite input (max integer for RF, max long for Tesla). */
    public static int maxEnergyInput = -1;
    /** This is how much energy is needed per milli bucket of STEM fluid. */
    public static int energyPerMb = 1000000;
}
