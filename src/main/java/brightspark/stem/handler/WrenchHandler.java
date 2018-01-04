package brightspark.stem.handler;

import brightspark.stem.block.AbstractBlockMachine;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.WrenchHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class WrenchHandler
{
    /*
    private static final int fontColour = 0xFFFFFF;

    @SubscribeEvent
    public static void renderOverlay(RenderGameOverlayEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        ItemStack heldItem = player.getHeldItemMainhand();
        if(heldItem != null && heldItem.getItem() instanceof ItemWrench && event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            //Do overlay render
            FontRenderer fontRenderer = mc.fontRendererObj;
            ScaledResolution res = event.getResolution();
            int xMid = res.getScaledWidth() / 2;
            int yMid = res.getScaledHeight() / 2 + 50;

            //Render wrench mode
            WrenchHelper.EnumWrenchMode wrenchMode = WrenchHelper.getWrenchMode(heldItem);
            String text = I18n.format("wrenchMode.overlay.mode") + " " + wrenchMode.toString();
            fontRenderer.drawStringWithShadow(text, xMid - (fontRenderer.getStringWidth(text) / 2), yMid, fontColour);

            //Render side permission for machine being looked at
            RayTraceResult ray = ((ItemWrench)heldItem.getItem()).rayTrace(mc.theWorld, player, false);
            if(ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK)
                return;
            IBlockState blockState = mc.theWorld.getBlockState(ray.getBlockPos());
            TileEntity te = mc.theWorld.getTileEntity(ray.getBlockPos());
            if(te == null || !(te instanceof TileMachine))
                return;
            //TODO: There's funny things going on with the side configs...
            TileMachine.EnumSidePerm side = ((TileMachine)te).getPermForSide(blockState, ray.sideHit);
            text = I18n.format("wrenchMode.overlay.side", CommonUtils.capitaliseFirstLetter(ray.sideHit.getName())) + " " + side.toString();
            if(blockState.getBlock() instanceof AbstractBlockMachineDirectional && blockState.getValue(AbstractBlockMachineDirectional.FACING) == ray.sideHit)
                text += " (Front)";
            fontRenderer.drawStringWithShadow(text, xMid - (fontRenderer.getStringWidth(text) / 2), yMid + 10, fontColour);
        }
    }
    */

    @SubscribeEvent
    public static void doWrenchBreak(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EntityPlayer player = event.getEntityPlayer();
        Block block = world.getBlockState(pos).getBlock();
        if(!WrenchHelper.isWrench(event.getItemStack()) || !player.isSneaking() ||
                !(block instanceof AbstractBlockMachine) ||
                !((AbstractBlockMachine) block).canPickupWithWrench())
            return;
        if(!world.isRemote)
        {
            //Break machine only on server
            IBlockState state = world.getBlockState(pos);
            AbstractBlockMachine machine = (AbstractBlockMachine) state.getBlock();
            TileEntity te = machine.getTileEntity(world, pos);
            if(!(te instanceof TileMachine))
                return;
            ((TileMachine) te).usedWrenchToBreak = true;
            if(machine.removedByPlayer(state, world, pos, player, true))
                machine.harvestBlock(world, player, pos, state, te, event.getItemStack());
        }
        event.setCanceled(true);
    }
}
