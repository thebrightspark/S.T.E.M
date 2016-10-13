package brightspark.stem.gui;

import brightspark.stem.STEM;
import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.tileentity.TileMachineWithFluid;
import brightspark.stem.util.CommonUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiEnergyStorage extends GuiScreen
{
    private static final ResourceLocation texture = new ResourceLocation(STEM.MOD_ID, STEM.GUI_TEXTURE_DIR + "energyStorage.png");
    private TileMachine machine;
    private int xSize = 78;
    private int ySize = 147;
    private int xBarSize = 30;
    private int yBarSize = 100;

    public GuiEnergyStorage(TileMachine machine)
    {
        this.machine = machine;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        //Draw gui background
        mc.getTextureManager().bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        //Draw power bar
        int pixelsHigh = Math.round(yBarSize * machine.getEnergyPercentFloat());
        int correctYPos = yBarSize - pixelsHigh;
        drawTexturedModalRect(x + 24, y + 24 + correctYPos, 78, correctYPos, xBarSize, pixelsHigh);

        //Draw text
        int textColour = 0xBBBBBB;
        drawCenteredString(fontRendererObj, machine.getBlockType().getLocalizedName(), x + 39, y + 3, textColour);
        drawCenteredString(fontRendererObj, CommonUtils.addDigitGrouping(machine.getMaxEnergyStored(null)), x + 39, y + 13, textColour);
        drawCenteredString(fontRendererObj, CommonUtils.addDigitGrouping(machine.getEnergyStored(null)), x + 39, y + 127, textColour);
        drawCenteredString(fontRendererObj, machine.getEnergyPercentString(), x + 39, y + 137, textColour);
        if(machine instanceof TileMachineWithFluid)
            drawCenteredString(fontRendererObj, Integer.toString(((TileMachineWithFluid)machine).getFluidAmount()), x + 39, y + 157, textColour);
        if(machine instanceof TileLiquidEnergiser)
            drawCenteredString(fontRendererObj, ((TileLiquidEnergiser)machine).getProgressPercentString(), x + 39, y + 167, textColour);
    }
}
