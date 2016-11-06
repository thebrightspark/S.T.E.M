package brightspark.stem.gui;

import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.tileentity.TileMachineWithFluid;
import brightspark.stem.util.CommonUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.Fluid;

import java.awt.*;
import java.util.List;

public class GuiLiquidEnergiser extends GuiMachineBase
{
    protected Rectangle fluidBar = new Rectangle(98, 23, 16, 47);

    public GuiLiquidEnergiser(InventoryPlayer invPlayer, TileLiquidEnergiser machine)
    {
        super(new ContainerLiquidEnergiser(invPlayer, machine), "liquidEnergiser");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        //Draw fluid
        Fluid fluid = ((TileMachineWithFluid) te).getFluidType();
        TextureAtlasSprite fluidTexture = mc.getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        //mc.renderEngine.bindTexture(fluid.getStill());
        int fluidHeight = ((TileMachineWithFluid) te).getFluidGuiHeight(fluidBar.height);
        //drawTexturedModalRect(fluidBar.x + guiLeft, fluidBar.y + guiTop + (fluidBar.height - fluidHeight), 0, 0, fluidBar.width, fluidHeight);
        drawTexturedModalRect(fluidBar.x + guiLeft, fluidBar.y + guiTop + (fluidBar.height - fluidHeight), fluidTexture, fluidBar.width, fluidHeight);

        //Draw lines over fluid
        mc.renderEngine.bindTexture(guiImage);
        drawTexturedModalRect(fluidBar.x + guiLeft, fluidBar.y + guiTop, 186, 0, fluidBar.width, fluidBar.height);
    }

    @Override
    protected void drawText()
    {
        super.drawText();
        fontRendererObj.drawString("Progress:", 42, 30, textColour);
        fontRendererObj.drawString(((TileLiquidEnergiser) te).getProgressPercentString(), 42, 40, textColour);
        //fontRendererObj.drawString(((TileLiquidEnergiser) te).getFluidAmount() + "mb", 42, 50, textColour);
    }

    @Override
    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY)
    {
        super.drawTooltips(tooltip, mouseX, mouseY);
        if(fluidBar.contains(mouseX, mouseY))
        {
            tooltip.add(I18n.format(((TileLiquidEnergiser) te).getFluidType().getUnlocalizedName()));
            tooltip.add(CommonUtils.addDigitGrouping(((TileLiquidEnergiser) te).getFluidAmount()) + "mb");
        }
    }
}
