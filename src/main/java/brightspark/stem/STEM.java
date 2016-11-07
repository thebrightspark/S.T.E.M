package brightspark.stem;

import brightspark.stem.handler.ConfigHandler;
import brightspark.stem.handler.GuiHandler;
import brightspark.stem.handler.WrenchHandler;
import brightspark.stem.init.StemBlocks;
import brightspark.stem.init.StemFluids;
import brightspark.stem.init.StemItems;
import brightspark.stem.init.StemRecipes;
import brightspark.stem.util.WrenchHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = STEM.MOD_ID, name = STEM.MOD_NAME, version = STEM.VERSION)
public class STEM
{
    public static final String MOD_ID = "stem";
    public static final String MOD_NAME = "S.T.E.M";
    public static final String VERSION = "1.10.2-0.0.2";
    public static final String GUI_TEXTURE_DIR = "textures/gui/";

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

        ConfigHandler.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());

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
}
