package brightspark.stem.util;

import brightspark.stem.ISubTypes;
import brightspark.stem.STEM;
import brightspark.stem.gui.GuiMachineBase;
import brightspark.stem.tileentity.TileMachineWithFluid;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

import java.awt.*;

public class ClientUtils
{
    private static Minecraft mc = Minecraft.getMinecraft();
    private static int chatMessageId = 1;

    public static void regModel(Item item)
    {
        if(item instanceof ISubTypes && item.getHasSubtypes())
            for(int meta = 0; meta < ((ISubTypes) item).getSubNames().length; meta++)
                ModelLoader.setCustomModelResourceLocation(item, meta,
                        new ModelResourceLocation(item.getRegistryName().toString() + "/" + ((ISubTypes) item).getSubNames()[meta], "inventory"));
        else
            regModel(item, 0);
    }

    public static void regModel(Block block)
    {
        Item item = Item.getItemFromBlock(block);
        assert item != null : "Block has no Item!";
        if(block instanceof ISubTypes)
            for(int meta = 0; meta < ((ISubTypes) block).getSubNames().length; meta++)
                ModelLoader.setCustomModelResourceLocation(item, meta,
                        new ModelResourceLocation(item.getRegistryName().toString() + "/" + ((ISubTypes) block).getSubNames()[meta], "inventory"));
        else
            regModel(item);
    }

    public static void regModel(Item item, int meta)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    //Register a liquid model
    public static void regModel(Fluid fluid)
    {
        IFluidBlock fluidBlock = (IFluidBlock) fluid.getBlock();
        Item item = Item.getItemFromBlock((Block) fluidBlock);
        if(item == null)
        {
            LogHelper.fatal("Fluid " + ((Block) fluidBlock).getRegistryName() + " gave a null Item!");
            return;
        }
        ModelBakery.registerItemVariants(item);
        final ModelResourceLocation modelLoc = new ModelResourceLocation(STEM.MOD_ID + ":liquid", fluidBlock.getFluid().getName());
        ModelLoader.setCustomMeshDefinition(item, stack -> modelLoc);
        ModelLoader.setCustomStateMapper((Block) fluidBlock, new StateMapperBase()
        {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return modelLoc;
            }
        });
    }

    public static int getNewChatMessageId()
    {
        return chatMessageId++;
    }

    public static void addClientChatMessage(ITextComponent message, int deleteId)
    {
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(message, deleteId);
    }

    public static void drawGuiFluidBar(GuiMachineBase gui, Rectangle fluidBar, int guiLeft, int guiTop)
    {
        //Draw fluid
        Fluid fluid = ((TileMachineWithFluid) gui.te).getFluidType();
        TextureAtlasSprite fluidTexture = mc.getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        //mc.renderEngine.bindTexture(fluid.getStill());
        int fluidHeight = ((TileMachineWithFluid) gui.te).getFluidGuiHeight(fluidBar.height);
        //drawTexturedModalRect(fluidBar.x + guiLeft, fluidBar.y + guiTop + (fluidBar.height - fluidHeight), 0, 0, fluidBar.width, fluidHeight);
        gui.drawTexturedModalRect(fluidBar.x + guiLeft, fluidBar.y + guiTop + (fluidBar.height - fluidHeight), fluidTexture, fluidBar.width, fluidHeight);

        //Draw lines over fluid
        mc.renderEngine.bindTexture(gui.guiImage);
        gui.drawTexturedModalRect(fluidBar.x + guiLeft, fluidBar.y + guiTop, 210, 0, fluidBar.width, fluidBar.height);
    }

    public static void drawGuiFluidTooltips(java.util.List<String> tooltip, GuiMachineBase gui, Rectangle fluidBar, int mouseX, int mouseY)
    {
        if(fluidBar.contains(mouseX, mouseY))
        {
            TileMachineWithFluid machine = (TileMachineWithFluid) gui.te;
            tooltip.add(I18n.format(machine.getFluidType().getUnlocalizedName()));
            tooltip.add(CommonUtils.addDigitGrouping(machine.getFluidAmount()) + "mb");
        }
    }

    public static void drawGuiProgressArrow(GuiMachineBase gui, Rectangle arrow, int guiLeft, int guiTop)
    {
        int arrowWidth = Math.round(((float) gui.te.getProgress() / 100f) * arrow.width);
        gui.drawTexturedModalRect(arrow.x + guiLeft, arrow.y + guiTop, 186, 0, arrowWidth, arrow.height);
    }
}
