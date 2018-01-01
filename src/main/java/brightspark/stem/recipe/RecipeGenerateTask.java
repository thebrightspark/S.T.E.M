package brightspark.stem.recipe;

import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.LogHelper;
import javafx.util.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.server.FMLServerHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

public class RecipeGenerateTask implements Runnable
{
    private static List<IRecipe> craftingRecipes = CraftingManager.getInstance().getRecipeList();
    private static Map<ItemStack, ItemStack> furnaceRecipes = FurnaceRecipes.instance().getSmeltingList();

    private RecipeGenerator generator;
    private ItemStack itemStack;
    private List<Item> items;
    private int recipesGenerated = 0;
    private Set<IRecipe> checkedCraftingRecipes = new HashSet<>();
    private Set<Pair<ItemStack, ItemStack>> checkedFurnaceRecipes = new HashSet<>();

    public RecipeGenerateTask(ItemStack stack)
    {
        itemStack = stack;
    }

    public RecipeGenerateTask(RecipeGenerator generator, List<Item> items)
    {
        this.generator = generator;
        this.items = items;
    }

    @Override
    public void run()
    {
        if(itemStack != null)
        {
            //Generate recipe for 1 ItemStack
            StemRecipe recipe = genRecipeForStack(itemStack, true);
            addRecipe(recipe);
        }
        else
        {
            //Generate recipes for multiple Items
            LogHelper.info("Starting generation for %s items: %s", items.size(), items);
            MinecraftServer server = FMLServerHandler.instance().getServer();
            boolean isSinglePlayer = server != null && server.isSinglePlayer();
            items.forEach(item -> {
                if(isSinglePlayer)
                {
                    //If in single player, we're safe to use Item#getSubItems
                    NonNullList<ItemStack> stacks = NonNullList.create();
                    item.getSubItems(item, null, stacks);
                    stacks.forEach((stack) -> {
                        StemRecipe recipe = genRecipeForStack(stack, true);
                        addRecipe(recipe);
                    });
                }
                else
                {
                    StemRecipe recipe = genRecipeForStack(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE), false);
                    addRecipe(recipe);
                }
            });
        }

        if(generator != null)
            generator.onTaskCompleted(recipesGenerated);
    }

    private StemRecipe genRecipeForStack(ItemStack stack, boolean strict)
    {
        //If recipe already exists, just return it
        StemRecipe stemRecipe = ServerRecipeManager.getRecipeFromCache(stack);
        if(stemRecipe != null) return stemRecipe;

        LogHelper.info("Trying to generate recipe for %s", CommonUtils.stackToString(stack));

        //Try generate using crafting recipes
        stemRecipe = genUsingCraftingRecipes(stack, strict);
        if(stemRecipe != null) return stemRecipe;

        //Try generate using furnace recipes
        stemRecipe = genUsingFurnaceRecipes(stack);
        if(stemRecipe != null) return stemRecipe;

        //TODO: Other modded recipes?

        LogHelper.warn("Couldn't generate a recipe for %s", CommonUtils.stackToString(stack));
        return null;
    }

    private StemRecipe genUsingCraftingRecipes(ItemStack stack, boolean strict)
    {
        //Get all crafting recipes with the stack as a result
        List<IRecipe> recipes = findCraftingRecipes(stack, strict);
        if(!recipes.isEmpty())
        {
            //Iterate through all crafting recipes
            for(IRecipe recipe : recipes)
            {
                //If we end up trying to generate a recipe twice, then skip
                if(!checkedCraftingRecipes.add(recipe))
                    continue;
                //Get the ingredients of the recipe
                List ingredients = getIngredients(recipe);
                if(ingredients == null) continue;
                //Get the fluid amount required from the ingredients
                long fluidAmount = genFluidFromIngredients(ingredients);
                //Create a Stem recipe for the stack
                if(fluidAmount > 0)
                {
                    StemRecipe generatedRecipe = new StemRecipe(recipe.getRecipeOutput(), fluidAmount);
                    LogHelper.info("Generated stem recipe (%s) from a crafting recipe", generatedRecipe);
                    //Stop trying to gen from a crafting recipe
                    return generatedRecipe;
                }
                else if(fluidAmount == 0)
                    break;
            }
        }
        return null;
    }

    private StemRecipe genUsingFurnaceRecipes(ItemStack stack)
    {
        //Get all furnace recipe inputs with the stack as the output
        List<ItemStack> furnaceInputs = findFurnaceInputs(stack);
        if(!furnaceInputs.isEmpty())
        {
            //Iterate through all of the furnace recipe inputs
            for(ItemStack furnaceInput : furnaceInputs)
            {
                //If we end up trying to generate a recipe twice, then skip
                if(!checkedFurnaceRecipes.add(new Pair<>(furnaceInput, furnaceRecipes.get(furnaceInput))))
                    continue;
                //Get the fluid amount required from the ingredient
                long fluidAmount = genFluidFromIngredients(Collections.singletonList(furnaceInput));
                //Create a Stem recipe for the stack
                if(fluidAmount > 0)
                {
                    StemRecipe generatedRecipe = new StemRecipe(stack, fluidAmount);
                    LogHelper.info("Generated stem recipe (%s) from a furnace recipe", generatedRecipe);
                    //Stop trying to gen from a furnace recipe
                    return generatedRecipe;
                }
            }
        }
        return null;
    }

    private long genFluidFromIngredients(List ingredients)
    {
        //Make sure we have stem recipes already for all of the ingredients, and sum up the fluid requirements
        long fluidAmount = 0;
        for(Object ingredient : ingredients)
        {
            //If ingredient is null, then skip it (you get nulls in shaped recipes)
            if(ingredient == null) continue;

            long ingFluid = 0;
            if(ingredient instanceof ItemStack)
            {
                ItemStack stack = (ItemStack) ingredient;
                //Some modded recipes use an empty stack rather than null
                if(stack.isEmpty()) continue;

                ingFluid = genFluidFromStack(stack);
                if(ingFluid == 0) return 0;
            }
            else if(ingredient instanceof NonNullList)
            {
                NonNullList<ItemStack> list = (NonNullList<ItemStack>) ingredient;
                for(ItemStack stack : list)
                {
                    if(stack.isEmpty()) continue;
                    ingFluid = genFluidFromStack(stack);
                    if(ingFluid > 0) break;
                }
                if(ingFluid == 0) return 0;
            }
            else
                //If the ingredient isn't something we expect, then we can't use this recipe
                return 0;

            fluidAmount += ingFluid;
        }
        return fluidAmount;
    }

    private long genFluidFromStack(ItemStack stack)
    {
        //See if we already have a Stem recipe for this ingredient
        StemRecipe cachedRecipe = ServerRecipeManager.getRecipeFromCache(stack);
        long ingFluid = cachedRecipe == null ? 0 : cachedRecipe.getFluidInput();
        //If no stem recipe, try generate one
        if(ingFluid <= 0)
        {
            StemRecipe ingStemRecipe = genRecipeForStack(stack, false);
            //If no recipe was generated, then we cant use this recipe
            if(ingStemRecipe == null)
                return 0;
            //Add the Stem recipe to the server recipe cache
            addRecipe(ingStemRecipe);
            return ingStemRecipe.getFluidInput();
        }
        return ingFluid;
    }

    private void addRecipe(StemRecipe recipe)
    {
        if(recipe != null && ServerRecipeManager.addRecipe(recipe))
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

    private static List<IRecipe> findCraftingRecipes(ItemStack stack, boolean strict)
    {
        List<IRecipe> matchingRecipes = new ArrayList<>();
        craftingRecipes.forEach(recipe -> {
            if(OreDictionary.itemMatches(stack, recipe.getRecipeOutput(), strict))
                matchingRecipes.add(recipe);
        });
        return matchingRecipes;
    }

    private static List<ItemStack> findFurnaceInputs(ItemStack stack)
    {
        List<ItemStack> matchingInputs = new ArrayList<>();
        furnaceRecipes.forEach((input, output) -> {
            if(OreDictionary.itemMatches(stack, output, false))
                matchingInputs.add(input);
        });
        return matchingInputs;
    }
}
