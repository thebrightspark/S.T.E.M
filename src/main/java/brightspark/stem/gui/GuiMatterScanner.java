package brightspark.stem.gui;

import brightspark.stem.tileentity.TileMatterScanner;
import net.minecraft.entity.player.InventoryPlayer;

import java.awt.*;

public class GuiMatterScanner extends GuiMachineBase
{
    private static final int colourRed = 14509670; //DD6666
    private static final int colourGold = 16766720; //FFD700
    private static final int colourGreen = 6741350; //66DD66
    protected Rectangle arrow = new Rectangle(95, 34, 24, 17);

    public GuiMatterScanner(InventoryPlayer invPlayer, TileMatterScanner machine)
    {
        super(new ContainerMatterScanner(invPlayer, machine), "matterScanner");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        //Draw Progress Arrow
        int arrowWidth = Math.round(((float) ((TileMatterScanner) te).getScanProgress() / 100f) * arrow.width);
        drawTexturedModalRect(arrow.x + guiLeft, arrow.y + guiTop, 186, 0, arrowWidth, arrow.height);
    }

    @Override
    protected void drawText()
    {
        super.drawText();

        //TODO: Put the strings in the lang file instead

        TileMatterScanner scanner = (TileMatterScanner) te;
        fontRendererObj.drawString("Progress: " + scanner.getScanProgressString(), 70, 20, textColour);
        int progress = scanner.getScanProgress();
        int colour = progress == 0 ? colourRed : progress >= 100 ? colourGreen : colourGold;
        fontRendererObj.drawString("Status: " + scanner.getScanStatus(), 70, 58, colour);
    }
}
