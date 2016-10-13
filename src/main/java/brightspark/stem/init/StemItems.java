package brightspark.stem.init;

import brightspark.stem.item.ItemWrench;
import brightspark.stem.util.ClientUtils;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class StemItems
{
    public static List<Item> ITEMS = new ArrayList<Item>();

    public static ItemWrench itemWrench;

    public static void registerItem(Item item)
    {
        GameRegistry.register(item);
        ITEMS.add(item);
    }



    public static void regItems()
    {
        registerItem(itemWrench = new ItemWrench());
    }

    public static void regModels()
    {
        for(Item item : ITEMS)
            ClientUtils.regModel(item);
    }
}
