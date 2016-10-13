package brightspark.stem.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class StemEnergyStorage extends EnergyStorage
{
    public StemEnergyStorage(int capacity)
    {
        super(capacity);
    }

    public StemEnergyStorage(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer);
    }

    public StemEnergyStorage(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract);
    }

    //These methods mostly come from the RF API's EnergyStorage class

    public EnergyStorage readFromNBT(NBTTagCompound nbt)
    {
        energy = nbt.getInteger("Energy");
        if (energy > capacity)
            energy = capacity;
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (energy < 0)
            energy = 0;
        nbt.setInteger("Energy", energy);
        return nbt;
    }

    public EnergyStorage setCapacity(int capacity)
    {
        this.capacity = capacity;
        if (energy > capacity)
            energy = capacity;
        return this;
    }

    public EnergyStorage setMaxTransfer(int maxTransfer)
    {
        setMaxReceive(maxTransfer);
        setMaxExtract(maxTransfer);
        return this;
    }

    public EnergyStorage setMaxReceive(int maxReceive)
    {
        this.maxReceive = maxReceive;
        return this;
    }

    public EnergyStorage setMaxExtract(int maxExtract)
    {
        this.maxExtract = maxExtract;
        return this;
    }

    public int getMaxReceive()
    {
        return maxReceive;
    }

    public int getMaxExtract()
    {
        return maxExtract;
    }

    /**
     * This function is included to allow for server to client sync.
     */
    public void setEnergyStored(int energy)
    {
        this.energy = energy;

        if (this.energy > capacity)
            this.energy = capacity;
        else if (this.energy < 0)
            this.energy = 0;
    }

    /**
     * This function is included to allow the containing tile to directly and efficiently modify the energy contained in the EnergyStorage.
     */
    public void modifyEnergyStored(int energy)
    {
        this.energy += energy;

        if (this.energy > capacity)
            this.energy = capacity;
        else if (this.energy < 0)
            this.energy = 0;
    }
}
