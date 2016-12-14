package brightspark.stem.item;

import brightspark.stem.init.StemBlocks;
import brightspark.stem.recipe.RecipeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
        setMaxStackSize(1);
    }

    public static boolean isMemoryEmpty(ItemStack memChipStack)
    {
        return memChipStack == null || getMemory(memChipStack) == null;
    }

    public static void setMemory(ItemStack memChipStack, ItemStack stack)
    {
        if(stack == null)
        {
            clearMemory(memChipStack);
            return;
        }
        NBTTagCompound memTag = memChipStack.getTagCompound();
        if(memTag == null)
            memTag = new NBTTagCompound();
        stack.writeToNBT(memTag);
        memChipStack.setTagCompound(memTag);
    }

    public static ItemStack getMemory(ItemStack memChipStack)
    {
        NBTTagCompound memTag = memChipStack.getTagCompound();
        return memTag == null ? null : ItemStack.loadItemStackFromNBT(memTag);
    }

    public static void clearMemory(ItemStack memChipStack)
    {
        memChipStack.setTagCompound(new NBTTagCompound());
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        stack.setTagCompound(new NBTTagCompound());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        //Clear memory on item
        if(player.isSneaking() && !isMemoryEmpty(stack))
        {
            clearMemory(stack);
            if(world.isRemote) player.addChatMessage(new TextComponentString("Memory Cleared!"));
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        ItemStack stackInMem = getMemory(stack);
        if(stackInMem != null)
        {
            tooltip.add(stackInMem.getDisplayName());
            int fluid = RecipeManager.getStemNeeded(stackInMem);
            if(fluid < 0)
            {
                tooltip.add("ERROR: Recipe not found for item");
                tooltip.add("Shift right click this item to clear the memory");
            }
            else
                tooltip.add(fluid + "mb");
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
        ItemStack stackInMem = getMemory(stack);
        if(stackInMem != null)
            return displayName + " (" + stackInMem.getDisplayName() + ")";
        return super.getHighlightTip(stack, displayName);
    }
}
