package brightspark.stem.item;

import brightspark.stem.ISubTypes;
import brightspark.stem.STEM;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemBasicSubTypes extends ItemBasic implements ISubTypes
{
    //protected final String TOOLTIP;
    protected String[] subNames;

    public ItemBasicSubTypes(String itemName, String... subNames)
    {
        super(itemName);
        //TOOLTIP = getUnlocalizedName() + ".tooltip.";
        setHasSubtypes(subNames != null && subNames.length > 0);
        this.subNames = hasSubtypes ? subNames : null;
    }

    /**
     * Returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        if(hasSubtypes)
            for(int i = 0; i < getSubNames().length; i++)
                subItems.add(new ItemStack(itemIn, 1, i));
        else
            subItems.add(new ItemStack(itemIn));
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        if(hasSubtypes)
            return super.getUnlocalizedName(stack) + "." + getSubNames()[stack.getMetadata()];
        return super.getUnlocalizedName(stack);
    }

    @Override
    public String[] getSubNames()
    {
        return subNames;
    }
}
