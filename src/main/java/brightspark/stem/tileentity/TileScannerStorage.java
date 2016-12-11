package brightspark.stem.tileentity;

import brightspark.stem.recipe.StemRecipe;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class TileScannerStorage extends TileEntity
{
    //TODO: Do I store actual recipes or just the ItemStacks and get it's recipe when needed?
    private List<StemRecipe> storedRecipes = new ArrayList<StemRecipe>();

    public TileScannerStorage() {}
}
