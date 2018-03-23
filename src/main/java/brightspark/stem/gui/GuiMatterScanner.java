package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMatterScanner;
import brightspark.stem.util.ClientUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import java.awt.*;

public class GuiMatterScanner extends GuiMachineBase<TileMatterScanner>
{
    protected Rectangle arrow = new Rectangle(73, 38, 24, 17);

    public GuiMatterScanner(InventoryPlayer invPlayer, TileMatterScanner machine)
    {
        super(new ContainerMatterScanner(invPlayer, machine), "matter_scanner");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        ClientUtils.drawGuiProgressArrow(this, arrow, guiLeft, guiTop);
    }

    @Override
    protected void drawText()
    {
        super.drawText();

        drawCenteredString(fontRenderer, I18n.format("gui.progress") + " " + te.getProgressString(), 20, textColour);
        int colour = te.getScanStatusColour();
        drawCenteredString(fontRenderer, I18n.format("gui.status") + " " + te.getScanStatus(), 66, colour);
    }
}
