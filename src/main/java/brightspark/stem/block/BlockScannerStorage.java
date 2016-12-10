package brightspark.stem.block;

import brightspark.stem.tileentity.TileScannerStorage;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockScannerStorage extends AbstractBlockContainer
{
    public BlockScannerStorage(String name, Material mat)
    {
        super(name, mat);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileScannerStorage();
    }
}
