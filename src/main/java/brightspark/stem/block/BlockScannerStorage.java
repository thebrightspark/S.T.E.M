package brightspark.stem.block;

import brightspark.stem.gui.ContainerScannerStorage;
import brightspark.stem.gui.GuiScannerStorage;
import brightspark.stem.tileentity.TileScannerStorage;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockScannerStorage extends AbstractBlockContainerDirectional<TileScannerStorage>
{
    public BlockScannerStorage()
    {
        super("scanner_storage", Material.ROCK);
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

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt != null)
        {
            int recipes = nbt.getTagList(TileScannerStorage.KEY_RECIPES, Constants.NBT.TAG_COMPOUND).tagCount();
            tooltip.add("Stored Recipes: " + recipes);
        }
        super.addInformation(stack, world, tooltip, flag);
    }
}
