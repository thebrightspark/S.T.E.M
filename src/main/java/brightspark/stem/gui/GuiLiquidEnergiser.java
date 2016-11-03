package brightspark.stem.gui;

import brightspark.stem.init.StemFluids;
import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.tileentity.TileMachine;
import brightspark.stem.util.CommonUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

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
        //TODO: Need to find out how to do this properly
        //https://github.com/SleepyTrousers/EnderIO/blob/1.10/src/main/java/crazypants/enderio/machine/tank/GuiTank.java#L103
        //https://github.com/SleepyTrousers/EnderCore/blob/master/src/main/java/com/enderio/core/client/render/RenderUtil.java#L565
        TextureAtlasSprite fluid = mc.getTextureMapBlocks().getTextureExtry(StemFluids.fluidStem.getStill().toString());
        drawTexturedModalRect(fluidBar.x, fluidBar.y, fluid, fluidBar.width, fluidBar.height);
        //Draw lines over fluid
        //drawTexturedModalRect(fluidBar.x, fluidBar.y, 186, 0, fluidBar.width, fluidBar.height);
    }

    @Override
    protected void drawText()
    {
        super.drawText();
        fontRendererObj.drawString("Progress:", 42, 30, textColour);
        fontRendererObj.drawString(((TileLiquidEnergiser) te).getProgressPercentString(), 42, 40, textColour);
        fontRendererObj.drawString(((TileLiquidEnergiser) te).getFluidAmount() + "mb", 42, 50, textColour);
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
