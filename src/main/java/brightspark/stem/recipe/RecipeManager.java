package brightspark.stem.recipe;

import brightspark.stem.STEM;
import brightspark.stem.util.LogHelper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeManager
{
    public static final char csvSeparator = ',';
    public static File stemFolder;
    private static File stemRecipeFile;
    private static List<StemRecipe> recipes;

    public static void addRecipe(StemRecipe recipe)
    {
        recipes.add(recipe);
    }

    /**
     * Get the amount of S.T.E.M needed to make the item.
     * Returns -1 if there's no recipe for the item.
     */
    public static int getStemNeeded(ItemStack stack)
    {
        for(StemRecipe recipe : recipes)
            if(recipe.isStackEqual(stack))
                return recipe.getFluidInput();
        return -1;
    }

    /**
     * Checks if a recipe exists for the given item.
     */
    public static boolean hasRecipeForStack(ItemStack stack)
    {
        return getRecipeForStack(stack) != null;
    }

    /**
     * Gets the recipe for the given item stack.
     * Returns null if no recipe is found.
     */
    public static StemRecipe getRecipeForStack(ItemStack stack)
    {
        for(StemRecipe recipe : recipes)
            if(recipe.isStackEqual(stack))
                return recipe;
        return null;
    }

    public static void init(FMLPreInitializationEvent event)
    {
        recipes = new ArrayList<StemRecipe>();
        stemFolder = new File(event.getModConfigurationDirectory(), STEM.MOD_ID);
        if(!stemFolder.mkdirs())
            LogHelper.error("Config directory couldn't be created! This will probably cause things to break later!");
        stemRecipeFile = new File(stemFolder, "recipes.csv");
    }

    public static void postInit()
    {
        if(!stemRecipeFile.exists())
        {
            //If recipe file doesn't exist, then create the default file.
            createRecipeFile();
        }
        else
        {
            //If recipe file does exist, then read it and use it for the recipes.
            LogHelper.info("Reading existing recipe file...");

            CSVReader reader;
            try
            {
                reader = new CSVReader(new FileReader(stemRecipeFile), csvSeparator, '\"', 3);
            }
            catch(IOException e)
            {
                LogHelper.error("Couldn't get recipe file! It may not exist or be inaccessible. Creating default recipe file.");
                createRecipeFile();
                return;
            }

            //Try read the file
            List<String[]> fromFile;
            try
            {
                fromFile = reader.readAll();
            }
            catch(IOException e)
            {
                LogHelper.error("Couldn't read recipe file! Creating default recipe file.");
                closeReader(reader);
                createRecipeFile();
                return;
            }
            closeReader(reader);

            //Save recipes from file as recipe objects in the recipe array
            recipes.clear();
            for(String[] r : fromFile)
                recipes.add(StemRecipe.fromCsvStringArray(r));

            LogHelper.info("Recipe file read successfully.");

            for(StemRecipe r : recipes)
                LogHelper.info(r.toString());
        }
    }

    private static void closeReader(CSVReader reader)
    {
        try
        {
            reader.close();
        }
        catch(IOException e)
        {
            throw new RuntimeException("Couldn't close CSV Reader");
        }
    }

    private static void createRecipeFile()
    {
        LogHelper.info("Creating default recipe file...");

        CSVWriter writer;
        try
        {
            writer = new CSVWriter(new FileWriter(stemRecipeFile), csvSeparator);
        }
        catch(IOException e)
        {
            LogHelper.error("Couldn't create default recipe file!");
            return;
        }

        //Write 'header'
        writer.writeNext(new String[] {"Put in here the recipes for converting S.T.E.M fluid into items"});
        writer.writeNext(new String[] {"Output Item ID", "Output Item Metadata", "Input fluid amount"});
        writer.writeNext(new String[] {""});

        //Write recipes
        for(StemRecipe r : recipes)
            writer.writeNext(r.toCsvStringArray());

        try
        {
            writer.close();
        }
        catch(IOException e)
        {
            throw new RuntimeException("Couldn't close CSV Writer");
        }

        LogHelper.info("Recipe file created successfully.");
    }
}
