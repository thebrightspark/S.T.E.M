package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMatterScanner;
import brightspark.stem.util.ClientUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import java.awt.*;

public class GuiMatterScanner extends GuiMachineBase
{
    protected Rectangle arrow = new Rectangle(95, 34, 24, 17);

    public GuiMatterScanner(InventoryPlayer invPlayer, TileMatterScanner machine)
    {
        super(new ContainerMatterScanner(invPlayer, machine), "matterScanner");
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

        TileMatterScanner scanner = (TileMatterScanner) te;
        fontRendererObj.drawString(I18n.format("gui.progress") + " " + scanner.getProgressString(), 55, 20, textColour);
        int colour = scanner.getScanStatusColour();
        fontRendererObj.drawString(I18n.format("gui.status"), 55, 58, colour);
        fontRendererObj.drawString(scanner.getScanStatus(), 65, 70, colour);
    }
}
