package brightspark.stem.message;

import brightspark.stem.tileentity.TileMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This message will send an integer from the server to the client.
 * This is needed since using the progress bar method in the container truncates the values to a short.
 */
public class MessageUpdateTile implements IMessage
{
    public int x, y, z, id, value;

    public MessageUpdateTile() {}

    public MessageUpdateTile(BlockPos pos, int fieldId, int value)
    {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        id = fieldId;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        id = buf.readInt();
        value = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(id);
        buf.writeInt(value);
    }

    public BlockPos getPos()
    {
        return new BlockPos(x, y, z);
    }

    public static class Handler implements IMessageHandler<MessageUpdateTile, IMessage>
    {
        @Override
        public IMessage onMessage(final MessageUpdateTile message, MessageContext ctx)
        {
            final IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    World world = Minecraft.getMinecraft().theWorld;
                    TileEntity te = world.getTileEntity(message.getPos());
                    if(te instanceof TileMachine)
                        ((TileMachine) te).setField(message.id, message.value);
                }
            });
            return null;
        }
    }
}
