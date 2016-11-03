package brightspark.stem.init;

import brightspark.stem.item.ItemBasic;
import brightspark.stem.item.ItemWrench;
import brightspark.stem.util.ClientUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class StemItems
{
    public static List<Item> ITEMS = new ArrayList<Item>();

    public static ItemBasic itemBasic;
    public static ItemWrench itemWrench;

    public static void registerItem(Item item)
    {
        GameRegistry.register(item);
        ITEMS.add(item);
    }

    public static void regItems()
    {
        registerItem(itemBasic = new ItemBasic("basic",
                "blusteelCompound", "ingotBlusteel", "machineProcessor", "internalTank", "memChip", "memBank",
                "largeMemBank", "energyCircuit", "infDevice", "scanDevice"));
        registerItem(itemWrench = new ItemWrench());

        for(int i = 0; i < itemBasic.getSubNames().length; i++)
            OreDictionary.registerOre(itemBasic.getSubNames()[i], new ItemStack(itemBasic, 1, i));
    }

    public static void regModels()
    {
        for(Item item : ITEMS)
            ClientUtils.regModel(item);
    }
}
