package brightspark.stem.tileentity;

import brightspark.stem.Config;
import brightspark.stem.energy.StemEnergyStorage;
import brightspark.stem.util.NBTHelper;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid = "redstoneflux", iface = "cofh.redstoneflux.api.IEnergyReceiver", striprefs = true)
public class TileMachine extends StemTileEntity implements IEnergyReceiver, ITickable
{
    protected StemEnergyStorage energy;
    //Dependant on redstone input. Redstone signal = machine off //TODO: Make a button to change redstone interactivity
    public boolean active = true;
    //Progress isn't actually changed in this class, but is available for tiles which extend this class.
    protected int progress = 0;

    private static final String KEY_STACK_ENERGY = "stackEnergy";
    private static final String KEY_PROGRESS = "progress";

    public TileMachine(StemEnergyStorage energy, int numSlots)
    {
        super(numSlots);
        this.energy = energy == null ? new StemEnergyStorage(Config.machineEnergyCapacity, Config.machineEnergyMaxTransfer) : energy;
    }

    /**
     * If the machine is able to do work - does not take into account energy stored
     */
    public boolean canWork()
    {
        return active;
    }

    /**
     * If the machine has enough energy to work
     */
    public boolean hasEnoughEnergy()
    {
        return energy.getEnergyStored() >= getEnergyPerTick();
    }

    public int getProgress()
    {
        return progress;
    }

    public String getProgressString()
    {
        return getProgress() + "%";
    }

    public boolean isWorking()
    {
        return progress > 0 && progress < 100;
    }

    public int getEnergyPerTick()
    {
        return 1000000;
    }

    /**
     * Called in update() every time there's enough energy to do some work.
     * May be called multiple times per tick if there's a lot of energy.
     */
    public void doWork()
    {
        energy.modifyEnergyStored(-getEnergyPerTick());
    }

    @Override
    public void copyDataFrom(StemTileEntity machine)
    {
        super.copyDataFrom(machine);
        if(machine == null || !(machine instanceof TileMachine) || ((TileMachine) machine).energy == null)
            return;
        TileMachine tileMachine = (TileMachine) machine;
        //Copy energy
        energy.setCapacity(tileMachine.energy.getMaxEnergyStored());
        energy.setMaxExtract(tileMachine.energy.getMaxExtract());
        energy.setMaxReceive(tileMachine.energy.getMaxReceive());
        energy.setEnergyStored(tileMachine.energy.getEnergyStored());
    }

    /* NBT */

    /**
     * Writes the tile's data to the ItemStack.
     */
    @Override
    public void writeDataToStack(ItemStack stack)
    {
        super.writeDataToStack(stack);

        //Write energy
        NBTHelper.setInteger(stack, KEY_STACK_ENERGY, energy.getEnergyStored());
    }

    /**
     * Reads and set the energy saved to the ItemStack to the energy for this TileMachine.
     */
    @Override
    public void readDataFromStack(ItemStack stack)
    {
        super.readDataFromStack(stack);

        //Read energy
        energy.setEnergyStored(NBTHelper.getInt(stack, KEY_STACK_ENERGY));
    }

    /**
     * Reads and returns the energy saved to the ItemStack.
     */
    public static int readEnergyFromStack(ItemStack stack)
    {
        return NBTHelper.getInt(stack, KEY_STACK_ENERGY);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        //Read energy
        energy.readFromNBT(nbt);

        //Read progress
        progress = nbt.getInteger(KEY_PROGRESS);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        //Write energy
        energy.writeToNBT(nbt);

        //Write progress
        nbt.setInteger(KEY_PROGRESS, progress);

        return super.writeToNBT(nbt);
    }

    /* Overrides */

    @Override
    public void update()
    {
        energy.setCanTransfer(canWork());
        if(canWork() && hasEnoughEnergy())
        {
            if(!world.isRemote)
                while(canWork() && hasEnoughEnergy())
                    doWork();
            markDirty();
        }
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
    {
        return energy.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from)
    {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from)
    {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from)
    {
        return true; //sideConfigs.get(from) != EnumSidePerm.NONE;
    }

    @Override
    public int getField(int id)
    {
        return id == 0 ? energy.getEnergyStored() : id == 1 ? progress : 0;
    }

    @Override
    public void setField(int id, int value)
    {
        switch(id)
        {
            case 0:
                energy.setEnergyStored(value);
                break;
            case 1:
                progress = value;
                break;
        }
    }

    @Override
    public int getFieldCount()
    {
        return 2;
    }
}
