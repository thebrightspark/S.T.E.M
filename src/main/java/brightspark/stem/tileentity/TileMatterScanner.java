package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.recipe.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;

public class TileMatterScanner extends TileMachine
{
    private int scanProgress = 0;
    private static final String KEY_PROGRESS = "scanProgress";

    //Slot 0 -> Input Stack
    //Slot 1 -> Memory Chip Stack

    public TileMatterScanner()
    {
        super(2);
    }

    @Override
    public void update()
    {
        super.update();

        //TODO: Check for Scanner Storage adjacent and save to it if one exists

        //Scan item
        if(scanProgress > 0 || (slots[0] != null && slots[1] != null && slots[1].getItem() instanceof ItemMemoryChip && ItemMemoryChip.isMemoryEmpty(slots[1]) && RecipeManager.hasRecipeForStack(slots[0]) && energy.getEnergyStored() >= Config.matterScannerEnergyPerTick))
        {
            if(!worldObj.isRemote)
            {
                scanProgress++;
                energy.modifyEnergyStored(-Config.matterScannerEnergyPerTick);
                if(scanProgress >= 100) //TEMP?
                {
                    scanProgress = 0;
                    ItemMemoryChip.setItemInMemory(slots[1], slots[0]);
                    slots[0] = null;
                }
            }
            markDirty();
        }
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
