package brightspark.stem.recipe;

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
    @Override
    public String getCommandName()
    {
        return "stem";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        String text = "\nAdd Specific Item: stem add <itemId> [itemMeta] <fluidAmount>" +
                "\n Remove Specific Item: stem remove <itemId> [itemMeta]" +
                "\n Save Recipes To File: stem save";
        if(sender instanceof EntityPlayer)
            text += "\nAdd Held Item: stem add <fluidAmount>" +
                    "\nRemove Held Item: stem remove";
        return text;
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(sender.getEntityWorld().isRemote)
            return;
        if(args.length == 0)
            throw new WrongUsageException(getCommandUsage(sender));

        boolean isPlayer = sender instanceof EntityPlayer;

        if(args[0].equals("remove") || args[0].equals("r"))
        {
            if(args.length == 1)
            {
                //Remove held item
                if(isPlayer)
                {
                    if(((EntityPlayer) sender).getHeldItemMainhand() == null)
                        throw new CommandException("No item held");
                    else
                    {
                        ItemStack heldItem = ((EntityPlayer) sender).getHeldItemMainhand().copy();
                        heldItem.stackSize = 1;
                        if(RecipeManager.removeRecipe(heldItem))
                            sender.addChatMessage(new TextComponentString("Removed recipe for " + heldItem.getDisplayName()));
                        else
                            sender.addChatMessage(new TextComponentString("No recipe found for " + heldItem.getDisplayName()));
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

                ItemStack stack = new ItemStack(item, 1, meta);
                if(RecipeManager.removeRecipe(stack))
                    sender.addChatMessage(new TextComponentString("Removed recipe for " + stack.getDisplayName()));
                else
                    sender.addChatMessage(new TextComponentString("No recipe found for " + stack.getDisplayName()));
            }
        }
        else if(args[0].equals("add") || args[0].equals("a"))
        {
            if(args.length == 2)
            {
                //Add held item
                if(isPlayer)
                {
                    if(((EntityPlayer) sender).getHeldItemMainhand() == null)
                        throw new CommandException("No item held");
                    else
                    {
                        ItemStack heldItem = ((EntityPlayer) sender).getHeldItemMainhand().copy();
                        heldItem.stackSize = 1;
                        int fluidAmount;
                        try
                        {
                            fluidAmount = parseInt(args[1], 1);
                        }
                        catch(NumberInvalidException e)
                        {
                            throw new CommandException("Fluid amount must be a number greater than 0");
                        }

                        if(RecipeManager.addRecipe(new StemRecipe(heldItem, fluidAmount)))
                            sender.addChatMessage(new TextComponentString("Added recipe for " + heldItem.getDisplayName() + " with " + fluidAmount + "mb"));
                        else
                            sender.addChatMessage(new TextComponentString("Recipe for " + heldItem.getDisplayName() + " already exists!"));
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

                ItemStack stack = new ItemStack(item, 1, meta);
                if(RecipeManager.addRecipe(new StemRecipe(stack, fluidAmount)))
                    sender.addChatMessage(new TextComponentString("Added recipe for " + stack.getDisplayName() + " with " + fluidAmount + "mb"));
                else
                    sender.addChatMessage(new TextComponentString("Recipe for " + stack.getDisplayName() + " already exists!"));
            }
            else
                //Incorrect command
                throw new WrongUsageException(getCommandUsage(sender));
        }
        else if(args[0].equals("save") || args[0].equals("s"))
        {
            //Save recipes
            RecipeManager.saveRecipes();
            sender.addChatMessage(new TextComponentString("Recipes saved"));
        }
        else
            //Incorrect command
            throw new WrongUsageException(getCommandUsage(sender));
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        switch(args.length)
        {
            case 1:
                return getListOfStringsMatchingLastWord(args, "add", "a", "remove", "r", "save", "s");
            case 2:
                return getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
            default:
                return Collections.emptyList();
        }
    }
}
