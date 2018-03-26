package brightspark.stem;

import brightspark.stem.handler.ConfigHandler;
import brightspark.stem.handler.GuiHandler;
import brightspark.stem.init.StemBlocks;
import brightspark.stem.init.StemFluids;
import brightspark.stem.init.StemItems;
import brightspark.stem.recipe.CommandStem;
import brightspark.stem.recipe.ServerRecipeManager;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.LogHelper;
import brightspark.stem.util.WrenchHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

@Mod(modid = STEM.MOD_ID, name = STEM.MOD_NAME, version = STEM.VERSION, dependencies = STEM.DEPENDENCIES)
public class STEM
{
    public static final String MOD_ID = "stem";
    public static final String MOD_NAME = "S.T.E.M";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "after:redstoneflux";
    public static final String GUI_TEXTURE_DIR = "textures/gui/";
    public static File CONFIG_DIR;

    @Mod.Instance(MOD_ID)
    public static STEM instance;

    public static final CreativeTabs STEM_TAB = new CreativeTabs(MOD_ID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(StemFluids.fluidStem.getBlock());
        }

        @Override
        public String getTranslatedTabLabel()
        {
            return MOD_NAME;
        }
    };

    static
    {
        //Enable universal buckets for S.T.E.M liquid
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Initialize item, blocks, textures/models and configs here
        LogHelper.setLogger(event.getModLog());

        CONFIG_DIR = new File(event.getModConfigurationDirectory(), STEM.MOD_ID);
        if(!CONFIG_DIR.mkdirs())
            LogHelper.error("Config directory either already exists or couldn't be created");
        ConfigHandler.init(new File(CONFIG_DIR, "config.cfg"));
        CommonUtils.regNetwork();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //Initialize GUIs, tile entities, recipies, event handlers here

        GameRegistry.addSmelting(new ItemStack(StemItems.itemBasic, 1, 0), new ItemStack(StemItems.itemBasic, 1, 1), 1f);
        StemBlocks.regOres();
        StemItems.regOres();
        WrenchHelper.addWrench(StemItems.itemWrench.getRegistryName().toString());

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //Run stuff after mods have initialized here

        //Init recipes
        ServerRecipeManager.init();
        ServerRecipeManager.readRecipeFile();

        StemItems.ITEMS = null;
        StemBlocks.BLOCKS = null;
        StemBlocks.ITEM_BLOCKS = null;
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        //Register commands
        event.registerServerCommand(new CommandStem());
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        //Save recipes to file
        ServerRecipeManager.saveRecipes();
    }
}
