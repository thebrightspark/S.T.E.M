package brightspark.stem.item;

import brightspark.stem.ISubTypes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if(isInCreativeTab(tab))
        {
            if(hasSubtypes)
                for(int i = 0; i < getSubNames().length; i++)
                    subItems.add(new ItemStack(this, 1, i));
            else
                subItems.add(new ItemStack(this));
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    @Override
    public String getTranslationKey(ItemStack stack)
    {
        if(hasSubtypes)
        {
            int meta = stack.getMetadata();
            String[] names = getSubNames();
            if(meta >= 0 && meta < names.length)
                return super.getTranslationKey(stack) + "." + names[meta];
        }
        return super.getTranslationKey(stack);
    }

    @Override
    public String[] getSubNames()
    {
        return subNames;
    }
}
