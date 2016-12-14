package brightspark.stem.gui;

import brightspark.stem.Config;
import brightspark.stem.STEM;
import brightspark.stem.recipe.RecipeManager;
import brightspark.stem.recipe.StemRecipe;
import brightspark.stem.tileentity.TileScannerStorage;
import brightspark.stem.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GuiScannerStorage extends GuiContainer
{
    private static final ResourceLocation guiImage = new ResourceLocation(STEM.MOD_ID, STEM.GUI_TEXTURE_DIR + "scannerStorage.png");
    protected TileScannerStorage te;
    protected ContainerScannerStorage container;
    private static final int colourRed = 0x7A3D3D;
    private static final int colourBlue = 0x3B7177;
    private static final Rectangle recipeBox = new Rectangle(48, 23, 111, 47);

    public GuiScannerStorage(InventoryPlayer invPlayer, TileScannerStorage machine)
    {
        super(new ContainerScannerStorage(invPlayer, machine));
        te = machine;
        container = (ContainerScannerStorage) inventorySlots;
        xSize = 176;
        ySize = 175;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        //Add the buttons to the gui
        buttonList.add(new ArrowButton(0, 81, 76, true));
        buttonList.add(new ArrowButton(1, 109, 76, false));
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
        fontRendererObj.drawString(I18n.format(te.getBlockType().getUnlocalizedName() + ".name"), 8, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, 82, 4210752);

        List<ItemStack> recipes = te.getStoredRecipes();
        if(recipes == null || recipes.isEmpty())
            //No recipes stored
            fontRendererObj.drawString("No Recipes Stored", 50, 25, colourRed);
        else
        {
            //Display recipe
            StemRecipe currentRecipe = RecipeManager.getRecipeForStack(recipes.get(container.recipeSelected));

            //Draw index
            String numText = (container.recipeSelected + 1) + "/" + te.getStoredRecipes().size();
            int textWidth = fontRendererObj.getStringWidth(numText);
            int x = (recipeBox.width / 2) - (textWidth / 2) + recipeBox.x;
            fontRendererObj.drawString(numText, x, recipeBox.y + 2, colourBlue);

            if(currentRecipe == null)
            {
                //If the recipe has been removed while the GUI is open
                //Draw barrier for an "X"
                itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), recipeBox.x, recipeBox.y + 16);
                //Draw error text
                fontRendererObj.drawString("ERROR:", recipeBox.x + 18, recipeBox.y + 12, colourRed);
                fontRendererObj.drawString("Recipe removed", recipeBox.x + 18, recipeBox.y + 22, colourRed);
                fontRendererObj.drawString("Re-open GUI to fix", recipeBox.x + 18, recipeBox.y + 32, colourRed);
                return;
            }

            //Draw item
            itemRender.renderItemAndEffectIntoGUI(currentRecipe.getOutput(), recipeBox.x, recipeBox.y + 16);
            //Draw item name
            fontRendererObj.drawString(currentRecipe.getOutput().getDisplayName(), recipeBox.x + 18, recipeBox.y + 12, colourBlue);
            //Draw fluid needed
            fontRendererObj.drawString(currentRecipe.getFluidInput() + "mb", recipeBox.x + 18, recipeBox.y + 22, colourBlue);
            //Draw energy needed
            fontRendererObj.drawString(CommonUtils.addDigitGrouping(currentRecipe.getFluidInput() * Config.matterCreatorEnergyPerMb) + "RF", recipeBox.x + 18, recipeBox.y + 32, colourBlue);
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        inventorySlots.enchantItem(mc.thePlayer, button.id);
        mc.playerController.sendEnchantPacket(inventorySlots.windowId, button.id);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        //Update arrows
        buttonList.get(0).enabled = container.recipeSelected > 0;
        buttonList.get(1).enabled = container.recipeSelected < te.getStoredRecipes().size() - 1;
    }

    @SideOnly(Side.CLIENT)
    private class ArrowButton extends GuiButton
    {
        private int iconX = 176;

        public ArrowButton(int buttonId, int x, int y, boolean isLeftArrow)
        {
            super(buttonId, guiLeft + x, guiTop + y, 17, 11, "");
            iconX = isLeftArrow ? iconX : iconX + width;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY)
        {
            if(!visible) return;
            mc.getTextureManager().bindTexture(guiImage);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
            //Draw icon
            drawTexturedModalRect(xPosition, yPosition, iconX, enabled ? 0 : height, width, height);
        }
    }
}
