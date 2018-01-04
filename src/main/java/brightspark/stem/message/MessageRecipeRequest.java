package brightspark.stem.message;

import brightspark.stem.recipe.ServerRecipeManager;
import brightspark.stem.util.CommonUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRecipeRequest implements IMessage
{
    public ItemStack recipeStack;

    public MessageRecipeRequest() {}

    public MessageRecipeRequest(ItemStack stack)
    {
        recipeStack = stack.copy();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        recipeStack = CommonUtils.readStackFromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        CommonUtils.writeStackToBuf(buf, recipeStack);
    }

    public static class Handler implements IMessageHandler<MessageRecipeRequest, IMessage>
    {
        @Override
        public IMessage onMessage(MessageRecipeRequest message, MessageContext ctx)
        {
            //Sends recipe data back to the client
            long fluidAmount = ServerRecipeManager.getStemNeeded(message.recipeStack);
            return new MessageRecipeReply(message.recipeStack, fluidAmount);
        }
    }
}
