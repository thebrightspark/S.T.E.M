package brightspark.stem.message;

import brightspark.stem.recipe.ClientRecipeCache;
import brightspark.stem.util.CommonUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRecipeReply implements IMessage
{
    public ItemStack recipeStack;
    public long recipeFluid;

    public MessageRecipeReply() {}

    public MessageRecipeReply(ItemStack stack, long fluidAmount)
    {
        recipeStack = stack.copy();
        recipeFluid = fluidAmount;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        recipeStack = CommonUtils.readStackFromBuf(buf);
        recipeFluid = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        CommonUtils.writeStackToBuf(buf, recipeStack);
        buf.writeLong(recipeFluid);
    }

    public static class Handler implements IMessageHandler<MessageRecipeReply, IMessage>
    {
        @Override
        public IMessage onMessage(MessageRecipeReply message, MessageContext ctx)
        {
            ClientRecipeCache.receiveRecipe(message.recipeStack, message.recipeFluid);
            return null;
        }
    }
}
