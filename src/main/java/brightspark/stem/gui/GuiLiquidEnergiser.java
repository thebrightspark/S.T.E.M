package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMachine;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiLiquidEnergiser extends GuiMachineBase
{
    public GuiLiquidEnergiser(InventoryPlayer invPlayer, TileMachine machine)
    {
        super(invPlayer, machine, "liquidEnergiser");
        xSize = 176;
        ySize = 166;
    }
}
