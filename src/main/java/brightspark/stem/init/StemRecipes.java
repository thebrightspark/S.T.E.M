package brightspark.stem.init;

import brightspark.stem.recipe.RecipeManager;
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
        GameRegistry.addRecipe(new ShapedOreRecipe(StemItems.itemWrench, " bg", " pb", "b  ", 'b', "ingotBlusteel", 'g', Items.GLOWSTONE_DUST, 'p', "machineProcessor"));
        //Blusteel Compound
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(StemItems.itemBasic, 1, 0), "ingotIron", "gemLapis", new ItemStack(Items.COAL, 1, 0), new ItemStack(Items.COAL, 1, 0)));
        //Blusteel Ingot
        GameRegistry.addSmelting(new ItemStack(StemItems.itemBasic, 1, 0), new ItemStack(StemItems.itemBasic, 1, 1), 1f);
        //Machine Block
        GameRegistry.addRecipe(new ShapedOreRecipe(StemBlocks.machineBlock, "ioi", "o o", "ioi", 'i', new ItemStack(StemItems.itemBasic, 1, 1), 'o', Blocks.IRON_BARS));
        //Machine Processor
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 2), "rgr", "qdq", "rgr", 'r', "dustRedstone", 'g', "ingotGold", 'q', "gemQuartz", 'd', "gemDiamond"));
        //Internal Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 3), "igi", "g g", "igi", 'i', "ingotBlusteel", 'g', "blockGlass"));
        //Memory Chip
        GameRegistry.addRecipe(new ShapedOreRecipe(StemItems.itemMemoryChip, "iqi", "idi", "igi", 'i', "ingotBlusteel", 'q', "gemQuartz", 'd', "gemDiamond", 'g', "ingotGold"));
        //Memory Bank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 5), "ici", "ccc", "ici", 'i', "ingotBlusteel", 'c', "memChip"));
        //Large Memory Bank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 6), "ibi", "bdb", "ibi", 'i', "ingotBlusteel", 'b', "memBank", 'd', "gemDiamond"));
        //Energy Input Circuit
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 7), "r", "g", "p", 'r', "dustRedstone", 'g', "ingotGold", 'p', "machineProcessor"));
        //Infusion Device
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 8), "pcp", "g b", "pep", 'p', "gemPrismarine", 'c', "energyCircuit", 'g', "ingotGold", 'b', Items.BUCKET, 'e', Blocks.END_ROD));
        //Scanning Device
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StemItems.itemBasic, 1, 9), "ici", "glg", "ipi", 'i', "ingotBlusteel", 'c', "energyCircuit", 'g', "ingotGold", 'l', Blocks.REDSTONE_LAMP, 'p', "gemPrismarine"));

        //Liquid Energiser
        GameRegistry.addRecipe(new ShapedOreRecipe(StemBlocks.liquidEnergiser, " c ", "tbt", " i ", 'c', "energyCircuit", 't', "internalTank", 'b', "machineBlock", 'i', "infDevice"));



        //Register S.T.E.M fluid recipes
        RecipeManager.addRecipe(new StemRecipe(Items.DIAMOND, 10));
    }
}
