package brightspark.stem.handler;

import brightspark.stem.recipe.ClientRecipeCache;
import brightspark.stem.util.LogHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod.EventBusSubscriber
public class EventHandler
{
    @SubscribeEvent
    public static void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        //Clear client cache so recipes are re-synced as needed
        LogHelper.info("Clearing client recipe cache");
        ClientRecipeCache.markRecipeDirty(null);
    }
}
