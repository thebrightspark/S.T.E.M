package brightspark.stem.util;

import brightspark.stem.item.ItemWrench;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class WrenchHelper
{
    public enum EnumWrenchMode
    {
        TURN(0),
        CONFIG_SIDE(1);

        public final int id;

        EnumWrenchMode(int id)
        {
            this.id = id;
        }

        public static EnumWrenchMode getById(int id)
        {
            return id < 0 || id > EnumWrenchMode.values().length - 1 ? null : EnumWrenchMode.values()[id];
        }

        public EnumWrenchMode getNextMode()
        {
            return id + 1 > EnumWrenchMode.values().length - 1 ? getById(0) : getById(id + 1);
        }

        @Override
        public String toString()
        {
            return I18n.format("wrenchMode." + name().toLowerCase());
        }

        public String getDisplayPrefix()
        {
            return TextFormatting.GOLD + "[" + I18n.format("wrenchMode.mode") + " " + TextFormatting.YELLOW + toString() + TextFormatting.GOLD + "] " + TextFormatting.RESET;
        }
    }

    private static List<String> WRENCH_IDS = new ArrayList<String>();

    public static boolean addWrench(String id)
    {
        if(WRENCH_IDS.contains(id)) return false;
        WRENCH_IDS.add(id);
        return true;
    }

    /**
     * Returns true if the item is a recognised wrench.
     */
    public static boolean isWrench(ItemStack stack)
    {
        if(stack.isEmpty()) return false;
        String item = stack.getItem().getRegistryName().toString();
        for(String id : WRENCH_IDS)
            if(item.equals(id))
                return true;
        return false;
    }

    /**
     * Gets the translated wrench mode for compatible wrenches.
     */
    public static EnumWrenchMode getWrenchMode(ItemStack stack)
    {
        EnumWrenchMode mode = null;
        if(isWrench(stack))
        {
            if(stack.getItem() instanceof ItemWrench)
                mode = ItemWrench.getMode(stack);
            //TODO: Add other wrench compatability (Also add wrench to string ids above)
        }
        return mode;
    }
}
