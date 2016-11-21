package brightspark.stem.handler;

import brightspark.stem.Config;
import brightspark.stem.STEM;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

/**
 * Created by Mark on 07/11/2016.
 */
public class ConfigHandler
{
    public static class Categories
    {
        public static final String GENERAL = Configuration.CATEGORY_GENERAL;
        public static final String LIQUID_ENERGISER = "liquid_energiser";
        public static final String MATTER_SCANNER = "matter_scanner";
    }

    public static Configuration configuration;

    public static void init(File configFile)
    {
        //Create configuration object from the given configuration file
        if(configuration == null)
        {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        //General
        Config.machineEnergyMaxTransfer = configuration.getInt("machineEnergyMaxTransfer", Categories.GENERAL, Config.machineEnergyMaxTransfer, 1, Integer.MAX_VALUE, "This will be the default machine energy transfer rate unless it has a custom value.");
        Config.machineEnergyCapacity = configuration.getInt("machineEnergyCapacity", Categories.GENERAL, Config.machineEnergyCapacity, 1, Integer.MAX_VALUE, "This will be the default machine energy capacity unless it has a custom value.");

        //Liquid Energiser
        Config.liquidEnergiserMaxEnergyInput = configuration.getInt("liquidEnergiserMaxEnergyInput", Categories.LIQUID_ENERGISER, Config.liquidEnergiserMaxEnergyInput, -1, Integer.MAX_VALUE, "Use this to limit the energy input. If <= 0, then it'll accept infinite input (max integer for RF, max long for Tesla)");
        Config.liquidEnergiserEnergyPerMb = configuration.getInt("liquidEnergiserEnergyPerMb", Categories.LIQUID_ENERGISER, Config.liquidEnergiserEnergyPerMb, 1, Integer.MAX_VALUE, "This is how much energy is needed per milli bucket of STEM fluid");

        //Matter Scanner
        Config.matterScannerEnergyPerTick = configuration.getInt("matterScannerEnergyPerTick", Categories.MATTER_SCANNER, Config.matterScannerEnergyPerTick, 1, Integer.MAX_VALUE, "Amount of energy used per tick");

        if(configuration.hasChanged())
            configuration.save();
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.getModID().equalsIgnoreCase(STEM.MOD_ID))
            //Resync configs
            loadConfiguration();
    }
}
