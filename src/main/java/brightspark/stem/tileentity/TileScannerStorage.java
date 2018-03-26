package brightspark.stem.tileentity;

import brightspark.stem.recipe.ServerRecipeManager;
import brightspark.stem.recipe.StemRecipe;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class TileScannerStorage extends StemTileEntity
{
    private List<ItemStack> storedRecipes = new ArrayList<>();

    public static final String KEY_RECIPES = "recipes";

    public TileScannerStorage()
    {
        super(2);
        shouldSaveInventoryToNBT = false;
    }

    /**
     * Checks for any stored ItemStacks which don't have recipes and removes them
     */
    public void removeMissingRecipes()
    {
        //Check for and remove missing recipes
        storedRecipes.removeIf(itemStack -> !ServerRecipeManager.hasRecipeForStack(itemStack));
        markDirty();
    }

    /**
     * Sorts the stored recipes alphabetically
     */
    private void sortRecipes()
    {
        CommonUtils.sortItemStackList(storedRecipes);
        markDirty();
    }

    /**
     * Checks if the given ItemStack is stored
     */
    private boolean containsRecipe(ItemStack stack)
    {
        if(stack.isEmpty())
            return true;
        return CommonUtils.itemStackListContains(storedRecipes, stack);
    }

    public void setRecipes(List<ItemStack> recipeStacks)
    {
        storedRecipes = recipeStacks;
    }

    public List<ItemStack> getStoredRecipes()
    {
        return storedRecipes;
    }

    public boolean hasRecipes()
    {
        return getStoredRecipes().size() > 0;
    }

    /**
     * Adds the ItemStack to the stored recipes
     */
    public void storeRecipe(ItemStack recipeStack)
    {
        if(!containsRecipe(recipeStack) && ServerRecipeManager.hasRecipeForStack(recipeStack))
        {
            storedRecipes.add(recipeStack);
            sortRecipes();
        }
    }

    /**
     * Adds the ItemStack to the stored recipes
     * This is an internal method which doesn't auto-sort the recipes after adding
     */
    private void addRecipe(ItemStack recipeStack)
    {
        if(!containsRecipe(recipeStack) && ServerRecipeManager.hasRecipeForStack(recipeStack))
            storedRecipes.add(recipeStack);
        markDirty();
    }

    public StemRecipe getRecipeAtIndex(int index)
    {
        return index < 0 || index >= storedRecipes.size() ? null : CommonUtils.getRecipeForStack(storedRecipes.get(index));
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
        for(ItemStack storedRecipe : storedRecipes)
            recipeList.appendTag(storedRecipe.writeToNBT(new NBTTagCompound()));
        NBTHelper.setList(stack, KEY_RECIPES, recipeList);

        super.writeDataToStack(stack);
    }

    /**
     * Reads and set the energy saved to the ItemStack to the energy for this TileMachine.
     */
    @Override
    public void readDataFromStack(ItemStack stack)
    {
        //Read recipes
        NBTTagList recipeList = NBTHelper.getList(stack, KEY_RECIPES);
        for(int i = 0; i < recipeList.tagCount(); ++i)
        {
            NBTTagCompound tag = recipeList.getCompoundTagAt(i);
            addRecipe(new ItemStack(tag));
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
            addRecipe(new ItemStack(tag));
        }
        sortRecipes();

        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        //Write recipes
        NBTTagList recipeList = new NBTTagList();
        for(ItemStack stack : storedRecipes)
            recipeList.appendTag(stack.writeToNBT(new NBTTagCompound()));
        nbt.setTag(KEY_RECIPES, recipeList);

        return super.writeToNBT(nbt);
    }
}
