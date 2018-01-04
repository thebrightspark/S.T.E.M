package brightspark.stem.gui;

import brightspark.stem.item.ItemMemoryChip;
import brightspark.stem.message.MessageUpdateTileRecipes;
import brightspark.stem.recipe.StemRecipe;
import brightspark.stem.tileentity.TileScannerStorage;
import brightspark.stem.util.CommonUtils;
import brightspark.stem.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContainerScannerStorage extends ContainerMachineBase
{
    protected List<ItemStack> cachedRecipes = new ArrayList<ItemStack>();
    public int recipeSelected = 0;

    public ContainerScannerStorage(InventoryPlayer invPlayer, TileScannerStorage machine)
    {
        super(invPlayer, machine);
        if(!machine.getWorld().isRemote)
            machine.removeMissingRecipes();
    }

    private TileScannerStorage getMachine()
    {
        return (TileScannerStorage) inventory;
    }

    public StemRecipe getCurrentRecipe()
    {
        return getMachine().getRecipeAtIndex(recipeSelected);
    }

    /**
     * Called by the input slot when it's contents have changed and are not null
     */
    public void inputSlotChanged(ItemStack newItem)
    {
        if(newItem.isEmpty() || !(newItem.getItem() instanceof ItemMemoryChip))
            return;

        ItemStack chipSavedStack = ItemMemoryChip.getMemory(newItem);

        if(chipSavedStack.isEmpty())
        {
            //Save current recipe to item
            if(getMachine().getStoredRecipes() == null || getMachine().getStoredRecipes().isEmpty())
                return;
            ItemMemoryChip.setMemory(newItem, getCurrentRecipe().getOutput());
        }
        else
        {
            //Save recipe on item to storage
            getMachine().storeRecipe(chipSavedStack);
            ItemMemoryChip.clearMemory(newItem);
        }

        //Move item to output slot
        inventory.setInventorySlotContents(1, newItem.copy());
        inventory.setInventorySlotContents(0, ItemStack.EMPTY);

        detectAndSendChanges();
    }

    @Nullable
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        ItemStack returnStack = super.slotClick(slotId, dragType, clickTypeIn, player);

        ItemStack inputStack = inventory.getStackInSlot(0);
        if(inputStack != null && inputStack.getItem() instanceof ItemMemoryChip && (getMachine().hasRecipes() || !ItemMemoryChip.isMemoryEmpty(inputStack)))
        {
            if(!player.world.isRemote)
                inputSlotChanged(inputStack);
            else
                inventory.setInventorySlotContents(0, ItemStack.EMPTY);
        }

        return returnStack;
    }

    @Override
    protected void addSlots()
    {
        //Memory Chip Input
        addSlotToContainer(new SlotMachine(inventory, 17, 23)
        {
            @Override
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return stack != null && stack.getItem() instanceof ItemMemoryChip;
            }
        });

        //Memory Chip Output
        addSlotToContainer(new SlotOutputOnly(inventory, 17, 54));
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        //Sends changes with the recipes
        List<ItemStack> scannerRecipes = getMachine().getStoredRecipes();

        for(IContainerListener listener : listeners)
            if(!cachedRecipes.equals(scannerRecipes))
            {
                cachedRecipes = scannerRecipes;
                CommonUtils.NETWORK.sendTo(new MessageUpdateTileRecipes(getMachine().getPos(), scannerRecipes), (EntityPlayerMP) listener);
            }
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        playerIn.dropItem(inventory.removeStackFromSlot(0), false);
        playerIn.dropItem(inventory.removeStackFromSlot(1), false);
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     * I am using this to handle arrow button presses
     */
    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id)
    {
        switch(id)
        {
            case 0: //Left Arrow
                recipeSelected--;
                if(recipeSelected < 0)
                    recipeSelected = 0;
                return true;
            case 1: //Right Arrow
                recipeSelected++;
                if(recipeSelected >= getMachine().getStoredRecipes().size())
                    recipeSelected--;
                return true;
            default:
                LogHelper.warn("ID " + id + " isn't handled in ContainerScannerStorage!");
                return false;
        }
    }
}
