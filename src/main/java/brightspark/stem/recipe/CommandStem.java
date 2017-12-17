package brightspark.stem.recipe;

import brightspark.stem.message.MessageRecipeMakeDirty;
import brightspark.stem.util.CommonUtils;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandStem extends CommandBase
{
    private void removeRecipe(ICommandSender sender, ItemStack stack)
    {
        if(ServerRecipeManager.removeRecipe(stack))
        {
            CommonUtils.NETWORK.sendToAll(new MessageRecipeMakeDirty(stack));
            sender.sendMessage(new TextComponentString("Removed recipe for " + stack.getDisplayName()));
        }
        else
            sender.sendMessage(new TextComponentString("No recipe found for " + stack.getDisplayName()));
    }

    private void addRecipe(ICommandSender sender, ItemStack stack, int fluidAmount)
    {
        if(ServerRecipeManager.addRecipe(new StemRecipe(stack, fluidAmount)))
        {
            CommonUtils.NETWORK.sendToAll(new MessageRecipeMakeDirty(stack));
            sender.sendMessage(new TextComponentString("Added recipe for " + stack.getDisplayName() + " with " + fluidAmount + "mb"));
        }
        else
            sender.sendMessage(new TextComponentString("Recipe for " + stack.getDisplayName() + " already exists!"));
    }



    @Override
    public String getName()
    {
        return "stem";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        String text = "\nAdd Specific Item: stem add <itemId> [itemMeta] <fluidAmount>" +
                "\n Remove Specific Item: stem remove <itemId> [itemMeta]" +
                "\n Save Recipes To File: stem save" +
                "\n Reset Recipe File To Default : stem reset";
        if(sender instanceof EntityPlayer)
            text += "\nAdd Held Item: stem add <fluidAmount>" +
                    "\nRemove Held Item: stem remove";
        return text;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(sender.getEntityWorld().isRemote)
            return;
        if(args.length == 0)
            throw new WrongUsageException(getUsage(sender));

        boolean isPlayer = sender instanceof EntityPlayer;

        if(args[0].equals("remove") || args[0].equals("r"))
        {
            if(args.length == 1)
            {
                //Remove held item
                if(isPlayer)
                {
                    if(((EntityPlayer) sender).getHeldItemMainhand().isEmpty())
                        throw new CommandException("No item held");
                    else
                    {
                        //Remove recipe
                        ItemStack heldItem = ((EntityPlayer) sender).getHeldItemMainhand().copy();
                        heldItem.setCount(1);
                        removeRecipe(sender, heldItem);
                    }
                }
                else
                    throw new CommandException("Must be a player holding an item");
            }
            else
            {
                //Remove specific item
                int meta = 0;
                if(args.length >= 3)
                {
                    //Get meta data
                    try
                    {
                        meta = parseInt(args[2], 0);
                    }
                    catch(NumberInvalidException e)
                    {
                        throw new CommandException("Meta data must be a number");
                    }
                }

                Item item;
                if((item = Item.getByNameOrId(args[1])) == null)
                    throw new CommandException("Item couldn't be found");

                //Remove recipe
                ItemStack stack = new ItemStack(item, 1, meta);
                removeRecipe(sender, stack);
            }
        }
        else if(args[0].equals("add") || args[0].equals("a"))
        {
            if(args.length == 2)
            {
                //Add held item
                if(isPlayer)
                {
                    if(((EntityPlayer) sender).getHeldItemMainhand().isEmpty())
                        throw new CommandException("No item held");
                    else
                    {
                        ItemStack heldItem = ((EntityPlayer) sender).getHeldItemMainhand().copy();
                        heldItem.setCount(1);
                        int fluidAmount;
                        try
                        {
                            fluidAmount = parseInt(args[1], 1);
                        }
                        catch(NumberInvalidException e)
                        {
                            throw new CommandException("Fluid amount must be a number greater than 0");
                        }

                        //Add recipe
                        addRecipe(sender, heldItem, fluidAmount);
                    }
                }
                else
                    throw new CommandException("Must be a player holding an item");
            }
            else if(args.length >= 3)
            {
                //Add specific item

                Item item;
                if((item = Item.getByNameOrId(args[1])) == null)
                    throw new CommandException("Item couldn't be found");

                int meta = 0;
                int fluidAmount;
                int fluidArgsIndex = 2;
                if(args.length >= 4)
                {
                    //Get meta data
                    fluidArgsIndex = 3;
                    try
                    {
                        meta = parseInt(args[2], 0);
                    }
                    catch(NumberInvalidException e)
                    {
                        throw new CommandException("Meta data must be a number");
                    }
                }

                //Get fluid amount
                try
                {
                    fluidAmount = parseInt(args[fluidArgsIndex], 1);
                }
                catch(NumberInvalidException e)
                {
                    throw new CommandException("Fluid amount must be a number greater than 0");
                }

                //Add recipe
                ItemStack stack = new ItemStack(item, 1, meta);
                addRecipe(sender, stack, fluidAmount);
            }
            else
                //Incorrect command
                throw new WrongUsageException(getUsage(sender));
        }
        else if(args[0].equals("save") || args[0].equals("s"))
        {
            //Save recipes
            ServerRecipeManager.saveRecipes();
            sender.sendMessage(new TextComponentString("Recipes saved"));
        }
        else if(args[0].equals("reset"))
        {
            //Reset recipes to default
            ServerRecipeManager.resetRecipeFile();
            CommonUtils.NETWORK.sendToAll(new MessageRecipeMakeDirty(null));
            sender.sendMessage(new TextComponentString("Recipes reset to default"));
        }
        else
            //Incorrect command
            throw new WrongUsageException(getUsage(sender));
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        switch(args.length)
        {
            case 1:
                return getListOfStringsMatchingLastWord(args, "add", "a", "remove", "r", "save", "s", "reset");
            case 2:
                return getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
            default:
                return Collections.emptyList();
        }
    }
}
