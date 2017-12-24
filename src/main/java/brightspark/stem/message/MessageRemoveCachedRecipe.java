package brightspark.stem.message;

import brightspark.stem.recipe.ClientRecipeCache;
import brightspark.stem.util.CommonUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Arrays;

public class MessageRemoveCachedRecipe implements IMessage
{
    public NonNullList<ItemStack> stacks = NonNullList.create();

    public MessageRemoveCachedRecipe() {}

    public MessageRemoveCachedRecipe(ItemStack stack)
    {
        this.stacks.add(stack);
    }

    public MessageRemoveCachedRecipe(ItemStack[] stacks)
    {
        this.stacks.addAll(Arrays.asList(stacks));
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int size = buf.readInt();
        stacks = NonNullList.create();
        if(size > 0)
            for(int i = 0; i < size; i++)
                stacks.add(CommonUtils.readStackFromBuf(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        int size = stacks.size();
        buf.writeInt(size);
        if(size > 0) stacks.forEach(stack -> CommonUtils.writeStackToBuf(buf, stack));
    }

    public static class Handler implements IMessageHandler<MessageRemoveCachedRecipe, IMessage>
    {
        @Override
        public IMessage onMessage(MessageRemoveCachedRecipe message, MessageContext ctx)
        {
            if(message.stacks.isEmpty())
                ClientRecipeCache.removeRecipe(null);
            else
                message.stacks.forEach(ClientRecipeCache::removeRecipe);
            return null;
        }
    }
}
