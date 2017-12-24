package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.init.StemFluids;
import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.recipe.StemRecipe;
import brightspark.stem.util.CommonUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileMatterCreator extends TileMachineWithFluid
{
    private StemRecipe recipeCache;
    //private int timeToNextRecipeCheck = 0;
    //private List<ItemStack> storageRecipeCache = new ArrayList<ItemStack>();
    private EnumCreationStatus createStatus = EnumCreationStatus.INACTIVE;

    private static final int colourRed = 0xD20000;
    private static final int colourGold = 0xFF8200;
    private static final int colourGreen = 0x28AA00;

    private enum EnumCreationStatus
    {
        INACTIVE("gui.inactive", colourRed),
        NO_RECIPE("gui.recipe", colourRed),
        CANT_OUTPUT("gui.create.output", colourRed),
        NO_ENERGY("gui.create.energy", colourRed),
        NO_FLUID("gui.create.fluid", colourRed),
        ACTIVE("gui.create.active", colourGold),
        COMPLETE("gui.create.complete", colourGreen);

        public String unlocText, locText;
        public int colour;

        EnumCreationStatus(String unlocText, int colour)
        {
            this.unlocText = unlocText;
            this.colour = colour;
        }

        @SideOnly(Side.CLIENT)
        public String getText()
        {
            if(locText == null)
                locText = I18n.format(unlocText);
            return locText;
        }
    }

    //Slot 0 -> Fluid Input Stack
    //Slot 1 -> Bucket Output Stack
    //Slot 2 -> Memory Chip Slot
    //Slot 3 -> Item Creation Output

    public TileMatterCreator()
    {
        super(new StemEnergyStorage(-1, Config.matterCreatorMaxEnergyInput), new FluidStack(StemFluids.fluidStem, 8000), 4);
    }

    @SideOnly(Side.CLIENT)
    public String getCreationStatus()
    {
        return createStatus.getText();
    }

    public int getCreationStatusColour()
    {
        return createStatus.colour;
    }

    @Override
    public boolean isWorking()
    {
        return recipeCache != null && progress > 0 && progress < recipeCache.getFluidInput();
    }

    /**
     * Gets the progress as an integer between 0 and 100 taking into account the energy required.
     */
    @Override
    public int getProgress()
    {
        if(recipeCache == null)
            return 0;
        return Math.round(((float) progress / (float) recipeCache.getFluidInput()) * 100);
    }

    /**
     * Force updates the cached recipe to the given ItemStack
     */
    public void updateCachedRecipe(ItemStack stack)
    {
        if(stack == null)
            recipeCache = null;
        else
        {
            StemRecipe recipe = CommonUtils.getRecipeForStack(stack);
            if(recipe != null && recipe.getFluidInput() > 0)
                recipeCache = recipe;
            else
                recipeCache = null;
        }
    }

    /*
    private void sortCachedRecipes()
    {
        CommonUtils.sortItemStackList(storageRecipeCache);
        markDirty();
    }
    */

    /**
     * Forces a check for recipes in all adjacent Scanner Storage blocks and saves to a cache.
     */
    /*
    public void updateCachedRecipeStorage()
    {
        storageRecipeCache.clear();
        for(EnumFacing side : EnumFacing.VALUES)
        {
            BlockPos offsetPos = pos.offset(side);
            Block offsetBlock = worldObj.getBlockState(offsetPos).getBlock();
            if(offsetBlock instanceof BlockScannerStorage)
            {
                TileScannerStorage te = ((BlockScannerStorage) offsetBlock).getTileEntity(worldObj, offsetPos);
                List<ItemStack> recipes = te.getStoredRecipes();
                //Add recipes to cache if they don't already exist there
                for(ItemStack stack : recipes)
                    if(CommonUtils.itemStackListContains(storageRecipeCache, stack))
                        storageRecipeCache.add(stack);
            }
        }
        sortCachedRecipes();
    }
    */

    /**
     * Stops creation and sets energy to 0
     */
    public void stopCreation()
    {
        energy.setEnergyStored(0);
        progress = 0;
        recipeCache = null;
    }

    /**
     * Checks if an item about to be created can be put in the output slot.
     */
    private boolean canOutput()
    {
        ItemStack outputStack = slots.get(3);
        return outputStack.isEmpty() || (recipeCache != null && outputStack.isItemEqual(recipeCache.getOutput()) && outputStack.getCount() < 64);
    }

    private boolean hasMemoryChip()
    {
        return slots.get(2).getItem() instanceof ItemMemoryChip && !ItemMemoryChip.isMemoryEmpty(slots.get(2));
    }

    /*
    private boolean hasAdjacentRecipeStorage()
    {
        return !storageRecipeCache.isEmpty();
    }
    */

    @Override
    public int getEnergyPerTick()
    {
        return Config.matterCreatorEnergyPerMb;
    }

    @Override
    public boolean canWork()
    {
        if(recipeCache == null)
            updateCachedRecipe(hasMemoryChip() ? ItemMemoryChip.getMemory(slots.get(2)) : null);
        return super.canWork() && recipeCache != null && tank.getFluidAmount() > 0 && hasMemoryChip() && canOutput() && progress < recipeCache.getFluidInput();
    }

    @Override
    public void doWork()
    {
        super.doWork();

        /*
        if(timeToNextRecipeCheck > 0)
            timeToNextRecipeCheck--;
        */

        //Update cached recipe
        //if(timeToNextRecipeCheck <= 0)
        //{
            //timeToNextRecipeCheck = 20;

            //Used to have the check for a memory chip here. Now been moved to canWork()

            //TODO: Later be able to choose an item from adjacent storages within the GUI
            //Then check adjacent scanner storage blocks
            /*
            else
            {
                updateCachedRecipeStorage();
                if(recipeCache == null && !storageRecipeCache.isEmpty())
                    updateCachedRecipe(storageRecipeCache.get(0));
            }
            */
        //}

        //Matter progress
        //Increase progress
        progress++;
        tank.drain(1, true);
        if(progress >= recipeCache.getFluidInput())
        {
            //Craft item
            ItemStack stackInOutputSlot = slots.get(3);
            if(stackInOutputSlot.isItemEqual(recipeCache.getOutput()))
                stackInOutputSlot.grow(1);
            else
                setInventorySlotContents(3, recipeCache.getOutput().copy());
            progress = 0;
        }
    }

    @Override
    public void update()
    {
        super.update();

        //Handle slots
        //Bucket input
        if(CommonUtils.isStemBucket(slots.get(0)) && getFluidSpace() >= Fluid.BUCKET_VOLUME &&
                (slots.get(1).isEmpty() || slots.get(1).getItem().equals(Items.BUCKET)))
        {
            tank.fillInternal(Fluid.BUCKET_VOLUME, true);
            slots.get(0).shrink(1);
            if(slots.get(0).getCount() <= 0)
                setInventorySlotContents(0, ItemStack.EMPTY);
            if(slots.get(1).getItem().equals(Items.BUCKET))
                slots.get(1).grow(1);
            else
                setInventorySlotContents(1, new ItemStack(Items.BUCKET));
        }

        //Update creation status
        if(!hasMemoryChip()) //&& !hasAdjacentRecipeStorage())
            createStatus = EnumCreationStatus.NO_RECIPE;
        else if(!canOutput())
            createStatus = EnumCreationStatus.CANT_OUTPUT;
        else if(tank.getFluidAmount() <= 0)
            createStatus = EnumCreationStatus.NO_FLUID;
        //else if(!hasEnoughEnergy())
        //    createStatus = EnumCreationStatus.NO_ENERGY;
        else if(progress > 0)
            createStatus = EnumCreationStatus.ACTIVE;
        else
            createStatus = EnumCreationStatus.INACTIVE;
        //else if(recipeCache != null && progress >= recipeCache.getFluidInput())
        //    createStatus = EnumCreationStatus.COMPLETE;
    }
}
