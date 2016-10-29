package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.LogHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerLiquidEnergiser extends ContainerMachineBase
{
    public ContainerLiquidEnergiser(InventoryPlayer invPlayer, TileMachine machine)
    {
        super(invPlayer, machine);
    }

    @Override
    protected void addSlots()
    {
        //TODO: Add slots!
    }
}
