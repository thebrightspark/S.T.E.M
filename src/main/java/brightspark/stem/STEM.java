package brightspark.stem;

import brightspark.stem.handler.ConfigHandler;
import brightspark.stem.handler.GuiHandler;
import brightspark.stem.handler.WrenchHandler;
import brightspark.stem.init.StemBlocks;
import brightspark.stem.init.StemFluids;
import brightspark.stem.init.StemItems;
import brightspark.stem.init.StemRecipes;
import brightspark.stem.recipe.CommandStem;
import brightspark.stem.recipe.ServerRecipeManager;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.LogHelper;
import brightspark.stem.util.WrenchHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

@Mod(modid = STEM.MOD_ID, name = STEM.MOD_NAME, version = STEM.VERSION)
public class STEM
{
    public static final String MOD_ID = "stem";
    public static final String MOD_NAME = "S.T.E.M";
    public static final String VERSION = "1.10.2-0.1.0";
    public static final String GUI_TEXTURE_DIR = "textures/gui/";
    public static File CONFIG_DIR;

    @Mod.Instance(MOD_ID)
    public static STEM instance;

    public static final CreativeTabs STEM_TAB = new CreativeTabs(MOD_ID)
    {
        @Override
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(StemFluids.fluidStem.getBlock());
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
        CONFIG_DIR = new File(event.getModConfigurationDirectory(), STEM.MOD_ID);
        if(!CONFIG_DIR.mkdirs())
            LogHelper.error("Config directory either already exists or couldn't be created");
        ConfigHandler.init(new File(CONFIG_DIR, "config.cfg"));
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
        CommonUtils.regNetwork();

        StemFluids.regFluids();
        StemItems.regItems();
        StemBlocks.regBlocks();

        WrenchHelper.addWrench(StemItems.itemWrench.getRegistryName().toString());

        if(event.getSide() == Side.CLIENT)
        {
            StemFluids.regModels();
            StemItems.regModels();
            StemBlocks.regModels();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //Initialize GUIs, tile entities, recipies, event handlers here

        StemRecipes.init();

        if(event.getSide() == Side.CLIENT)
            StemBlocks.regColours();
        StemBlocks.regTileEntities();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new WrenchHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //Run stuff after mods have initialized here

    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        //Register commands
        event.registerServerCommand(new CommandStem());

        //Init recipes
        ServerRecipeManager.init();
        StemRecipes.initServerRecipes();
        ServerRecipeManager.readRecipeFile();
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        //Save recipes to file
        ServerRecipeManager.saveRecipes();
    }
}
