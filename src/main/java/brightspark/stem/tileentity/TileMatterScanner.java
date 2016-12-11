package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.block.BlockScannerStorage;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.recipe.RecipeManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @SideOnly(Side.CLIENT)
    public String getScanStatus()
    {
        if(scanProgress == 0)
        {
            if(slots[1] != null && !RecipeManager.hasRecipeForStack(slots[1]))
                return I18n.format("gui.scan.recipe");
            else if(slots[1] != null && !hasStorageDestination())
                return I18n.format("gui.scan.storage");
            else if(!ItemMemoryChip.isMemoryEmpty(slots[2]))
                return I18n.format("gui.scan.complete");
            else
                return I18n.format("gui.scan.inactive");
        }
        else if(scanProgress >= 100)
            return I18n.format("gui.scan.complete");
        else if(!hasStorageDestination())
            return I18n.format("gui.scan.storage");
        else
            return I18n.format("gui.scan.active");
    }

    public int getScanStatusColour()
    {
        if(slots[2] != null && !ItemMemoryChip.isMemoryEmpty(slots[2]))
            return 0x28AA00; //Green
        else if(scanProgress <= 0)
            return 0xD20000; //Red
        else
            return 0xFF8200; //Gold
    }

    public boolean isScanning()
    {
        return scanProgress > 0 && scanProgress < 100;
    }

    @Override
    public void update()
    {
        super.update();

        //Scan item
        if(isScanning() && hasStorageDestination() && energy.getEnergyStored() >= Config.matterScannerEnergyPerTick)
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
        return slots[2] != null && slots[2].getItem() instanceof ItemMemoryChip && ItemMemoryChip.isMemoryEmpty(slots[2]);
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

    @Override
    public int getField(int id)
    {
        return id == 1 ? scanProgress : super.getField(id);
    }

    @Override
    public void setField(int id, int value)
    {
        if(id == 1)
            scanProgress = value;
        else
            super.setField(id, value);
    }

    @Override
    public int getFieldCount()
    {
        return 2;
    }
}
