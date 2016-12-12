package brightspark.stem.init;

import brightspark.stem.block.*;
import brightspark.stem.tileentity.TileLiquidEnergiser;
import brightspark.stem.tileentity.TileMatterScanner;
import brightspark.stem.tileentity.TileScannerStorage;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StemBlocks
{
    public static List<Block> BLOCKS = new ArrayList<Block>();

    public static BlockBasic machineBlock;
    public static BlockLiquidEnergiser liquidEnergiser;
    public static BlockMatterScanner matterScanner;
    public static BlockScannerStorage scannerStorage;
    public static BlockMatterCreator matterCreator;

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

    public static void regOreDic(Block block)
    {
        OreDictionary.registerOre(block.getRegistryName().getResourcePath(), block);
    }



    public static void regBlocks()
    {
        registerBlock(machineBlock = new BlockBasic("machineBlock", Material.IRON));

        registerBlock(liquidEnergiser = new BlockLiquidEnergiser());
        registerBlock(matterScanner = new BlockMatterScanner());
        registerBlock(scannerStorage = new BlockScannerStorage());

        regOreDic(machineBlock);
    }

    public static void regTileEntities()
    {
        registerTE(TileLiquidEnergiser.class, liquidEnergiser);
        registerTE(TileMatterScanner.class, matterScanner);
        registerTE(TileScannerStorage.class, scannerStorage);
    }

    @SideOnly(Side.CLIENT)
    public static void regModels()
    {
        for(Block block : BLOCKS)
            ClientUtils.regModel(block);
    }

    @SideOnly(Side.CLIENT)
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
}
