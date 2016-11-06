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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StemFluids
{
    public static Fluid fluidStem;

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

    public static void regFluids()
    {
        fluidStem = regFluid("stem", 4000, 4000, MapColor.PINK);
    }

    @SideOnly(Side.CLIENT)
    public static void regModels()
    {
        ClientUtils.regFluidModel((IFluidBlock) fluidStem.getBlock());
    }
}
