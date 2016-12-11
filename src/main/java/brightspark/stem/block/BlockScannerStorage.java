package brightspark.stem.block;

import brightspark.stem.tileentity.TileScannerStorage;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    /*
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(InventoryPlayer invPlayer, TileEntity te)
    {
        return new GuiMatterScanner(invPlayer, (TileMatterScanner) te);
    }

    @Override
    public Container getContainer(InventoryPlayer invPlayer, TileEntity te)
    {
        return new ContainerMatterScanner(invPlayer, (TileMatterScanner) te);
    }
    */
}
