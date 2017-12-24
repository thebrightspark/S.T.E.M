package brightspark.stem.recipe;

import brightspark.stem.util.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecipeGenerateTask implements Runnable
{
    private static List<IRecipe> craftingRecipes = CraftingManager.getInstance().getRecipeList();
    private static Map<ItemStack, ItemStack> furnaceRecipes = FurnaceRecipes.instance().getSmeltingList();

    private RecipeGenerator generator;
    private List<Item> items;
    private int recipesGenerated = 0;

    public RecipeGenerateTask(RecipeGenerator generator, List<Item> items)
    {
        this.generator = generator;
        this.items = items;
    }

    @Override
    public void run()
    {
        items.forEach(item -> {
            StemRecipe recipe = genRecipeForStack(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
            if(recipe != null) addRecipe(recipe);
        });

        generator.onTaskCompleted(recipesGenerated);
    }

    private StemRecipe genRecipeForStack(ItemStack stack)
    {
        StemRecipe generatedRecipe = null;
        List<IRecipe> recipes = findCraftingRecipes(stack);
        if(!recipes.isEmpty())
        {
            //Use crafting recipe
            for(IRecipe recipe : recipes)
            {
                List ingredients = getIngredients(recipe);
                if(ingredients != null)
                {
                    //Make sure we have stem recipes already for all of the ingredients, and sum up the fluid requirements
                    int fluidAmount = 0;
                    for(Object ingredient : ingredients)
                    {
                        ItemStack ingStack = null;
                        if(ingredient instanceof ItemStack)
                            ingStack = (ItemStack) ingredient;
                        //TODO: Handle other cases of the ingredient if it's not already an ItemStack (i.e. ore recipes)

                        if(ingStack == null)
                        {
                            fluidAmount = -1;
                            break;
                        }
                        int ingFluid = ServerRecipeManager.getStemNeeded(ingStack);
                        if(ingFluid <= 0)
                        {
                            //If no stem recipe, try generate one
                            StemRecipe ingStemRecipe = genRecipeForStack(ingStack);
                            if(ingStemRecipe == null)
                            {
                                fluidAmount = - 1;
                                break;
                            }
                            else
                            {
                                fluidAmount += ingStemRecipe.getFluidInput();
                                addRecipe(ingStemRecipe);
                            }
                        }
                        fluidAmount += ingFluid;
                    }

                    if(fluidAmount > 0)
                    {
                        generatedRecipe = new StemRecipe(stack, fluidAmount);
                        LogHelper.info("Generated stem recipe (%s) from a crafting recipe", generatedRecipe);
                        //Stop trying to gen from a crafting recipe
                        break;
                    }
                }
            }
        }

        if(generatedRecipe == null)
        {
            List<ItemStack> furnaceInputs = findFurnaceInputs(stack);
            if(!furnaceInputs.isEmpty())
            {
                //Use furnace recipe
                for(ItemStack furnaceInput : furnaceInputs)
                {
                    int ingFluid = ServerRecipeManager.getStemNeeded(furnaceInput);
                    if(ingFluid <= 0)
                    {
                        //If no stem recipe, try generate one
                        StemRecipe ingStemRecipe = genRecipeForStack(furnaceInput);
                        if(ingStemRecipe == null)
                            continue;
                        else
                        {
                            ingFluid = ingStemRecipe.getFluidInput();
                            addRecipe(ingStemRecipe);
                        }
                    }

                    if(ingFluid > 0)
                    {
                        generatedRecipe = new StemRecipe(stack, ingFluid);
                        LogHelper.info("Generated stem recipe (%s) from a furnace recipe", generatedRecipe);
                        //Stop trying to gen from a furnace recipe
                        break;
                    }
                }
            }
        }

        //TODO: Other modded recipes?

        return generatedRecipe;
    }

    private void addRecipe(StemRecipe recipe)
    {
        ServerRecipeManager.addRecipe(recipe);
        recipesGenerated++;
    }

    private static List getIngredients(IRecipe recipe)
    {
        List ingredients = null;
        if(recipe instanceof ShapedRecipes)
            ingredients = Arrays.asList(((ShapedRecipes) recipe).recipeItems);
        else if(recipe instanceof ShapelessRecipes)
            ingredients = ((ShapelessRecipes) recipe).recipeItems;
        else if(recipe instanceof ShapedOreRecipe)
            ingredients = Arrays.asList(((ShapedOreRecipe) recipe).getInput());
        else if(recipe instanceof ShapelessOreRecipe)
            ingredients = ((ShapelessOreRecipe) recipe).getInput();
        return ingredients;
    }

    private static List<IRecipe> findCraftingRecipes(ItemStack stack)
    {
        List<IRecipe> matchingRecipes = new ArrayList<>();
        craftingRecipes.forEach(recipe -> {
            if(recipe.getRecipeOutput().isItemEqualIgnoreDurability(stack))
                matchingRecipes.add(recipe);
        });
        return matchingRecipes;
    }

    private static List<ItemStack> findFurnaceInputs(ItemStack stack)
    {
        List<ItemStack> matchingInputs = new ArrayList<>();
        furnaceRecipes.forEach((input, output) -> {
            if(OreDictionary.itemMatches(output, stack, false))
                matchingInputs.add(input);
        });
        return matchingInputs;
    }
}
