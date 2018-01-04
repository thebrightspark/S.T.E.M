package brightspark.stem.message;

import brightspark.stem.tileentity.TileScannerStorage;
import brightspark.stem.util.CommonUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class MessageUpdateTileRecipes implements IMessage
{
    public int x, y, z;
    public List<ItemStack> recipeStacks;

    public MessageUpdateTileRecipes() {}

    public MessageUpdateTileRecipes(BlockPos pos, List<ItemStack> recipeStacks)
    {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.recipeStacks = recipeStacks;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        int size = buf.readInt();
        recipeStacks = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
            recipeStacks.add(CommonUtils.readStackFromBuf(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(recipeStacks.size());
        recipeStacks.forEach(stack -> CommonUtils.writeStackToBuf(buf, stack));
    }

    public BlockPos getPos()
    {
        return new BlockPos(x, y, z);
    }

    public static class Handler implements IMessageHandler<MessageUpdateTileRecipes, IMessage>
    {
        @Override
        public IMessage onMessage(final MessageUpdateTileRecipes message, MessageContext ctx)
        {
            final IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    World world = Minecraft.getMinecraft().world;
                    TileEntity te = world.getTileEntity(message.getPos());
                    if(te instanceof TileScannerStorage)
                        ((TileScannerStorage) te).setRecipes(message.recipeStacks);
                }
            });

            return null;
        }
    }
}
