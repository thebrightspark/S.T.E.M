package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMachine;
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

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        //TODO: Send progress data
        /*
        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = this.listeners.get(i);
            if (this.fuel != this.inventory.getField(0))
                icontainerlistener.sendProgressBarUpdate(this, 0, this.inventory.getField(0));
        }

        this.fuel = this.inventory.getField(0);
        */
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        //TODO: Set progress data
        this.inventory.setField(id, data);
    }
}
