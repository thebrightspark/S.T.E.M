package brightspark.stem.message;

import brightspark.stem.tileentity.TileScannerStorage;
import brightspark.stem.util.CommonUtils;
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

public class MessageUpdateTileRecipe implements IMessage
{
    public int x, y, z;
    public int index;
    public ItemStack recipeStack;

    public MessageUpdateTileRecipe() {}

    public MessageUpdateTileRecipe(BlockPos pos, int index, ItemStack recipeStack)
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
        recipeStack = CommonUtils.readStackFromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(index);
        CommonUtils.writeStackToBuf(buf, recipeStack);
    }

    public BlockPos getPos()
    {
        return new BlockPos(x, y, z);
    }

    public static class Handler implements IMessageHandler<MessageUpdateTileRecipe, IMessage>
    {
        @Override
        public IMessage onMessage(final MessageUpdateTileRecipe message, MessageContext ctx)
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
