package brightspark.stem.block;

import brightspark.stem.tileentity.TileLiquidEnergiser;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLiquidEnergiser extends AbstractBlockMachineDirectional<TileLiquidEnergiser>
{
    public BlockLiquidEnergiser()
    {
        super("liquidEnergiser");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileLiquidEnergiser();
    }
}
