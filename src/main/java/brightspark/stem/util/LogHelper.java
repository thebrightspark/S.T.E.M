package brightspark.stem.util;

import brightspark.stem.STEM;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LogHelper
{
    private static Logger LOGGER = null;
    
    public static void setLogger(Logger logger)
    {
        LOGGER = logger;
    }
    
    public static void log(Level logLevel, String text, Object... args)
    {
        String s = STEM.MOD_NAME + ": " + String.format(text, args);
        if(LOGGER == null)
            System.out.println(String.format("[%s] %s", logLevel, s));
        else
            LOGGER.log(logLevel, s);
    }

    public static void debug(String text, Object... args)
    {
        log(Level.DEBUG, text, args);
    }

    public static void error(String text, Object... args)
    {
        log(Level.ERROR, text, args);
    }

    public static void info(String text, Object... args)
    {
        log(Level.INFO, text, args);
    }

    public static void warn(String text, Object... args)
    {
        log(Level.WARN, text, args);
    }
}
