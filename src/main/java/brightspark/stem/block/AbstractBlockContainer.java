package brightspark.stem.block;

import brightspark.stem.STEM;
import brightspark.stem.tileentity.StemTileEntity;
import brightspark.stem.util.LogHelper;
import brightspark.stem.util.WrenchHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class AbstractBlockContainer<T extends StemTileEntity> extends BlockContainer
{
    protected boolean hasGui = false;

    public AbstractBlockContainer(String name, Material mat)
    {
        super(mat);
        setCreativeTab(STEM.STEM_TAB);
        setTranslationKey(name);
        setRegistryName(name);
        setHardness(2f);
        setResistance(10f);
    }

    public T getTileEntity(IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if(!(te instanceof StemTileEntity))
        {
            LogHelper.error("Tile entity for block at position " + pos.toString() + " is not a StemTileEntity!");
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if(!hasGui)
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        if(!player.isSneaking() && !WrenchHelper.isWrench(player.getHeldItem(hand)))
            player.openGui(STEM.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, player, stack);
        if(world == null || pos == null) return;
        T machine = getTileEntity(world, pos);
        if(machine == null) return;
        machine.readDataFromStack(stack);
    }

    /**
     * My own getDrops which is used when block is broken with a wrench
     */
    public NonNullList<ItemStack> getDrops(IBlockState state, T te)
    {
        NonNullList<ItemStack> drops = NonNullList.create();
        if(te != null && te.usedWrenchToBreak)
        {
            //Write data to ItemStack
            ItemStack drop = new ItemStack(this, 1, damageDropped(state));
            writeNbtToDroppedStack(te, drop);
            drops.add(drop);
        }
        return drops;
    }

    /**
     * Write any data about the block to the NBT of the dropped ItemStack
     */
    protected void writeNbtToDroppedStack(T machine, ItemStack drop)
    {
        if(machine != null)
            machine.writeDataToStack(drop);
    }

    public boolean canPickupWithWrench()
    {
        return true;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        //Spawn drops manually here, since the TE has already been removed if picked up with wrench
        if(te instanceof StemTileEntity && ((StemTileEntity) te).usedWrenchToBreak)
        {
            player.addStat(StatList.getBlockStats(state.getBlock()));
            player.addExhaustion(0.005F);

            NonNullList<ItemStack> drops = getDrops(state, (T) te);
            harvesters.set(player);
            if(!worldIn.restoringBlockSnapshots)
            {
                float chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, worldIn, pos, state, 0, 1F, false, harvesters.get());
                for(ItemStack drop : drops)
                    if(worldIn.rand.nextFloat() <= chance)
                        spawnAsEntity(worldIn, pos, drop);
            }
            harvesters.set(null);
        }
        else
            super.harvestBlock(worldIn, player, pos, state, te, stack);
    }
}
