package com.fantasticsource.faerunutils;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(InteractPacketHandler.class, InteractPacket.class, discriminator++, Side.CLIENT);
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
}
