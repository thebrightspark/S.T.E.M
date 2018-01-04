package brightspark.stem.init;

import brightspark.stem.item.ItemBasicSubTypes;
import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.item.ItemWrench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class StemItems
{
    public static List<Item> ITEMS;

    public static ItemBasicSubTypes itemBasic;
    public static ItemMemoryChip itemMemoryChip;
    public static ItemWrench itemWrench;

    public static void addItem(Item item)
    {
        ITEMS.add(item);
    }

    public static void init()
    {
        ITEMS = new ArrayList<>();

        addItem(itemBasic = new ItemBasicSubTypes("basic",
                "blusteel_compound", "ingot_blusteel", "machine_processor", "internal_tank", "mem_bank",
                "large_mem_bank", "energy_circuit", "inf_device", "scan_device", "mem_reader", "mem_writer", "exciter",
                "former", "compressor"));
        addItem(itemMemoryChip = new ItemMemoryChip());
        addItem(itemWrench = new ItemWrench());
    }

    public static Item[] getItems()
    {
        if(ITEMS == null) init();
        return ITEMS.toArray(new Item[ITEMS.size()]);
    }

    public static void regOres()
    {
        for(int i = 0; i < itemBasic.getSubNames().length; i++)
            OreDictionary.registerOre(itemBasic.getSubNames()[i], new ItemStack(itemBasic, 1, i));
        OreDictionary.registerOre("mem_chip", itemMemoryChip);
    }
}
