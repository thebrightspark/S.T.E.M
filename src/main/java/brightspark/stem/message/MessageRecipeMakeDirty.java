package brightspark.stem.message;

import brightspark.stem.recipe.ClientRecipeCache;
import brightspark.stem.util.CommonUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRecipeMakeDirty implements IMessage
{
    public ItemStack stack;

    public MessageRecipeMakeDirty() {}

    public MessageRecipeMakeDirty(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        stack = CommonUtils.readStackFromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        CommonUtils.writeStackToBuf(buf, stack);
    }

    public static class Handler implements IMessageHandler<MessageRecipeMakeDirty, IMessage>
    {
        @Override
        public IMessage onMessage(MessageRecipeMakeDirty message, MessageContext ctx)
        {
            ClientRecipeCache.markRecipeDirty(message.stack);
            return null;
        }
    }
}
