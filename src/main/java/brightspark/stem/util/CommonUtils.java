package brightspark.stem.util;

import brightspark.stem.STEM;
import brightspark.stem.init.StemFluids;
import brightspark.stem.message.*;
import brightspark.stem.recipe.ClientRecipeCache;
import brightspark.stem.recipe.ServerRecipeManager;
import brightspark.stem.recipe.StemRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class CommonUtils
{
    public static SimpleNetworkWrapper NETWORK;

    public static void regNetwork()
    {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(STEM.MOD_ID);
        NETWORK.registerMessage(MessageUpdateClientContainer.Handler.class, MessageUpdateClientContainer.class, 0, Side.CLIENT);
        NETWORK.registerMessage(MessageUpdateTileRecipe.Handler.class, MessageUpdateTileRecipe.class, 1, Side.CLIENT);
        NETWORK.registerMessage(MessageRecipeRequest.Handler.class, MessageRecipeRequest.class, 2, Side.SERVER);
        NETWORK.registerMessage(MessageRecipeReply.Handler.class, MessageRecipeReply.class, 3, Side.CLIENT);
        NETWORK.registerMessage(MessageSyncConfigs.Handler.class, MessageSyncConfigs.class, 4, Side.CLIENT);
        NETWORK.registerMessage(MessageRemoveCachedRecipe.Handler.class, MessageRemoveCachedRecipe.class, 5, Side.CLIENT);
    }

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
     * Gets the rounded average of all the integer values given
     */
    public static int average(int... values)
    {
        int total = 0;
        for(int v : values)
            total += v;
        return Math.round((float) total / (float) values.length);
    }

    /**
     * Creates an ItemStack for a Universal Bucket filled with the given fluid.
     */
    public static ItemStack createFilledBucket(Fluid fluid)
    {
        return UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid);
    }

    public static boolean isStemBucket(ItemStack stack)
    {
        return stack.getItem() instanceof UniversalBucket && ((UniversalBucket) stack.getItem()).getFluid(stack).getFluid().equals(StemFluids.fluidStem);
    }

    public static ItemStack readStackFromBuf(ByteBuf buf)
    {
        Item item = Item.getItemById(buf.readInt());
        int itemMeta = buf.readInt();
        return new ItemStack(item, 1, itemMeta);
    }

    public static void writeStackToBuf(ByteBuf buf, ItemStack stack)
    {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.getMetadata());
    }

    /**
     * Sorts the given ItemStack List alphabetically.
     */
    public static void sortItemStackList(List<ItemStack> list)
    {
        list.sort((o1, o2) -> o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName()));
    }

    /**
     * Checks if the given ItemStack List contains the given ItemStack.
     */
    public static boolean itemStackListContains(List<ItemStack> list, ItemStack stackToCheckFor)
    {
        for(ItemStack stored : list)
            if(ItemStack.areItemStacksEqual(stored, stackToCheckFor))
                return true;
        return false;
    }

    /**
     * Gets the StemRecipe for the given ItemStack if it exists.
     * On the server, this will access the ServerRecipeManager and get the recipe directly.
     * On the client, this will access the ClientRecipeCache, which will get the cached recipe and request it from
     *  the server if it doesn't have it.
     */
    public static StemRecipe getRecipeForStack(ItemStack stack)
    {
        if(FMLCommonHandler.instance().getSide().isClient())
            return ClientRecipeCache.getRecipe(stack);
        else
            return ServerRecipeManager.getRecipeForStack(stack);
    }

    /**
     * Uses getRecipeForStack and checks if the result is not null.
     */
    public static boolean hasRecipeForStack(ItemStack stack)
    {
        StemRecipe recipe = getRecipeForStack(stack);
        return recipe != null && recipe.getFluidInput() > 0;
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
