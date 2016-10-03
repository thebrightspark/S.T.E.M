package brightspark.stem.init;

import brightspark.stem.STEM;
import brightspark.stem.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class StemFluids
{
    public static Fluid fluidStem;
    public static Block blockStem;

    private static ResourceLocation createLoc(String name)
    {
        return new ResourceLocation(STEM.MOD_ID, "blocks/" + name);
    }

    private static Fluid regFluid(String fluidName, int density, int viscosity)
    {
        Fluid fluid = new Fluid(fluidName, createLoc(fluidName + "_still"), createLoc(fluidName + "_flowing"))
                .setDensity(density)
                .setViscosity(viscosity);
        FluidRegistry.addBucketForFluid(fluid);
        return fluid;
    }

    private static Block regBlock(Fluid fluid, MapColor colour)
    {
        Block block = new BlockFluidClassic(fluid, new MaterialLiquid(colour))
                .setRegistryName(fluid.getName())
                .setUnlocalizedName(fluid.getName())
                .setCreativeTab(STEM.STEM_TAB);
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        return block;
    }

    public static void regFluids()
    {
        fluidStem = regFluid("stem", 4000, 4000);
        blockStem = regBlock(fluidStem, MapColor.PINK);
    }

    public static void regModels()
    {
        ClientUtils.regFluidModel((IFluidBlock) blockStem);
    }
}
