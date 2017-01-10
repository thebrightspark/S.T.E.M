package brightspark.stem.gui;

import brightspark.stem.STEM;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.CommonUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiMachineBase extends GuiContainer
{
    public ResourceLocation guiImage;
    protected int textColour = 4210752;
    public TileMachine te;
    protected final Rectangle energyBar = new Rectangle(29, 18, 10, 42);

    public GuiMachineBase(ContainerMachineBase container, String guiImageName)
    {
        super(container);
        guiImage = new ResourceLocation(STEM.MOD_ID, STEM.GUI_TEXTURE_DIR + guiImageName + ".png");
        te = (TileMachine) container.inventory;
        xSize = 176;
        ySize = 175;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        //Draw gui
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(guiImage);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        //Draw energy bar
        int pixelsHigh = Math.round(energyBar.height * te.getEnergyPercentFloat());
        int correctYPos = energyBar.height - pixelsHigh;
        drawTexturedModalRect(guiLeft + energyBar.x, guiTop + energyBar.y + correctYPos, 176, correctYPos, energyBar.width, pixelsHigh);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        drawText();

        mouseX -= guiLeft;
        mouseY -= guiTop;
        List<String> tooltip = new ArrayList<String>();
        drawTooltips(tooltip, mouseX, mouseY);
        if(!tooltip.isEmpty())
            drawHoveringText(tooltip, mouseX, mouseY);
    }

    protected void drawText()
    {
        fontRendererObj.drawString(I18n.format(te.getBlockType().getUnlocalizedName() + ".name"), 8, 6, textColour);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, 82, textColour);
    }

    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY)
    {
        if(energyBar.contains(mouseX, mouseY))
        {
            tooltip.add("Energy: " + CommonUtils.addDigitGrouping(te.getEnergyStored(null)) + " RF");
            tooltip.add("Max: " + CommonUtils.addDigitGrouping(te.getMaxEnergyStored(null)) + " RF");
        }
    }
}
