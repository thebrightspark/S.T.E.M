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
        fontRendererObj.drawString(I18n.format("gui.progress"), 8, 30, textColour);
        fontRendererObj.drawString(te.getEnergyPercentString(), 8, 40, textColour);
        fontRendererObj.drawString(I18n.format("gui.averageInput"), 8, 55, textColour);
        fontRendererObj.drawString(((TileLiquidEnergiser) te).getAverageInputString(), 8, 65, textColour);
    }

    @Override
    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY)
    {
        if(fluidBar.contains(mouseX, mouseY))
        {
            TileLiquidEnergiser machine = (TileLiquidEnergiser) te;
            tooltip.add(I18n.format(machine.getFluidType().getUnlocalizedName()));
            tooltip.add(CommonUtils.addDigitGrouping(machine.getFluidAmount()) + "mb");
        }
    }
}
