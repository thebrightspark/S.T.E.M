package brightspark.stem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = STEM.MOD_ID, name = STEM.MOD_NAME, version = STEM.VERSION)
public class STEM
{
    public static final String MOD_ID = "stem";
    public static final String MOD_NAME = "S.T.E.M";
    public static final String VERSION = "1.10.2-1.0.0";

    @Mod.Instance(MOD_ID)
    public static STEM instance;

    public static final CreativeTabs STEM_TAB = new CreativeTabs(MOD_ID)
    {
        @Override
        public Item getTabIconItem()
        {
            return Items.ENDER_EYE;
        }

        @Override
        public String getTranslatedTabLabel()
        {
            return MOD_NAME;
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Initialize item, blocks and configs here

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //Initialize textures/models, GUIs, tile entities, recipies, event handlers here

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //Run stuff after mods have initialized here

    }
}
