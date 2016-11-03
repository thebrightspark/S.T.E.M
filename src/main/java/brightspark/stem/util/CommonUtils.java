package brightspark.stem.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.UniversalBucket;

public class CommonUtils
{
    /**
     * Returns a string of the inputted number with commas added to group the digits.
     */
    public static String addDigitGrouping(int number)
    {
        return addDigitGrouping(Integer.toString(number));
    }

    /**
     * Returns a string of the inputted number with commas added to group the digits.
     */
    public static String addDigitGrouping(String number)
    {
        String output = number;
        for(int i = number.length() - 3; i > 0; i -= 3)
            output = output.substring(0, i) + "," + output.substring(i);
        return output;
    }

    /**
     * Capitalises the first letter in the given string.
     */
    public static String capitaliseFirstLetter(String text)
    {
        if(text == null || text.length() <= 0)
            return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Creates an array where each value increases by 1 from 0.
     * e.g. input -> 5
     * returns -> [0, 1, 2, 3, 4]
     */
    public static int[] createAscIntArray(int size)
    {
        int[] array = new int[size];
        for(int i = 0; i < size; ++i)
            array[i] = i;
        return array;
    }

    /**
     * Creates an ItemStack for a Universal Bucket filled with the given fluid.
     */
    public static ItemStack createFilledBucket(Fluid fluid)
    {
        return UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid);
    }

    /**
     * Returns a Rotation which represents the rotation of this facing to North.
     * North, Up and Down will just return Rotation.NONE.
     */
    /*
    public static Rotation getRotationToNorth(EnumFacing facing)
    {
        switch(facing)
        {
            case EAST:
                return Rotation.COUNTERCLOCKWISE_90;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case WEST:
                return Rotation.CLOCKWISE_90;
            default:
                return Rotation.NONE;
        }
    }
    */

    /**
     * Returns a Rotation which represents the rotation of this facing from North.
     * North, Up and Down will just return Rotation.NONE.
     */
    /*
    public static Rotation getRotationFromNorth(EnumFacing facing)
    {
        switch(facing)
        {
            case EAST:
                return Rotation.CLOCKWISE_90;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case WEST:
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                return Rotation.NONE;
        }
    }
    */

    /**
     * Returns the absolute side facing given a relative to the given front side.
     */
    /*
    public static EnumFacing getAbsoluteSide(EnumFacing relativeSide, EnumFacing front)
    {
        LogHelper.info("Getting absolute side of " + relativeSide + " where front is " + front);
        if(relativeSide == front || relativeSide == EnumFacing.DOWN || relativeSide == EnumFacing.UP)
            return relativeSide;
        Rotation rot = getRotationToNorth(front);
        EnumFacing absSide = rot.rotate(relativeSide);
        LogHelper.info("Absolute side is " + absSide);
        return absSide;
    }
    */

    /**
     * Returns the relative side facing given an absolute side.
     */
    /*
    public static EnumFacing getRelativeSide(EnumFacing absoluteSide, EnumFacing front)
    {
        LogHelper.info("Getting relative side of " + absoluteSide + " where front is " + front);
        if(absoluteSide == front || absoluteSide == EnumFacing.DOWN || absoluteSide == EnumFacing.UP)
            return absoluteSide;
        Rotation rot = getRotationFromNorth(front);
        EnumFacing relSide = rot.rotate(absoluteSide);
        LogHelper.info("Relative side is " + relSide);
        return relSide;
    }
    */
}
