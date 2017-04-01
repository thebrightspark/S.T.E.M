package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.block.BlockScannerStorage;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.recipe.ServerRecipeManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileMatterScanner extends TileMachine
{
    private EnumScanStatus scanStatus = EnumScanStatus.INACTIVE;

    private static final int colourRed = 0xD20000;
    private static final int colourGold = 0xFF8200;
    private static final int colourGreen = 0x28AA00;

    private enum EnumScanStatus
    {
        INACTIVE("gui.inactive", colourRed),
        NO_RECIPE("gui.recipe", colourRed),
        NO_STORAGE("gui.scan.storage", colourRed),
        ACTIVE("gui.scan.active", colourGold),
        COMPLETE("gui.scan.complete", colourGreen);

        public String unlocText, locText;
        public int colour;

        EnumScanStatus(String unlocText, int colour)
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
    //Slot 1 -> Input Stack
    //Slot 2 -> Memory Chip Stack

    public TileMatterScanner()
    {
        super(new StemEnergyStorage(Config.machineEnergyCapacity, 2000), 3);
    }

    @SideOnly(Side.CLIENT)
    public String getScanStatus()
    {
        return scanStatus.getText();
    }

    public int getScanStatusColour()
    {
        return scanStatus.colour;
    }

    @Override
    public int getEnergyPerTick()
    {
        return Config.matterScannerEnergyPerTick;
    }

    @Override
    public boolean canWork()
    {
        if(isWorking())
            return super.canWork();
        else
            return super.canWork() && slots[0] != null && hasStorageDestination() && ServerRecipeManager.hasRecipeForStack(slots[0]);
    }

    @Override
    public void doWork()
    {
        super.doWork();

        //Scan item
        if(progress < 100)
            progress++;
        if(progress >= 100)
        {
            //Finish scan
            if(slots[1] != null)
                ItemMemoryChip.setMemory(slots[1], slots[0]);
            else
            {
                TileScannerStorage storage = getAdjacentStorage();
                if(storage != null)
                    storage.storeRecipe(slots[0]);
            }
            slots[0] = null;
            progress = 0;
        }
    }

    @Override
    public void update()
    {
        super.update();

        //Update scan status
        if(progress == 0)
        {
            if(slots[0] != null && !ServerRecipeManager.hasRecipeForStack(slots[0]))
                scanStatus = EnumScanStatus.NO_RECIPE;
            else if(slots[0] != null && !hasStorageDestination())
                scanStatus = EnumScanStatus.NO_STORAGE;
            else if(!ItemMemoryChip.isMemoryEmpty(slots[1]))
                scanStatus = EnumScanStatus.COMPLETE;
            else
                scanStatus = EnumScanStatus.INACTIVE;
        }
        else if(slots[1] != null && slots[1].getItem() instanceof ItemMemoryChip && !ItemMemoryChip.isMemoryEmpty(slots[1]))
            scanStatus = EnumScanStatus.COMPLETE;
        else if(!hasStorageDestination())
            scanStatus = EnumScanStatus.NO_STORAGE;
        else
            scanStatus = EnumScanStatus.ACTIVE;
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
        return slots[1] != null && slots[1].getItem() instanceof ItemMemoryChip && ItemMemoryChip.isMemoryEmpty(slots[1]);
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
}
