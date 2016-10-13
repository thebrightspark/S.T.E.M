package brightspark.stem.init;

import brightspark.stem.block.BlockLiquidEnergiser;
import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class StemBlocks
{
    public static List<Block> BLOCKS = new ArrayList<Block>();

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
        registerBlock(liquidEnergiser = new BlockLiquidEnergiser());
    }

    public static void regModels()
    {
        for(Block block : BLOCKS)
            ClientUtils.regModel(block);
    }

    public static void regTileEntities()
    {
        registerTE(TileLiquidEnergiser.class, liquidEnergiser);
    }
}
