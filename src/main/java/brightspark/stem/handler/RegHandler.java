package brightspark.stem.handler;

import brightspark.stem.init.StemBlocks;
import brightspark.stem.init.StemFluids;
import brightspark.stem.init.StemItems;
import brightspark.stem.init.StemRecipes;
import brightspark.stem.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class RegHandler
{
    @SubscribeEvent
    public static void initItems(Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(StemItems.getItems());
        registry.registerAll(StemBlocks.getItemBlocks());

        StemFluids.regItems(registry);
    }

    @SubscribeEvent
    public static void initBlocks(Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.registerAll(StemBlocks.getBlocks());

        StemFluids.regBlocks(registry);
        StemBlocks.regTileEntities();
    }

    @SubscribeEvent
    public static void initRecipes(Register<IRecipe> event)
    {
        IForgeRegistry<IRecipe> registry = event.getRegistry();
        registry.registerAll(StemRecipes.getRecipes());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void regModels(ModelRegistryEvent event)
    {
        StemItems.ITEMS.forEach(ClientUtils::regModel);
        StemBlocks.BLOCKS.forEach(ClientUtils::regModel);
        ClientUtils.regModel(StemFluids.fluidStem);
    }
}
