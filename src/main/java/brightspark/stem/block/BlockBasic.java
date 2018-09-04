package brightspark.stem.block;

import brightspark.stem.STEM;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBasic extends Block
{
    public BlockBasic(String name, Material mat)
    {
        super(mat);
        setCreativeTab(STEM.STEM_TAB);
        setTranslationKey(name);
        setRegistryName(name);
        setHardness(2f);
        setResistance(10f);
    }
}
