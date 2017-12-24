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
    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private int activeThreads, recipesGenerated;

    public void generateRecipes(ICommandSender sender)
    {
        List<Item> allItems = Lists.newArrayList(Item.REGISTRY);
        //Split all registered items into lists of 10
        List<List<Item>> partitions = Lists.partition(allItems, 10);
        sender.sendMessage(new TextComponentString("Starting to process " + allItems.size() + " items in " + partitions.size() + " threads..."));
        //Run a task for each list in a new thread
        partitions.forEach(items -> {
            RecipeGenerateTask genTask = new RecipeGenerateTask(this, items);
            executor.execute(genTask);
            activeThreads++;
        });

        //Run a checker to detect when the tasks have finished
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if(activeThreads == 0)
            {
                sender.sendMessage(new TextComponentString("Generated " + recipesGenerated + " stem recipes"));
                executor.shutdown();
                scheduledExecutor.shutdown();
            }
        }, 1000, 500, TimeUnit.MILLISECONDS);
    }

    public void onTaskCompleted(int recipesGenerated)
    {
        this.recipesGenerated += recipesGenerated;
        activeThreads--;
    }
}
