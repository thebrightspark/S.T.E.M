package brightspark.stem.message;

import brightspark.stem.Config;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncConfigs implements IMessage
{
    public MessageSyncConfigs() {}

    @Override
    public void fromBytes(ByteBuf buf)
    {
        Config.matterCreatorEnergyPerMb = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(Config.matterCreatorEnergyPerMb);
    }

    public static class Handler implements IMessageHandler<MessageSyncConfigs, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSyncConfigs message, MessageContext ctx)
        {
            return null;
        }
    }
}
