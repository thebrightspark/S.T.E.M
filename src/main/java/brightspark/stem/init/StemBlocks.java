package brightspark.stem.init;

import brightspark.stem.block.BlockBasic;
import brightspark.stem.block.BlockLiquidEnergiser;
import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StemBlocks
{
    public static List<Block> BLOCKS = new ArrayList<Block>();

    public static BlockBasic blockBasic;
    public static BlockLiquidEnergiser liquidEnergiser;

    public static void registerBlock(Block block)
    {
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        BLOCKS.add(block);
    }

    public static void registerTE(Class<? extends TileEntity> te, Block block)
    {
        GameRegistry.registerTileEntity(te, block.getRegistryName().getResourcePath());
    }



    public static void regBlocks()
    {
        registerBlock(blockBasic = new BlockBasic("machineBlock", Material.IRON));
        registerBlock(liquidEnergiser = new BlockLiquidEnergiser());

        OreDictionary.registerOre("machineBlock", blockBasic);
    }

    public static void regModels()
    {
        for(Block block : BLOCKS)
            ClientUtils.regModel(block);
    }

    public static void regColours()
    {
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor()
        {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return tintIndex == 0 ? (int) Math.round(Math.random() * 0xFFFFFF) : -1;
            }
        });
    }

    public static void regTileEntities()
    {
        registerTE(TileLiquidEnergiser.class, liquidEnergiser);
    }
}
