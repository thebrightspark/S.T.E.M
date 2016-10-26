package brightspark.stem.util;

import brightspark.stem.ISubTypes;
import brightspark.stem.STEM;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.IFluidBlock;

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
    public static void regFluidModel(IFluidBlock fluidBlock)
    {
        Item item = Item.getItemFromBlock((Block) fluidBlock);
        if(item == null)
        {
            LogHelper.fatal("Fluid " + ((Block) fluidBlock).getRegistryName() + " gave a null Item!");
            return;
        }
        ModelBakery.registerItemVariants(item);
        final ModelResourceLocation modelLoc = new ModelResourceLocation(STEM.MOD_ID + ":liquid", fluidBlock.getFluid().getName());
        ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return modelLoc;
            }
        });
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
}
