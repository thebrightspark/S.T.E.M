package brightspark.stem.init;

import brightspark.stem.STEM;
import brightspark.stem.util.CommonUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class StemFluids
{
    public static Fluid fluidStem;
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

    private static Fluid regFluid(String fluidName, int density, int viscosity, MapColor colour)
    {
        Fluid fluid = new Fluid(fluidName, createLoc(fluidName + "_still"), createLoc(fluidName + "_flowing"))
                .setDensity(density)
                .setViscosity(viscosity);
        FluidRegistry.addBucketForFluid(fluid);
        Block block = new BlockFluidClassic(fluid, new MaterialLiquid(colour))
                .setRegistryName(fluid.getName())
                .setUnlocalizedName(fluid.getName())
                .setCreativeTab(STEM.STEM_TAB);
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        return fluid;
    }

    public static void init()
    {
        fluidStem = regFluid("stem", 4000, 4000, MapColor.PINK);
    }
}
