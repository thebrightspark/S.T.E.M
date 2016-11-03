package brightspark.stem.item;

import brightspark.stem.block.AbstractBlockMachine;
import brightspark.stem.util.ClientUtils;
import brightspark.stem.util.NBTHelper;
import brightspark.stem.util.WrenchHelper.EnumWrenchMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemWrench extends ItemBasic
{
    //private static final String KEY_MODE = "mode";
    //private final int chatIdWrenchMode = ClientUtils.getNewChatMessageId();

    public ItemWrench()
    {
        super("wrench");
        setMaxStackSize(1);
    }

    //TEMP
    public static EnumWrenchMode getMode(ItemStack stack)
    {
        return EnumWrenchMode.TURN;
    }

    /*
    private static void setMode(ItemStack stack, EnumWrenchMode mode)
    {
        NBTHelper.setInteger(stack, KEY_MODE, mode.id);
    }

    private static void nextMode(ItemStack stack)
    {
        EnumWrenchMode mode = EnumWrenchMode.getById(NBTHelper.getInt(stack, KEY_MODE)).getNextMode();
        NBTHelper.setInteger(stack, KEY_MODE, mode.id);
    }

    public static EnumWrenchMode getMode(ItemStack stack)
    {
        return EnumWrenchMode.getById(NBTHelper.getInt(stack, KEY_MODE));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if(player.isSneaking())
        {
            RayTraceResult ray = rayTrace(world, player, false);
            if(ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK || !(world.getBlockState(ray.getBlockPos()).getBlock() instanceof AbstractBlockMachine))
            {
                //Change wrench mode
                nextMode(stack);
                if(world.isRemote)
                    ClientUtils.addClientChatMessage(new TextComponentString(getMode(stack).getDisplayPrefix()), chatIdWrenchMode);
                //player.addChatMessage(new TextComponentString(getMode(stack).getDisplayPrefix()));
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
            }
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    */

    /**
     * Allow the item one last chance to modify its name used for the
     * tool highlight useful for adding something extra that can't be removed
     * by a user in the displayed name, such as a mode of operation.
     *
     * @param stack the ItemStack for the item.
     * @param displayName the name that will be displayed unless it is changed in this method.
     */
    /*
    public String getHighlightTip(ItemStack stack, String displayName)
    {
        return getMode(stack).getDisplayPrefix() + displayName;
    }
    */
}
