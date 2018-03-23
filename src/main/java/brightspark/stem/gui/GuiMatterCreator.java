package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMatterCreator;
import brightspark.stem.util.ClientUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import java.awt.*;

public class GuiMatterCreator extends GuiMachineBase<TileMatterCreator>
{
    protected Rectangle fluidBar = new Rectangle(8, 23, 16, 47);
    protected Rectangle arrow = new Rectangle(102, 39, 24, 17);

    public GuiMatterCreator(InventoryPlayer invPlayer, TileMatterCreator machine)
    {
        super(new ContainerMatterCreator(invPlayer, machine), "matter_creator");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        ClientUtils.drawGuiFluidBar(this, fluidBar, guiLeft, guiTop);
        ClientUtils.drawGuiProgressArrow(this, arrow, guiLeft, guiTop);
    }

    @Override
    protected void drawText()
    {
        super.drawText();
        fontRenderer.drawString(I18n.format("gui.progress") + " " + te.getProgressString(), 75, 22, textColour);
        int colour = te.getCreationStatusColour();
        fontRenderer.drawString(I18n.format("gui.status"), 75, 65, colour);
        fontRenderer.drawString(te.getCreationStatus(), 75, 77, colour);
    }

    @Override
    protected void drawTooltips(java.util.List<String> tooltip, int mouseX, int mouseY)
    {
        super.drawTooltips(tooltip, mouseX, mouseY);
        ClientUtils.drawGuiFluidTooltips(tooltip, this, fluidBar, mouseX, mouseY);
    }
}
