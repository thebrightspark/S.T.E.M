package brightspark.stem;

public class Config
{
    //Machines General

    /** This will be the default machine energy transfer rate unless it has a custom value. */
    public static int machineEnergyMaxTransfer = 200;
    /** This will be the default machine energy capacity unless it has a custom value. */
    public static int machineEnergyCapacity = 100000;

    //Liquid Energiser

    /** Use this to limit the energy input. If <= 0, then it'll accept infinite input (max integer for RF, max long for Tesla). */
    public static int liquidEnergiserMaxEnergyInput = -1;
    /** This is how much energy is needed per milli bucket of STEM fluid. */
    public static int liquidEnergiserEnergyPerMb = 1000000;

    //Matter Scanner

    /** Amount of energy used per tick */
    public static int matterScannerEnergyPerTick = 1000;

    //Matter Creator

    /** Use this to limit the energy input. If <= 0, then it'll accept infinite input (max integer for RF, max long for Tesla). */
    public static int matterCreatorMaxEnergyInput = -1;
    /** Amount of energy used per mb of STEM fluid to create an item */
    public static int matterCreatorEnergyPerMb = 10000;
}
