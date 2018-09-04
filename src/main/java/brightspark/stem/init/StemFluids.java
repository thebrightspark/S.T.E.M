package brightspark.stem.init;

import brightspark.stem.STEM;
import brightspark.stem.util.CommonUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class StemFluids
{
    public static Fluid fluidStem;
    public static Block blockStem;
    private static ItemStack stemBucket;

    /**
     * Gets an ItemStack for a filled bucket of STEM.
     */
    public static ItemStack getStemBucket()
    {
        if(stemBucket == null)
            stemBucket = CommonUtils.createFilledBucket(StemFluids.fluidStem);
        return stemBucket.copy();
    }

    private static ResourceLocation createLoc(String name)
    {
        return new ResourceLocation(STEM.MOD_ID, "blocks/" + name);
    }

    private static Fluid addFluid(String fluidName, int density, int viscosity)
    {
        Fluid fluid = new Fluid(fluidName, createLoc(fluidName + "_still"), createLoc(fluidName + "_flowing"))
                .setDensity(density)
                .setViscosity(viscosity);
        FluidRegistry.addBucketForFluid(fluid);
        return fluid;
    }

    private static Block addBlock(Fluid fluid, MapColor colour)
    {
        return new BlockFluidClassic(fluid, new MaterialLiquid(colour))
                .setRegistryName(fluid.getName())
                .setTranslationKey(fluid.getName())
                .setCreativeTab(STEM.STEM_TAB);
    }

    public static void regItems(IForgeRegistry<Item> registry)
    {
        init();
        registry.register(new ItemBlock(blockStem).setRegistryName(blockStem.getRegistryName()));
    }

    public static void regBlocks(IForgeRegistry<Block> registry)
    {
        init();
        registry.register(blockStem);
    }

    public static void init()
    {
        if(fluidStem != null) return;

        fluidStem = addFluid("stem", 4000, 4000);
        blockStem = addBlock(fluidStem, MapColor.PINK);
    }
}
