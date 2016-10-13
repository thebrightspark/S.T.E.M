package brightspark.stem.block;

import brightspark.stem.STEM;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.WrenchHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AbstractBlockContainer extends BlockContainer
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

    public void setHasGui()
    {
        hasGui = true;
    }

    public GuiScreen getGui(TileEntity te)
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
