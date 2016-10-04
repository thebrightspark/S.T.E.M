package brightspark.stem.block;

import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.LogHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractBlockMachine<T extends TileMachine> extends AbstractBlockContainer
{
    public AbstractBlockMachine(String name)
    {
        super(name, Material.ROCK);
        setHasGui();
    }

    public boolean canPickupWithWrench()
    {
        return true;
    }

    public T getTileEntity(IBlockAccess world, BlockPos pos)
    {
        return (T) world.getTileEntity(pos);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool)
    {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param state Current state
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        TileMachine machine = getTileEntity(world, pos);
        LogHelper.info("Getting Machine Drops - Used Wrench: " + machine.usedWrenchToBreak);
        if(machine.usedWrenchToBreak)
        {
            //Write energy to ItemStack
            ItemStack drop = new ItemStack(this, 1, damageDropped(state));
            writeNbtToDroppedStack(world, pos, state, machine, drop);
            return Lists.newArrayList(drop);
        }
        return super.getDrops(world, pos, state, fortune);
    }

    /**
     * Write any data about the block to the NBT of the dropped ItemStack
     */
    protected void writeNbtToDroppedStack(IBlockAccess world, BlockPos pos, IBlockState state, TileMachine machine, ItemStack drop)
    {
        LogHelper.info("Is Machine Null: " + (machine == null));
        if(machine != null)
            machine.writeEnergyToStack(drop);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, player, stack);
        LogHelper.info("Block Placed");
        if(world == null || pos == null) return;
        TileMachine machine = getTileEntity(world, pos);
        LogHelper.info("Block Placed - TE: " + (machine != null));
        if(machine == null) return;
        machine.getEnergyFromStack(stack);
        //if(!world.isRemote)
        //    world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        tooltip.add("Energy: " + TileMachine.readEnergyFromStack(stack));
        super.addInformation(stack, playerIn, tooltip, advanced);
    }
}
