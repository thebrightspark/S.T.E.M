package brightspark.stem.block;

import brightspark.stem.gui.ContainerLiquidEnergiser;
import brightspark.stem.gui.GuiLiquidEnergiser;
import brightspark.stem.tileentity.TileLiquidEnergiser;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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

    @Override
    public GuiScreen getGui(InventoryPlayer invPlayer, TileEntity te)
    {
        return new GuiLiquidEnergiser(invPlayer, (TileLiquidEnergiser) te);
    }

    @Override
    public Container getContainer(InventoryPlayer invPlayer, TileEntity te)
    {
        return new ContainerLiquidEnergiser(invPlayer, (TileLiquidEnergiser) te);
    }
}
