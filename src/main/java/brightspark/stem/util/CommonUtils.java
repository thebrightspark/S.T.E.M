package brightspark.stem.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CommonUtils
{
    public static boolean isWrench(ItemStack stack)
    {
        if(stack == null) return false;
        Item item = stack.getItem();
        //TODO: Finish checking for wrench
        return false;
    }

    /**
     * Returns a string of the inputted number with commas added to group the digits.
     */
    public static String addDigitGrouping(int number)
    {
        return addDigitGrouping(Integer.toString(number));
    }

    public static String addDigitGrouping(String number)
    {
        String output = number;
        for(int i = number.length() - 3; i > 0; i -= 3)
            output = output.substring(0, i) + "," + output.substring(i);
        return output;
    }

    public static String capitaliseFirstLetter(String text)
    {
        if(text == null || text.length() <= 0)
            return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
