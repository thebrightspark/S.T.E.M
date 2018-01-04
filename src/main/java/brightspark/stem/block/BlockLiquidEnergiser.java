package brightspark.stem.block;

import brightspark.stem.Config;
import brightspark.stem.gui.ContainerLiquidEnergiser;
import brightspark.stem.gui.GuiLiquidEnergiser;
import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.tileentity.TileMachineWithFluid;
import brightspark.stem.util.CommonUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockLiquidEnergiser extends AbstractBlockMachine<TileLiquidEnergiser>
{
    public BlockLiquidEnergiser()
    {
        super("liquid_energiser");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileLiquidEnergiser();
    }

    @SideOnly(Side.CLIENT)
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

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack heldItem = player.getHeldItem(hand);
        //Handle bucket click
        if(heldItem.getItem().equals(Items.BUCKET))
        {
            TileLiquidEnergiser machine = getTileEntity(world, pos);
            if(machine.getFluidAmount() >= Fluid.BUCKET_VOLUME)
            {
                machine.drainBucket();
                heldItem.shrink(1);
                ItemStack filledBucket = CommonUtils.createFilledBucket(machine.getFluidType());
                if(!player.inventory.addItemStackToInventory(filledBucket))
                    player.entityDropItem(filledBucket, 0f);
            }
            return true;
        }

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Progress: " + Math.round(((float) TileMachine.readEnergyFromStack(stack) / (float) Config.liquidEnergiserEnergyPerMb) * 100) + "%");
        FluidStack fluid = TileMachineWithFluid.readFluidFromStack(stack);
        if(fluid != null)
            tooltip.add(fluid.getLocalizedName() + ": " + CommonUtils.addDigitGrouping(fluid.amount) + "mb");
    }
}
