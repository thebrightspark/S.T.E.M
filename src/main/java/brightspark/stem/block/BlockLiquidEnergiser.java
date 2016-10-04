package brightspark.stem.block;

import brightspark.stem.tileentity.TileLiquidEnergiser;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLiquidEnergiser extends AbstractBlockMachineDirectional<TileLiquidEnergiser>
{
    public BlockLiquidEnergiser(String name)
    {
        super(name);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileLiquidEnergiser();
    }
}
