package com.fantasticsource.faerunutils;

import com.fantasticsource.mctools.component.CItemStack;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    protected static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(BagPacketHandler.class, BagPacket.class, discriminator++, Side.CLIENT);
    }


    public static class BagPacket implements IMessage
    {
        String itemType = null;
        int size;
        ItemStack bag;

        public BagPacket() //Required; probably for when the packet is received
        {
        }

        public BagPacket(String itemType, int size, ItemStack bag)
        {
            this.itemType = itemType;
            this.size = size;
            this.bag = bag;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(itemType != null);
            if (itemType != null) ByteBufUtils.writeUTF8String(buf, itemType);
            buf.writeInt(size);
            new CItemStack().set(bag).write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            if (buf.readBoolean()) itemType = ByteBufUtils.readUTF8String(buf);
            size = buf.readInt();
            bag = new CItemStack().read(buf).value;
        }
    }

    public static class BagPacketHandler implements IMessageHandler<BagPacket, IMessage>
    {
        @Override
        public IMessage onMessage(BagPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() -> ClientProxy.showBagGUI(packet.itemType, packet.size, packet.bag));
            }

            return null;
        }
    }
}
