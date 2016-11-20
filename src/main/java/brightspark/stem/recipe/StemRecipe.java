package brightspark.stem.recipe;

import brightspark.stem.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class StemRecipe
{
    protected int fluidInput;
    protected ItemStack output;

    public StemRecipe(Block result, int fluid)
    {
        this(new ItemStack(result), fluid);
    }
    public StemRecipe(Item result, int fluid)
    {
        this(new ItemStack(result), fluid);
    }
    public StemRecipe(ItemStack result, int fluid)
    {
        fluidInput = fluid;
        output = result.copy();
    }

    public int getFluidInput()
    {
        return fluidInput;
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
        return new String[] {output.getItem().getRegistryName().toString(), Integer.toString(output.getMetadata()), Integer.toString(fluidInput)};
    }

    public static StemRecipe fromCsvStringArray(String[] recipe)
    {
        Item item = Item.getByNameOrId(recipe[0]);
        if(item == null)
        {
            LogHelper.warn("Couldn't find item '" + recipe[0] + "' from recipe: " + recipe[0] + ", " + recipe[1] + ", " + recipe[2]);
            return null;
        }
        return new StemRecipe(new ItemStack(item, Integer.parseInt(recipe[1])), Integer.parseInt(recipe[2]));
    }

    @Override
    public String toString()
    {
        return output.toString() + ", " + fluidInput + "mb";
    }
}
