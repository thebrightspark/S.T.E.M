package brightspark.stem.item;

import brightspark.stem.util.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.List;

public class ItemMemoryChip extends ItemBasic
{
    private static final String KEY_ITEM_ID = "itemId";
    private static final String KEY_ITEM_META = "itemMeta";

    public ItemMemoryChip()
    {
        super("memChip");
    }

    public static boolean isMemoryEmpty(ItemStack memChipStack)
    {
        return memChipStack == null || NBTHelper.getString(memChipStack, KEY_ITEM_ID).equals("");
    }

    public static void setItemInMemory(ItemStack memChipStack, ItemStack stack)
    {
        if(stack == null)
        {
            NBTHelper.setString(memChipStack, KEY_ITEM_ID, "");
            NBTHelper.setInteger(memChipStack, KEY_ITEM_META, 0);
        }
        else
        {
            NBTHelper.setString(memChipStack, KEY_ITEM_ID, stack.getUnlocalizedName());
            int meta = stack.isItemStackDamageable() ? 0 : stack.getMetadata();
            NBTHelper.setInteger(memChipStack, KEY_ITEM_META, meta);
        }
    }

    public static ItemStack getItemFromMemory(ItemStack memChipStack)
    {
        String itemId = NBTHelper.getString(memChipStack, KEY_ITEM_ID);
        if(itemId.equals(""))
            return null;
        Item item = Item.getByNameOrId(itemId);
        if(item == null)
            return null;
        return new ItemStack(item, NBTHelper.getInt(memChipStack, KEY_ITEM_ID));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        //Clear memory on item
        if(player.isSneaking() && isMemoryEmpty(stack))
        {
            setItemInMemory(stack, null);
            if(world.isRemote) player.addChatMessage(new TextComponentString("Memory Cleared!"));
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        ItemStack stackInMem = getItemFromMemory(stack);
        if(stackInMem != null)
        {
            tooltip.add(stackInMem.getDisplayName());
            //TODO: Get fluid needed for item
        }
    }

    /**
     * Allow the item one last chance to modify its name used for the
     * tool highlight useful for adding something extra that can't be removed
     * by a user in the displayed name, such as a mode of operation.
     *
     * @param stack the ItemStack for the item.
     * @param displayName the name that will be displayed unless it is changed in this method.
     */
    @Override
    public String getHighlightTip(ItemStack stack, String displayName)
    {
        ItemStack stackInMem = getItemFromMemory(stack);
        if(stackInMem != null)
            return displayName + " (" + stackInMem.getDisplayName() + ")";
        return super.getHighlightTip(stack, displayName);
    }
}
