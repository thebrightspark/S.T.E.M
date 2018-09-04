package brightspark.stem.item;

import brightspark.stem.STEM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemBasic extends Item
{
    public ItemBasic(String itemName)
    {
        setCreativeTab(STEM.STEM_TAB);
        setTranslationKey(itemName);
        setRegistryName(itemName);
    }

    @Override
    public RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
    {
        return super.rayTrace(worldIn, playerIn, useLiquids);
    }
}
