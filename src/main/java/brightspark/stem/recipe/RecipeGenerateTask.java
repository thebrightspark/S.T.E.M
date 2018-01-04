package brightspark.stem.recipe;

import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.LogHelper;
import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.server.FMLServerHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

public class RecipeGenerateTask implements Runnable
{
    private static List<IRecipe> craftingRecipes = Lists.newArrayList(CraftingManager.REGISTRY);
    private static Map<ItemStack, ItemStack> furnaceRecipes = FurnaceRecipes.instance().getSmeltingList();

    private ItemStack itemStack;
    private List<Item> items;
    private int recipesGenerated = 0;
    private Set<IRecipe> checkedCraftingRecipes = new HashSet<>();
    private Set<ImmutablePair<ItemStack, ItemStack>> checkedFurnaceRecipes = new HashSet<>();

    public RecipeGenerateTask(ItemStack stack)
    {
        itemStack = stack;
    }

    public RecipeGenerateTask(List<Item> items)
    {
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
                    item.getSubItems(CreativeTabs.SEARCH, stacks);
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

            RecipeGenerator.onTaskCompleted(recipesGenerated);
        }
    }

    private StemRecipe genRecipeForStack(ItemStack stack, boolean strict)
    {
        //If recipe already exists, just return it
        StemRecipe stemRecipe = ServerRecipeManager.getRecipeFromCache(stack);
        if(stemRecipe != null && stemRecipe.getFluidInput() > 0) return stemRecipe;

        LogHelper.info("Trying to generate recipe for %s", CommonUtils.stackToString(stack));

        //Try generate using crafting recipes
        stemRecipe = genUsingCraftingRecipes(stack, strict);
        if(stemRecipe != null) return stemRecipe;

        //Try generate using furnace recipes
        stemRecipe = genUsingFurnaceRecipes(stack);
        if(stemRecipe != null) return stemRecipe;

        //TODO: Other modded recipes?

        LogHelper.warn("Couldn't generate a recipe for %s", CommonUtils.stackToString(stack));
        ServerRecipeManager.addRecipe(new StemRecipe(stack, 0));
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
                List<Ingredient> ingredients = recipe.getIngredients();
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
                if(!checkedFurnaceRecipes.add(new ImmutablePair<>(furnaceInput, furnaceRecipes.get(furnaceInput))))
                    continue;
                //Get the fluid amount required from the ingredient
                long fluidAmount = genFluidFromIngredients(Collections.singletonList(Ingredient.fromStacks(furnaceInput)));
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

    private long genFluidFromIngredients(List<Ingredient> ingredients)
    {
        //Make sure we have stem recipes already for all of the ingredients, and sum up the fluid requirements
        long fluidAmount = 0;
        for(Ingredient ingredient : ingredients)
        {
            //If ingredient is null, then skip it
            if(ingredient == null || ingredient.getMatchingStacks().length == 0) continue;

            long ingFluid = 0;
            for(ItemStack stack : ingredient.getMatchingStacks())
            {
                ingFluid = genFluidFromStack(stack);
                if(ingFluid > 0) break;
            }
            if(ingFluid == 0) return 0;
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
