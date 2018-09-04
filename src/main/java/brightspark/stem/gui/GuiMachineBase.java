package brightspark.stem.gui;

import brightspark.stem.STEM;
import brightspark.stem.tileentity.StemTileEntity;
import brightspark.stem.tileentity.TileMachine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiMachineBase<T extends StemTileEntity> extends GuiContainer
{
    public ResourceLocation guiImage;
    protected int textColour = 4210752;
    public T te;

    public GuiMachineBase(ContainerMachineBase<T> container, String guiImageName)
    {
        super(container);
        guiImage = new ResourceLocation(STEM.MOD_ID, STEM.GUI_TEXTURE_DIR + guiImageName + ".png");
        te = container.inventory;
        xSize = 176;
        ySize = 175;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        //Draw gui
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(guiImage);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        drawText();

        mouseX -= guiLeft;
        mouseY -= guiTop;
        List<String> tooltip = new ArrayList<>();
        drawTooltips(tooltip, mouseX, mouseY);
        if(!tooltip.isEmpty())
            drawHoveringText(tooltip, mouseX, mouseY);
    }

    protected void drawText()
    {
        fontRenderer.drawString(I18n.format(te.getBlockType().getTranslationKey() + ".name"), 8, 6, textColour);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, 82, textColour);
    }

    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY) {}

    public void drawCenteredString(FontRenderer fontRendererIn, String text, int y, int color)
    {
        fontRendererIn.drawString(text, (xSize / 2) - (fontRendererIn.getStringWidth(text) / 2), y, color);
    }

    @Override
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawString(text, x - (fontRendererIn.getStringWidth(text) / 2), y, color);
    }
}
