package brightspark.stem.init;

import brightspark.stem.recipe.ServerRecipeManager;
import brightspark.stem.recipe.StemRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class StemRecipes
{
    public static void init()
    {
        //Wand (wrench)
        GameRegistry.addRecipe(new ShapedOreRecipe(StemItems.itemWrench, " bg", " pb", "b  ", 'b', "ingot_blusteel", 'g', Items.GLOWSTONE_DUST, 'p', "machine_processor"));
        //Blusteel Compound
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(StemItems.itemBasic, 1, 0), "ingotIron", "gemLapis", new ItemStack(Items.COAL, 1, 0), new ItemStack(Items.COAL, 1, 0)));
        //Blusteel Ingot
        GameRegistry.addSmelting(new ItemStack(StemItems.itemBasic, 1, 0), new ItemStack(StemItems.itemBasic, 1, 1), 1f);
        //Machine Block
        GameRegistry.addRecipe(new ShapedOreRecipe(StemBlocks.machineBlock, "ioi", "o o", "ioi", 'i', "ingot_blusteel", 'o', Blocks.IRON_BARS));
        //Machine Processor
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 2), "rgr", "qdq", "rgr", 'r', "dustRedstone", 'g', "ingotGold", 'q', "gemQuartz", 'd', "gemDiamond"));
        //Internal Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 3), "igi", "g g", "igi", 'i', "ingot_blusteel", 'g', "blockGlass"));
        //Memory Chip
        GameRegistry.addRecipe(new ShapedOreRecipe(StemItems.itemMemoryChip, "iqi", "idi", "igi", 'i', "ingot_blusteel", 'q', "gemQuartz", 'd', "gemDiamond", 'g', "ingotGold"));
        //Memory Bank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 4), "ici", "ccc", "ici", 'i', "ingot_blusteel", 'c', "memChip"));
        //Large Memory Bank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 5), "ibi", "bdb", "ibi", 'i', "ingot_blusteel", 'b', "mem_bank", 'd', "gemDiamond"));
        //Energy Input Circuit
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 6), "r", "g", "p", 'r', "dustRedstone", 'g', "ingotGold", 'p', "machine_processor"));
        //Infusion Device
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 7), "pcp", "g b", "pep", 'p', "gemPrismarine", 'c', "energy_circuit", 'g', "ingotGold", 'b', Items.BUCKET, 'e', Blocks.END_ROD));
        //Scanning Device
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 8), "ici", "glg", "ipi", 'i', "ingot_blusteel", 'c', "energy_circuit", 'g', "ingotGold", 'l', Blocks.REDSTONE_LAMP, 'p', "gemPrismarine"));
        //Memory Reader
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 9), " i ", "rpg", " i ", 'i', "ingot_blusteel", 'r', "dustRedstone", 'p', "machine_processor", 'g', "ingotGold"));
        //Memory Writer
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 10), " i ", "qpr", " i ", 'i', "ingot_blusteel", 'r', "dustRedstone", 'p', "machine_processor", 'q', "gemQuartz"));

        //S.T.E.M Exciter
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 11), "ici", "dpb", "iii", 'i', "ingot_blusteel", 'c', "energy_circuit", 'p', "machine_processor", 'd', "inf_device", 'b', Items.BUCKET));
        //Matter Former
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 12), "iei", "buc", "ipi", 'i', "ingot_blusteel", 'e', "energy_circuit", 'b', Items.BUCKET, 'u', "compressor", 'c', "chest", 'p', "machine_processor"));
        //Compression Unit
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 13), "opo", "pmp", "opo", 'o', "obsidian", 'p', Blocks.PISTON, 'm', "machine_processor"));

        //Liquid Energiser
        GameRegistry.addRecipe(new ShapedOreRecipe(StemBlocks.liquidEnergiser, " c ", "tbt", " i ", 'c', "energy_circuit", 't', "internal_tank", 'b', "machine_block", 'i', "inf_device"));
        //Matter Scanner
        GameRegistry.addRecipe(new ShapedOreRecipe(StemBlocks.matterScanner, " c ", " bm", " s ", 'c', "energy_circuit", 'm', "memChip", 'b', "machine_block", 's', "scan_device"));
        //Scanner Storage
        GameRegistry.addRecipe(new ShapedOreRecipe(StemBlocks.scannerStorage, "   ", "rbw", " m ", 'r', "mem_reader", 'w', "mem_writer", 'b', "machine_block", 'm', "large_mem_bank"));
        //Matter Creator
        GameRegistry.addRecipe(new ShapedOreRecipe(StemBlocks.matterCreator, " c ", "ebf", " t ", 'c', "energy_circuit", 'e', "exciter", 'b', "machine_block", 'f', "former"));
        //Liquid Compressor

    }

    public static void initServerRecipes()
    {
        //Register S.T.E.M fluid recipes
        ServerRecipeManager.addRecipe(new StemRecipe(Items.DIAMOND, 10));
    }
}
