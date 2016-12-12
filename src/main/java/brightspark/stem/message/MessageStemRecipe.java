package brightspark.stem.message;

import brightspark.stem.tileentity.TileScannerStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageStemRecipe implements IMessage
{
    public int x, y, z;
    public int index;
    public ItemStack recipeStack;

    public MessageStemRecipe() {}

    public MessageStemRecipe(BlockPos pos, int index, ItemStack recipeStack)
    {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.index = index;
        this.recipeStack = recipeStack;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        index = buf.readInt();
        Item item = Item.getItemById(buf.readInt());
        int itemMeta = buf.readInt();
        recipeStack = new ItemStack(item, 1, itemMeta);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(index);
        buf.writeInt(Item.getIdFromItem(recipeStack.getItem()));
        buf.writeInt(recipeStack.getMetadata());
    }

    public BlockPos getPos()
    {
        return new BlockPos(x, y, z);
    }

    public static class Handler implements IMessageHandler<MessageStemRecipe, IMessage>
    {
        @Override
        public IMessage onMessage(final MessageStemRecipe message, MessageContext ctx)
        {
            final IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    World world = Minecraft.getMinecraft().theWorld;
                    TileEntity te = world.getTileEntity(message.getPos());
                    if(te instanceof TileScannerStorage)
                        ((TileScannerStorage) te).setRecipeAtIndex(message.index, message.recipeStack);
                }
            });

            return null;
        }
    }
}
