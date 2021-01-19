package com.fantasticsource.faerunutils;

import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.mctools.component.CItemStack;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    protected static int discriminator = 0;
    protected static final HashMap<EntityPlayerMP, EntityPlayerMP> TRADE_REQUESTS = new HashMap<>();

    public static void init()
    {
        WRAPPER.registerMessage(InteractPacketHandler.class, InteractPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(BagPacketHandler.class, BagPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestTradePacketHandler.class, RequestTradePacket.class, discriminator++, Side.SERVER);
    }


    public static class InteractPacket implements IMessage
    {
        BlockPos pos;

        public InteractPacket() //Required; probably for when the packet is received
        {
        }

        public InteractPacket(BlockPos pos)
        {
            this.pos = pos;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        }
    }

    public static class InteractPacketHandler implements IMessageHandler<InteractPacket, IMessage>
    {
        @Override
        public IMessage onMessage(InteractPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() ->
                {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, packet.pos, EnumFacing.UP, new Vec3d(packet.pos), EnumHand.MAIN_HAND);
                });
            }

            return null;
        }
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


    public static class RequestTradePacket implements IMessage
    {
        String playerName;

        public RequestTradePacket()
        {
            //Required
        }

        public RequestTradePacket(String playerName)
        {
            this.playerName = playerName;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, playerName);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            playerName = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class RequestTradePacketHandler implements IMessageHandler<RequestTradePacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestTradePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player, other = (EntityPlayerMP) PlayerData.getEntity(packet.playerName);

                if (player.dimension != 0) player.sendMessage(new TextComponentString(TextFormatting.RED + "You can only trade in town"));
                else if (other == null) player.sendMessage(new TextComponentString(TextFormatting.RED + packet.playerName + " seems to have vanished"));
                else if (other.world != player.world || other.getDistanceSq(player) > 16) player.sendMessage(new TextComponentString(TextFormatting.RED + packet.playerName + " is too far away to trade"));
                else if (TRADE_REQUESTS.get(other) == player)
                {
                    TRADE_REQUESTS.remove(player);
                    TRADE_REQUESTS.remove(other);

                    //TODO initiate trade
                }
                else
                {
                    TRADE_REQUESTS.put(player, other);
                    other.sendMessage(new TextComponentString(TextFormatting.AQUA + player.getName() + " requests a trade"));
                }
            });
            return null;
        }
    }
}
