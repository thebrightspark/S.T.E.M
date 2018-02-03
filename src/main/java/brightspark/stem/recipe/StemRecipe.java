package brightspark.stem.recipe;

import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class StemRecipe
{
    protected long fluidInput;
    protected ItemStack output;

    public StemRecipe(Block result, long fluid)
    {
        this(new ItemStack(result), fluid);
    }
    public StemRecipe(Item result, long fluid)
    {
        this(new ItemStack(result), fluid);
    }
    public StemRecipe(ItemStack result, long fluid)
    {
        output = result.copy();
        output.setCount(1);
        fluidInput = (long) Math.ceil((double) fluid / (double) result.getCount());
    }

    /**
     * Checks if the given stack is equal to the output of this recipe.
     */
    public boolean isStackEqual(ItemStack stack)
    {
        return OreDictionary.itemMatches(stack, output, false);
    }

    public long getFluidInput()
    {
        return fluidInput;
    }

    public void setFluidInput(long fluid)
    {
        fluidInput = fluid;
    }

    public ItemStack getOutput()
    {
        return output;
    }

    /**
     * Returns a string array representation of this recipe for use in a CSV file.
     */
    public String[] toCsvStringArray()
    {
        return new String[] {output.getItem().getRegistryName().toString(), Integer.toString(output.getMetadata()), Long.toString(fluidInput)};
    }

    public static StemRecipe fromCsvStringArray(String[] recipe)
    {
        Item item = Item.getByNameOrId(recipe[0]);
        if(item == null)
        {
            LogHelper.warn("Couldn't find item '" + recipe[0] + "' from recipe: " + recipe[0] + ", " + recipe[1] + ", " + recipe[2]);
            return null;
        }
        String metaString = recipe[1].trim();
        int meta = metaString.isEmpty() ? 0 : Integer.parseInt(metaString);
        return new StemRecipe(new ItemStack(item, 1, meta), Long.parseLong(recipe[2]));
    }

    @Override
    public String toString()
    {
        return CommonUtils.stackToString(output) + ", " + fluidInput + "mb";
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof StemRecipe && ((StemRecipe) obj).output.isItemEqual(output) && ((StemRecipe) obj).fluidInput == fluidInput;
    }
}
