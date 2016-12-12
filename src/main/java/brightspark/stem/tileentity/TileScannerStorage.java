package brightspark.stem.tileentity;

import brightspark.stem.recipe.RecipeManager;
import brightspark.stem.recipe.StemRecipe;
import brightspark.stem.util.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TileScannerStorage extends StemTileEntity
{
    private List<ItemStack> storedRecipes = new ArrayList<ItemStack>();

    private static final String KEY_RECIPES = "recipes";

    public TileScannerStorage()
    {
        super(2);
    }

    private void sortRecipes()
    {
        Collections.sort(storedRecipes, new Comparator<ItemStack>()
        {
            @Override
            public int compare(ItemStack o1, ItemStack o2)
            {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
            }
        });
    }

    private boolean containsRecipe(ItemStack stack)
    {
        if(stack == null)
            return true;
        for(ItemStack stored : storedRecipes)
            if(ItemStack.areItemStacksEqual(stored, stack))
                return true;
        return false;
    }

    public void setRecipeAtIndex(int index, ItemStack recipeStack)
    {
        if(storedRecipes == null || storedRecipes.isEmpty())
            storedRecipes = new ArrayList<ItemStack>();

        if(storedRecipes.size() <= index)
            storedRecipes.add(recipeStack);
        else
            storedRecipes.set(index, recipeStack);

        sortRecipes();
    }

    public List<ItemStack> getStoredRecipes()
    {
        return storedRecipes;
    }

    public void storeRecipe(ItemStack recipeStack)
    {
        if(!containsRecipe(recipeStack))
        {
            storedRecipes.add(recipeStack);
            sortRecipes();
        }
    }

    private void addRecipe(ItemStack recipeStack)
    {
        if(!containsRecipe(recipeStack))
            storedRecipes.add(recipeStack);
    }

    public StemRecipe getRecipeAtIndex(int index)
    {
        return index < 0 || index >= storedRecipes.size() ? null : RecipeManager.getRecipeForStack(storedRecipes.get(index));
    }

    @Override
    public void copyDataFrom(StemTileEntity machine)
    {
        //Copy recipes
        for(ItemStack stack : ((TileScannerStorage)machine).getStoredRecipes())
            addRecipe(stack);
        sortRecipes();

        super.copyDataFrom(machine);
    }

    /**
     * Writes the tile's data to the ItemStack.
     */
    @Override
    public void writeDataToStack(ItemStack stack)
    {
        //Write recipes
        NBTTagList recipeList = new NBTTagList();
        for(int i = 0; i < slots.length; ++i)
        {
            NBTTagCompound tag = new NBTTagCompound();
            storedRecipes.get(i).writeToNBT(tag);
            recipeList.appendTag(tag);
        }
        NBTHelper.setList(stack, KEY_INVENTORY, recipeList);

        super.writeDataToStack(stack);
    }

    /**
     * Reads and set the energy saved to the ItemStack to the energy for this TileMachine.
     */
    @Override
    public void readDataFromStack(ItemStack stack)
    {
        //Read recipes
        NBTTagList recipeList = NBTHelper.getList(stack, KEY_INVENTORY);
        for(int i = 0; i < recipeList.tagCount(); ++i)
        {
            NBTTagCompound tag = recipeList.getCompoundTagAt(i);
            addRecipe(ItemStack.loadItemStackFromNBT(tag));
        }
        sortRecipes();

        super.readDataFromStack(stack);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        //Read recipes
        NBTTagList recipeList = nbt.getTagList(KEY_RECIPES, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < recipeList.tagCount(); i++)
        {
            NBTTagCompound tag = recipeList.getCompoundTagAt(i);
            addRecipe(ItemStack.loadItemStackFromNBT(tag));
        }
        sortRecipes();

        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        //Write recipes
        NBTTagList recipeList = new NBTTagList();
        for(int i = 0; i < storedRecipes.size(); i++)
        {
            NBTTagCompound tag = new NBTTagCompound();
            storedRecipes.get(i).writeToNBT(tag);
            recipeList.appendTag(tag);
        }
        nbt.setTag(KEY_RECIPES, recipeList);

        return super.writeToNBT(nbt);
    }
}
