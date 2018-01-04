package brightspark.stem.item;

import brightspark.stem.recipe.ClientRecipeCache;
import brightspark.stem.util.CommonUtils;
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
    public ItemMemoryChip()
    {
        super("mem_chip");
        setMaxStackSize(1);
    }

    public static boolean isMemoryEmpty(ItemStack memChipStack)
    {
        return getMemory(memChipStack).isEmpty();
    }

    public static void setMemory(ItemStack memChipStack, ItemStack stack)
    {
        if(stack.isEmpty())
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
        return memTag == null ? ItemStack.EMPTY : new ItemStack(memTag);
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        //Clear memory on item
        if(player.isSneaking() && !isMemoryEmpty(stack))
        {
            clearMemory(stack);
            if(world.isRemote) player.sendMessage(new TextComponentString("Memory Cleared!"));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        ItemStack stackInMem = getMemory(stack);
        if(!stackInMem.isEmpty())
        {
            tooltip.add(stackInMem.getDisplayName());
            long fluid = ClientRecipeCache.getFluidAmount(stackInMem);
            if(fluid < 0)
                tooltip.add("Waiting for fluid data from server...");
            else
                tooltip.add(CommonUtils.addDigitGrouping(fluid) + "mb");
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
        if(!stackInMem.isEmpty())
            return displayName + " (" + stackInMem.getDisplayName() + ")";
        return super.getHighlightTip(stack, displayName);
    }
}
