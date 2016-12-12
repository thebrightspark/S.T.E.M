package brightspark.stem.block;

import brightspark.stem.gui.ContainerScannerStorage;
import brightspark.stem.gui.GuiScannerStorage;
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
    public BlockScannerStorage()
    {
        super("scannerStorage", Material.ROCK);
        setHasGui();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileScannerStorage();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(InventoryPlayer invPlayer, TileEntity te)
    {
        return new GuiScannerStorage(invPlayer, (TileScannerStorage) te);
    }

    @Override
    public Container getContainer(InventoryPlayer invPlayer, TileEntity te)
    {
        return new ContainerScannerStorage(invPlayer, (TileScannerStorage) te);
    }
}
