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
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10, r -> new Thread(r, "StemRecipeGenerator"));
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
    private int activeThreads, recipesGeneratedTotal, recipeGeneratedLast, iterations;

    public void generateRecipes(ICommandSender sender)
    {
        runGenerationTasks();

        //Run a checker to detect when the tasks have finished
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if(activeThreads == 0)
            {
                if(recipeGeneratedLast == 0)
                {
                    //No recipes generated last time, so end generation
                    sender.sendMessage(new TextComponentString(String.format("Generated %s stem recipes in %s iterations.", recipesGeneratedTotal, iterations)));
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

        /*scheduledExecutor.schedule(() -> {
            if(activeThreads != 0)
            {
                sender.sendMessage(new TextComponentString("Threads haven't finished after 10 seconds, so shutting down threads." +
                        "\nManaged to generate " + recipesGenerated + " stem recipes though."));
                executor.shutdown();
                scheduledExecutor.shutdown();
            }
        }, 10, TimeUnit.SECONDS);*/
    }

    private void runGenerationTasks()
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
        executor.execute(new RecipeGenerateTask(this, allItems));
        activeThreads++;
    }

    public void onTaskCompleted(int recipesGenerated)
    {
        recipeGeneratedLast += recipesGenerated;
        activeThreads--;
    }
}
