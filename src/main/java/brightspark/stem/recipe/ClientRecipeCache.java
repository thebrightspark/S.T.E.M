package brightspark.stem.recipe;

import brightspark.stem.message.MessageRecipeRequest;
import brightspark.stem.util.CommonUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used for when something on the client side needs info about a recipe.
 * This will grab the necessary info from the server and store it for future requests.
 * Changes on the server using commands will notify this class of changes, but this class
 * will only get the changes to the recipe if needed.
 *
 * Mapped integer meanings:
 * >0 -> fluid amount for stack
 *  0 -> no recipe for stack
 * -1 -> waiting for server response
 */
@SideOnly(Side.CLIENT)
public class ClientRecipeCache
{
    private static List<StemRecipe> cachedRecipes = new ArrayList<StemRecipe>();

    private static void requestRecipe(ItemStack stack)
    {
        CommonUtils.NETWORK.sendToServer(new MessageRecipeRequest(stack));
        setRecipe(stack, -1);
    }

    private static StemRecipe getRecipeInternal(ItemStack stack)
    {
        for(StemRecipe recipe : cachedRecipes)
            if(recipe.isStackEqual(stack))
                return recipe;
        return null;
    }

    private static void setRecipe(ItemStack stack, long fluid)
    {
        StemRecipe recipe = getRecipeInternal(stack.copy());
        if(recipe == null)
            cachedRecipes.add(new StemRecipe(stack, fluid));
        else
            recipe.setFluidInput(fluid);
    }

    /**
     * Used by packets to recieve recipes sent from the server.
     */
    public static void receiveRecipe(ItemStack stack, long fluidAmount)
    {
        setRecipe(stack, fluidAmount);
    }

    /**
     * Gets the recipe for the stack given.
     * Will request the recipe from the server if not already cached locally.
     */
    public static StemRecipe getRecipe(ItemStack recipeStack)
    {
        StemRecipe recipe = getRecipeInternal(recipeStack);
        if(recipe == null)
        {
            //No recipe or dirty recipe - needs to be requested
            requestRecipe(recipeStack);
            return null;
        }
        return recipe;
    }

    /**
     * Gets the amount of STEM fluid needed to create the given ItemStack.
     * Will request the recipe from the server if not already cached locally.
     */
    public static long getFluidAmount(ItemStack recipeStack)
    {
        StemRecipe recipe = getRecipe(recipeStack);
        return recipe == null ? -1 : recipe.getFluidInput();
    }

    /**
     * Removed the cached recipe for this stack.
     * If stack is null, then clears all cached recipes.
     */
    public static void removeRecipe(ItemStack stack)
    {
        if(stack == null)
            //Remove all cached recipes
            cachedRecipes.clear();
        else
        {
            //Remove the recipe for the given stack
            Iterator<StemRecipe> iterator = cachedRecipes.iterator();
            while(iterator.hasNext())
                if(iterator.next().isStackEqual(stack))
                {
                    iterator.remove();
                    break;
                }
        }
    }
}
