package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.block.BlockScannerStorage;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.recipe.RecipeManager;
import brightspark.stem.util.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileMatterScanner extends TileMachine
{
    private int scanProgress = 0;
    private static final String KEY_PROGRESS = "scanProgress";

    //Slot 0 -> Energy Input Stack
    //Slot 1 -> Input Stack
    //Slot 2 -> Memory Chip Stack

    public TileMatterScanner()
    {
        super(new StemEnergyStorage(Config.machineEnergyCapacity, 2000), 3);
    }

    public int getScanProgress()
    {
        return scanProgress;
    }

    public String getScanProgressString()
    {
        return getScanProgress() + "%";
    }

    public String getScanStatus()
    {
        if(scanProgress == 0)
        {
            if(slots[1] != null && !RecipeManager.hasRecipeForStack(slots[1]))
                return "No Recipe";
            else
                return "Inactive";
        }
        else if(scanProgress >= 100)
            return "Scan Complete";
        else if(hasStorageDestination())
            return "Waiting For Storage";
        else
            return "Scanning...";
    }

    /**
     * Used to set the progress to 0 when the output item is picked up.
     */
    public void resetScanProgress()
    {
        scanProgress = 0;
    }

    @Override
    public void update()
    {
        super.update();

        //Scan item
        if(scanProgress > 0 && scanProgress < 100 && hasStorageDestination() && energy.getEnergyStored() >= Config.matterScannerEnergyPerTick)
        {
            if(!worldObj.isRemote)
            {
                scanProgress++;
                energy.modifyEnergyStored(-Config.matterScannerEnergyPerTick);
                if(scanProgress >= 100) //TEMP?
                {
                    //Finish scan
                    if(slots[2] != null)
                        ItemMemoryChip.setMemory(slots[2], slots[1]);
                    else
                    {
                        TileScannerStorage storage = getAdjacentStorage();
                        //TODO: Add to scanner storage
                    }
                    slots[1] = null;
                    scanProgress = 0;
                }
            }
            markDirty();
        }

        //Check if item can be scanned
        if(!worldObj.isRemote && scanProgress <= 0 && slots[1] != null && hasStorageDestination() && RecipeManager.hasRecipeForStack(slots[1]) && energy.getEnergyStored() >= Config.matterScannerEnergyPerTick)
        {
            LogHelper.info("Starting scan!");
            scanProgress++;
            energy.modifyEnergyStored(-Config.matterScannerEnergyPerTick);
        }
    }

    /**
     * Checks if there's a memory chip in the necessary slot or if there's a memory storage block adjacent.
     */
    private boolean hasStorageDestination()
    {
        return hasMemoryChip() || hasAdjacentStorage();
    }

    private boolean hasMemoryChip()
    {
        return slots[2] != null && slots[2].getItem() instanceof ItemMemoryChip && ItemMemoryChip.isMemoryEmpty(slots[1]);
    }

    private boolean hasAdjacentStorage()
    {
        for(EnumFacing side : EnumFacing.VALUES)
            if(worldObj.getBlockState(pos.offset(side)).getBlock() instanceof BlockScannerStorage)
                return true;
        return false;
    }

    /**
     * Gets the TileEntity for an adjacent storage.
     * Returns null if one couldn't be found.
     */
    private TileScannerStorage getAdjacentStorage()
    {
        for(EnumFacing side : EnumFacing.VALUES)
            if(worldObj.getBlockState(pos.offset(side)).getBlock() instanceof BlockScannerStorage)
                return (TileScannerStorage) worldObj.getTileEntity(pos.offset(side));
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        //Read scan progress
        scanProgress = nbt.getInteger(KEY_PROGRESS);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        //Write scan progress
        nbt.setInteger(KEY_PROGRESS, scanProgress);

        return super.writeToNBT(nbt);
    }
}
