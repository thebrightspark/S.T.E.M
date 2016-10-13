package brightspark.stem.item;

import brightspark.stem.STEM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemBasic extends Item
{
    protected final String TOOLTIP;

    public ItemBasic(String itemName)
    {
        setCreativeTab(STEM.STEM_TAB);
        setUnlocalizedName(itemName);
        setRegistryName(itemName);
        TOOLTIP = getUnlocalizedName() + ".tooltip.";
    }

    @Override
    public RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
    {
        return super.rayTrace(worldIn, playerIn, useLiquids);
    }
}
