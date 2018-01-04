package brightspark.stem.recipe;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RecipeGenerator
{
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10, r -> new Thread(r, "StemRecipeGenerator"));
    private static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
    private static int activeThreads, recipesGeneratedTotal, recipeGeneratedLast, iterations;
    private static boolean active = false;

    public static void generateRecipes(ICommandSender sender)
    {
        if(active)
        {
            sender.sendMessage(new TextComponentString("Generation task already running."));
            return;
        }

        active = true;
        runGenerationTasks();

        //Run a checker to detect when the tasks have finished
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if(activeThreads == 0)
            {
                if(recipeGeneratedLast == 0)
                {
                    //No recipes generated last time, so end generation
                    sender.sendMessage(new TextComponentString(String.format("Generated %s stem recipes in %s iterations.", recipesGeneratedTotal, iterations)));
                    active = false;
                    executor.shutdown();
                    scheduledExecutor.shutdown();
                }
                else
                {
                    //Recipes generated - try generate again
                    recipesGeneratedTotal += recipeGeneratedLast;
                    recipeGeneratedLast = 0;
                    iterations++;
                    runGenerationTasks();
                }
            }
        }, 1000, 500, TimeUnit.MILLISECONDS);

        scheduledExecutor.schedule(() -> {
            if(activeThreads != 0)
            {
                sender.sendMessage(new TextComponentString("Threads haven't finished after 10 seconds, so shutting down threads." +
                        "\nManaged to generate " + recipesGeneratedTotal + " stem recipes though."));
                executor.shutdown();
                scheduledExecutor.shutdown();
            }
        }, 60, TimeUnit.SECONDS);
    }

    private static void runGenerationTasks()
    {
        List<Item> allItems = Lists.newArrayList(Item.REGISTRY);
        /*
        //Split all registered items into lists of 10
        List<List<Item>> partitions = Lists.partition(allItems, 10);
        sender.sendMessage(new TextComponentString("Starting to process " + allItems.size() + " items in " + partitions.size() + " threads..."));
        //Run a task for each list in a new thread
        partitions.forEach(items -> {
            RecipeGenerateTask genTask = new RecipeGenerateTask(this, items);
            executor.execute(genTask);
            activeThreads++;
        });
        */

        //TEMP
        executor.execute(new RecipeGenerateTask(allItems));
        activeThreads++;
    }

    public static void onTaskCompleted(int recipesGenerated)
    {
        recipeGeneratedLast += recipesGenerated;
        activeThreads--;
    }
}
