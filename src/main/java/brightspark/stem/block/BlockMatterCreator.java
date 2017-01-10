package brightspark.stem.block;

import brightspark.stem.gui.ContainerMatterCreator;
import brightspark.stem.gui.GuiMatterCreator;
import brightspark.stem.tileentity.TileMatterCreator;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMatterCreator extends AbstractBlockMachine<TileMatterCreator>
{
    public BlockMatterCreator()
    {
        super("matterCreator");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileMatterCreator();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(InventoryPlayer invPlayer, TileEntity te)
    {
        return new GuiMatterCreator(invPlayer, (TileMatterCreator) te);
    }

    @Override
    public Container getContainer(InventoryPlayer invPlayer, TileEntity te)
    {
        return new ContainerMatterCreator(invPlayer, (TileMatterCreator) te);
    }
}
