package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.block.BlockScannerStorage;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.init.StemFluids;
import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.recipe.ClientRecipeCache;
import brightspark.stem.recipe.StemRecipe;
import brightspark.stem.util.CommonUtils;
import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class TileMatterCreator extends TileMachineWithFluid
{
    private int progress = 0;
    private StemRecipe recipeCache;
    private int timeToNextRecipeCheck = 0;
    private List<ItemStack> storageRecipeCache = new ArrayList<ItemStack>();
    private EnumCreationStatus createStatus = EnumCreationStatus.INACTIVE;

    private static final int colourRed = 0xD20000;
    private static final int colourGold = 0xFF8200;
    private static final int colourGreen = 0x28AA00;

    private enum EnumCreationStatus
    {
        INACTIVE("gui.create.inactive", colourRed),
        NO_RECIPE("gui.create.recipe", colourRed),
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

    //Slot 0 -> Energy Input Stack
    //Slot 1 -> Fluid Input Stack
    //Slot 2 -> Bucket Output Stack
    //Slot 3 -> Memory Chip Slot
    //Slot 4 -> Item Creation Output

    public TileMatterCreator()
    {
        super(new StemEnergyStorage(-1, Config.matterCreatorMaxEnergyInput), new FluidStack(StemFluids.fluidStem, 8000), 5);
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

    public boolean isCreating()
    {
        return recipeCache != null && progress > 0 && progress < recipeCache.getFluidInput();
    }

    /**
     * Gets the progress as an integer between 0 and 100 taking into account the energy required.
     */
    public int getProgress()
    {
        if(recipeCache == null)
            return 0;
        int energyNeeded = recipeCache.getFluidInput() * Config.matterCreatorEnergyPerMb;
        int energyReceived = progress * Config.matterCreatorEnergyPerMb + energy.getEnergyStored();
        return Math.round((energyReceived / energyNeeded) * 100);
    }

    /**
     * Force updates the cached recipe to the given ItemStack
     */
    public void updateCachedRecipe(ItemStack stack)
    {
        int fluidAmount = ClientRecipeCache.getFluidAmount(stack);
        if(fluidAmount > 0)
            recipeCache = new StemRecipe(stack, fluidAmount);
        else
            recipeCache = null;
    }

    private void sortCachedRecipes()
    {
        CommonUtils.sortItemStackList(storageRecipeCache);
        markDirty();
    }

    /**
     * Forces a check for recipes in all adjacent Scanner Storage blocks and saves to a cache.
     */
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

    /**
     * Starts the creating process if possible.
     * Will return true if creating was successfully started.
     *
     * This'll be called from the GUI //TODO or in update() if on repeat mode.
     */
    public boolean startCreating()
    {
        if(!active || energy.getEnergyStored() < Config.matterCreatorEnergyPerMb || tank.getFluidAmount() <= 0 || !canOutput())
            return false;
        progress++;
        energy.modifyEnergyStored(- Config.matterCreatorEnergyPerMb);
        tank.drainInternal(1);
        return true;
    }

    /**
     * Checks if an item about to be created can be put in the output slot.
     */
    private boolean canOutput()
    {
        ItemStack outputStack = slots[4];
        return outputStack == null || (outputStack.equals(recipeCache.getOutput()) && outputStack.stackSize < 64);
    }

    private boolean hasMemoryChip()
    {
        return slots[3] != null && slots[3].getItem() instanceof ItemMemoryChip && !ItemMemoryChip.isMemoryEmpty(slots[3]);
    }

    private boolean hasAdjacentRecipeStorage()
    {
        return !storageRecipeCache.isEmpty();
    }

    private boolean hasEnoughEnergy()
    {
        return energy.getEnergyStored() >= Config.matterCreatorEnergyPerMb;
    }

    private boolean hasEnoughFluid()
    {
        return tank.getFluidAmount() > 0;
    }

    @Override
    public void update()
    {
        super.update();

        if(timeToNextRecipeCheck > 0)
            timeToNextRecipeCheck--;

        //Update cached recipe
        if(timeToNextRecipeCheck <= 0)
        {
            timeToNextRecipeCheck = 20;
            //Check for memory chip first
            if(slots[3] != null)
            {
                if(recipeCache == null && hasMemoryChip())
                    updateCachedRecipe(ItemMemoryChip.getMemory(slots[3]));
            }
            //Then check adjacent scanner storage blocks
            else
            {
                updateCachedRecipeStorage();
                if(recipeCache == null && !storageRecipeCache.isEmpty())
                    updateCachedRecipe(storageRecipeCache.get(0));
            }
        }

        //Matter progress
        if(active && isCreating() && hasEnoughEnergy() && hasEnoughFluid() && recipeCache != null)
        {
            if(!worldObj.isRemote)
            {
                //Increase progress
                while(hasEnoughEnergy() && hasEnoughFluid())
                {
                    progress++;
                    energy.modifyEnergyStored(-Config.matterCreatorEnergyPerMb);
                    tank.drainInternal(1);
                    if(progress >= recipeCache.getFluidInput())
                    {
                        //Craft item
                        ItemStack stackInOutputSlot = slots[4];
                        if(stackInOutputSlot != null)
                        {
                            if(stackInOutputSlot.equals(recipeCache.getOutput()))
                                stackInOutputSlot.stackSize++;
                        }
                        setInventorySlotContents(4, recipeCache.getOutput().copy());
                    }
                }
            }
            markDirty();
        }

        //Handle slots
        ItemStack slotStack;
        //Energy input
        if((slotStack = slots[0]) != null && slotStack.getItem() instanceof IEnergyContainerItem)
            ((IEnergyContainerItem) slotStack.getItem()).extractEnergy(slotStack, getMaxReceieve(null), false);
        //Bucket input
        if((slotStack = slots[1]) != null && slotStack.isItemEqual(CommonUtils.createFilledBucket(StemFluids.fluidStem)) &&
                getFluidSpace() > Fluid.BUCKET_VOLUME && (slots[2] == null || slots[2].getItem().equals(Items.BUCKET)))
        {
            tank.fillInternal(Fluid.BUCKET_VOLUME);
            slotStack.stackSize--;
            if(slotStack.stackSize <= 0)
                setInventorySlotContents(1, null);
            if(slots[2] != null && slots[2].getItem().equals(Items.BUCKET))
                slots[2].stackSize++;
            else
                setInventorySlotContents(2, new ItemStack(Items.BUCKET));
        }

        //Update creation status
        if(progress == 0)
        {
            if(!hasMemoryChip() && !hasAdjacentRecipeStorage())
                createStatus = EnumCreationStatus.NO_RECIPE;
            else if(!canOutput())
                createStatus = EnumCreationStatus.CANT_OUTPUT;
            else if(!hasEnoughEnergy())
                createStatus = EnumCreationStatus.NO_ENERGY;
            else if(!hasEnoughFluid())
                createStatus = EnumCreationStatus.NO_FLUID;
            else
                createStatus = EnumCreationStatus.INACTIVE;
        }
        else if(recipeCache != null && progress >= recipeCache.getFluidInput())
            createStatus = EnumCreationStatus.COMPLETE;
        else
            createStatus = EnumCreationStatus.ACTIVE;
    }
}
