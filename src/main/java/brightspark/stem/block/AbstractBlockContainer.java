package brightspark.stem.block;

import brightspark.stem.STEM;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.LogHelper;
import brightspark.stem.util.WrenchHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class AbstractBlockContainer<T extends TileEntity> extends BlockContainer
{
    protected boolean hasGui = false;

    public AbstractBlockContainer(String name, Material mat)
    {
        super(mat);
        setCreativeTab(STEM.STEM_TAB);
        setUnlocalizedName(name);
        setRegistryName(name);
        setHardness(2f);
        setResistance(10f);
    }

    public T getTileEntity(IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if(!(te instanceof TileMachine))
        {
            LogHelper.error("Tile entity for block at position " + pos.toString() + " is not a TileMachine!");
            return null;
        }
        return (T) te;
    }

    public void setHasGui()
    {
        hasGui = true;
    }

    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(InventoryPlayer invPlayer, TileEntity te)
    {
        return null;
    }

    public Container getContainer(InventoryPlayer invPlayer, TileEntity te)
    {
        return null;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if(!hasGui)
            return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
        if(!player.isSneaking() && !WrenchHelper.isWrench(heldItem))
            player.openGui(STEM.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
