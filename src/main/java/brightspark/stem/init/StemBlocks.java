package brightspark.stem.init;

import brightspark.stem.block.*;
import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.tileentity.TileMatterCreator;
import brightspark.stem.tileentity.TileMatterScanner;
import brightspark.stem.tileentity.TileScannerStorage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class StemBlocks
{
    public static List<Block> BLOCKS;
    public static List<ItemBlock> ITEM_BLOCKS;

    public static BlockBasic machineBlock;
    public static BlockLiquidEnergiser liquidEnergiser;
    public static BlockMatterScanner matterScanner;
    public static BlockScannerStorage scannerStorage;
    public static BlockMatterCreator matterCreator;

    public static void addBlock(Block block)
    {
        BLOCKS.add(block);
        ITEM_BLOCKS.add((ItemBlock) new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    public static void registerTE(Class<? extends TileEntity> te, Block block)
    {
        GameRegistry.registerTileEntity(te, block.getRegistryName());
    }

    public static void init()
    {
        BLOCKS = new ArrayList<>();
        ITEM_BLOCKS = new ArrayList<>();

        addBlock(machineBlock = new BlockBasic("machine_block", Material.IRON));

        addBlock(liquidEnergiser = new BlockLiquidEnergiser());
        addBlock(matterScanner = new BlockMatterScanner());
        addBlock(scannerStorage = new BlockScannerStorage());
        addBlock(matterCreator = new BlockMatterCreator());
    }

    public static void regTileEntities()
    {
        registerTE(TileLiquidEnergiser.class, liquidEnergiser);
        registerTE(TileMatterScanner.class, matterScanner);
        registerTE(TileScannerStorage.class, scannerStorage);
        registerTE(TileMatterCreator.class, matterCreator);
    }

    @SideOnly(Side.CLIENT)
    public static void regColours()
    {
        //Was gonna try use this to make blocks constantly change colour as a test
        /*
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor()
        {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
            {
                return tintIndex == 0 ? (int) Math.round(Math.random() * 0xFFFFFF) : -1;
            }
        });
        */
    }

    public static Block[] getBlocks()
    {
        if(BLOCKS == null) init();
        return BLOCKS.toArray(new Block[BLOCKS.size()]);
    }

    public static ItemBlock[] getItemBlocks()
    {
        if(ITEM_BLOCKS == null) init();
        return ITEM_BLOCKS.toArray(new ItemBlock[ITEM_BLOCKS.size()]);
    }

    public static void regOres()
    {
        OreDictionary.registerOre("stem_machine_block", machineBlock);
    }
}
