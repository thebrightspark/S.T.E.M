package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMatterCreator;
import brightspark.stem.util.ClientUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import java.awt.*;

public class GuiMatterCreator extends GuiMachineBase
{
    protected Rectangle fluidBar = new Rectangle(33, 23, 16, 47);
    protected Rectangle arrow = new Rectangle(112, 38, 24, 17);

    public GuiMatterCreator(InventoryPlayer invPlayer, TileMatterCreator machine)
    {
        super(new ContainerMatterCreator(invPlayer, machine), "matterCreator");
        energyBar.setLocation(11, 18);
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
        fontRendererObj.drawString(I18n.format("gui.progress") + " " + te.getProgressString(), 84, 22, textColour);
        TileMatterCreator creator = (TileMatterCreator) te;
        int colour = creator.getCreationStatusColour();
        fontRendererObj.drawString(I18n.format("gui.status"), 84, 62, colour);
        fontRendererObj.drawString(creator.getCreationStatus(), 84, 74, colour);
    }

    @Override
    protected void drawTooltips(java.util.List<String> tooltip, int mouseX, int mouseY)
    {
        super.drawTooltips(tooltip, mouseX, mouseY);
        ClientUtils.drawGuiFluidTooltips(tooltip, this, fluidBar, mouseX, mouseY);
    }
}
