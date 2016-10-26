package brightspark.stem.gui;

import brightspark.stem.STEM;
import brightspark.stem.tileentity.TileMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiMachineBase extends GuiContainer
{
    public static ResourceLocation guiImage;
    protected TileMachine te;

    public GuiMachineBase(InventoryPlayer invPlayer, TileMachine machine, String guiImageName)
    {
        super(new ContainerMachineBase(invPlayer, machine));
        guiImage = new ResourceLocation(STEM.MOD_ID, STEM.GUI_TEXTURE_DIR + guiImageName + ".png");
        te = machine;
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
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        //Draw bars
        //TODO: Draw bars

        //Draw text
        int textColour = 4210752;
        fontRendererObj.drawString(I18n.format(te.getBlockType().getUnlocalizedName() + ".name"), 8, 6, textColour);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, 73, textColour);
    }
}
