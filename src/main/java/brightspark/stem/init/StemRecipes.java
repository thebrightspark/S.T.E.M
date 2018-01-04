package brightspark.stem.init;

import brightspark.stem.STEM;
import brightspark.stem.recipe.ServerRecipeManager;
import brightspark.stem.recipe.StemRecipe;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class StemRecipes
{
    private static ResourceLocation GROUP = new ResourceLocation(STEM.MOD_ID);
    public static List<IRecipe> RECIPES = new ArrayList<>();
    
    private static void addRecipe(IRecipe recipe)
    {
        RECIPES.add(recipe);
    }

    private static void addRecipe(ItemStack result, Object... recipe)
    {
        addRecipe(new ShapedOreRecipe(GROUP, result, recipe));
    }

    private static void addRecipe(Item result, Object... recipe)
    {
        addRecipe(new ShapedOreRecipe(GROUP, result, recipe));
    }

    private static void addRecipe(Block result, Object... recipe)
    {
        addRecipe(new ShapedOreRecipe(GROUP, result, recipe));
    }

    private static void addShapelessRecipe(ItemStack result, Object... recipe)
    {
        addRecipe(new ShapelessOreRecipe(GROUP, result, recipe));
    }

    private static void addShapelessRecipe(Item result, Object... recipe)
    {
        addRecipe(new ShapelessOreRecipe(GROUP, result, recipe));
    }

    private static void addShapelessRecipe(Block result, Object... recipe)
    {
        addRecipe(new ShapelessOreRecipe(GROUP, result, recipe));
    }
    
    public static void init()
    {
        //Wand (wrench)
        addRecipe(StemItems.itemWrench, " bg", " pb", "b  ", 'b', "ingot_blusteel", 'g', Items.GLOWSTONE_DUST, 'p', "machine_processor");
        //Blusteel Compound
        addShapelessRecipe(new ItemStack(StemItems.itemBasic, 1, 0), "ingotIron", "gemLapis", new ItemStack(Items.COAL, 1, 0), new ItemStack(Items.COAL, 1, 0));
        //Blusteel Ingot
        GameRegistry.addSmelting(new ItemStack(StemItems.itemBasic, 1, 0), new ItemStack(StemItems.itemBasic, 1, 1), 1f);
        //Machine Block
        addRecipe(StemBlocks.machineBlock, "ioi", "o o", "ioi", 'i', "ingot_blusteel", 'o', Blocks.IRON_BARS);
        //Machine Processor
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 2), "rgr", "qdq", "rgr", 'r', "dustRedstone", 'g', "ingotGold", 'q', "gemQuartz", 'd', "gemDiamond");
        //Internal Tank
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 3), "igi", "g g", "igi", 'i', "ingot_blusteel", 'g', "blockGlass");
        //Memory Chip
        addRecipe(StemItems.itemMemoryChip, "iqi", "idi", "igi", 'i', "ingot_blusteel", 'q', "gemQuartz", 'd', "gemDiamond", 'g', "ingotGold");
        //Memory Bank
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 4), "ici", "ccc", "ici", 'i', "ingot_blusteel", 'c', "mem_chip");
        //Large Memory Bank
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 5), "ibi", "bdb", "ibi", 'i', "ingot_blusteel", 'b', "mem_bank", 'd', "gemDiamond");
        //Energy Input Circuit
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 6), "r", "g", "p", 'r', "dustRedstone", 'g', "ingotGold", 'p', "machine_processor");
        //Infusion Device
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 7), "pcp", "g b", "pep", 'p', "gemPrismarine", 'c', "energy_circuit", 'g', "ingotGold", 'b', Items.BUCKET, 'e', Blocks.END_ROD);
        //Scanning Device
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 8), "ici", "glg", "ipi", 'i', "ingot_blusteel", 'c', "energy_circuit", 'g', "ingotGold", 'l', Blocks.REDSTONE_LAMP, 'p', "gemPrismarine");
        //Memory Reader
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 9), " i ", "rpg", " i ", 'i', "ingot_blusteel", 'r', "dustRedstone", 'p', "machine_processor", 'g', "ingotGold");
        //Memory Writer
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 10), " i ", "qpr", " i ", 'i', "ingot_blusteel", 'r', "dustRedstone", 'p', "machine_processor", 'q', "gemQuartz");

        //S.T.E.M Exciter
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 11), "ici", "dpb", "iii", 'i', "ingot_blusteel", 'c', "energy_circuit", 'p', "machine_processor", 'd', "inf_device", 'b', Items.BUCKET);
        //Matter Former
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 12), "iei", "buc", "ipi", 'i', "ingot_blusteel", 'e', "energy_circuit", 'b', Items.BUCKET, 'u', "compressor", 'c', "chest", 'p', "machine_processor");
        //Compression Unit
        addRecipe(new ItemStack(StemItems.itemBasic, 1, 13), "opo", "pmp", "opo", 'o', "obsidian", 'p', Blocks.PISTON, 'm', "machine_processor");

        //Liquid Energiser
        addRecipe(StemBlocks.liquidEnergiser, " c ", "tbt", " i ", 'c', "energy_circuit", 't', "internal_tank", 'b', "machine_block", 'i', "inf_device");
        //Matter Scanner
        addRecipe(StemBlocks.matterScanner, " c ", " bm", " s ", 'c', "energy_circuit", 'm', "mem_chip", 'b', "machine_block", 's', "scan_device");
        //Scanner Storage
        addRecipe(StemBlocks.scannerStorage, "   ", "rbw", " m ", 'r', "mem_reader", 'w', "mem_writer", 'b', "machine_block", 'm', "large_mem_bank");
        //Matter Creator
        addRecipe(StemBlocks.matterCreator, " c ", "ebf", " t ", 'c', "energy_circuit", 'e', "exciter", 'b', "machine_block", 'f', "former");
        //Liquid Compressor

    }
    
    public static IRecipe[] getRecipes()
    {
        if(RECIPES == null) init();
        return RECIPES.toArray(new IRecipe[RECIPES.size()]);
    }

    public static void initServerRecipes()
    {
        //Register S.T.E.M fluid recipes
        ServerRecipeManager.addRecipe(new StemRecipe(Items.DIAMOND, 10));
    }
}
