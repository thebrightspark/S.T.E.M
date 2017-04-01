package brightspark.stem.gui;

import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.util.ClientUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import java.awt.*;
import java.util.List;

public class GuiLiquidEnergiser extends GuiMachineBase
{
    public final Rectangle fluidBar = new Rectangle(98, 23, 16, 47);

    public GuiLiquidEnergiser(InventoryPlayer invPlayer, TileLiquidEnergiser machine)
    {
        super(new ContainerLiquidEnergiser(invPlayer, machine), "liquidEnergiser");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        ClientUtils.drawGuiFluidBar(this, fluidBar, guiLeft, guiTop);
    }

    @Override
    protected void drawText()
    {
        super.drawText();
        fontRendererObj.drawString(I18n.format("gui.progress"), 8, 30, textColour);
        fontRendererObj.drawString(te.getProgressString(), 8, 40, textColour);
        //fontRendererObj.drawString(I18n.format("gui.averageInput"), 8, 55, textColour);
        //fontRendererObj.drawString(((TileLiquidEnergiser) te).getAverageInputString(), 8, 65, textColour);
    }

    @Override
    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY)
    {
        super.drawTooltips(tooltip, mouseX, mouseY);
        ClientUtils.drawGuiFluidTooltips(tooltip, this, fluidBar, mouseX, mouseY);
    }
}
