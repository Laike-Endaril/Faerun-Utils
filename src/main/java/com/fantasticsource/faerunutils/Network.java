package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.bettercrafting.recipe.BetterRecipe;
import com.fantasticsource.faerunutils.bettercrafting.recipe.Recipes;
import com.fantasticsource.faerunutils.bettercrafting.table.ContainerBetterCraftingTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(FaerunUtils.MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(ChangeRecipePacketHandler.class, ChangeRecipePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(SetRecipePacketHandler.class, SetRecipePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RecipeOutputCountPacketHandler.class, RecipeOutputCountPacket.class, discriminator++, Side.CLIENT);
    }


    public static class RecipeOutputCountPacket implements IMessage
    {
        int count;

        public RecipeOutputCountPacket()
        {
            //Required
        }

        public RecipeOutputCountPacket(int count)
        {
            this.count = count;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(count);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            count = buf.readInt();
        }
    }

    public static class RecipeOutputCountPacketHandler implements IMessageHandler<RecipeOutputCountPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(RecipeOutputCountPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (player == null) return;

                    Container container = player.openContainer;
                    if (!(container instanceof ContainerBetterCraftingTable)) return;

                    ((ContainerBetterCraftingTable) container).invOutput.getStackInSlot(0).setCount(packet.count);
                });
            }

            return null;
        }
    }


    public static class ChangeRecipePacket implements IMessage
    {
        int offset;

        public ChangeRecipePacket()
        {
            //Required
        }

        public ChangeRecipePacket(int offset)
        {
            this.offset = offset;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(offset);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            offset = buf.readInt();
        }
    }

    public static class ChangeRecipePacketHandler implements IMessageHandler<ChangeRecipePacket, IMessage>
    {
        @Override
        public IMessage onMessage(ChangeRecipePacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.SERVER)
            {
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
                {
                    EntityPlayerMP playerMP = ctx.getServerHandler().player;
                    if (playerMP == null) return;

                    Container container = playerMP.openContainer;
                    if (!(container instanceof ContainerBetterCraftingTable)) return;

                    ((ContainerBetterCraftingTable) container).switchRecipe(packet.offset);
                });
            }

            return null;
        }
    }


    public static class SetRecipePacket implements IMessage
    {
        BetterRecipe recipe;

        public SetRecipePacket()
        {
            //Required
        }

        public SetRecipePacket(BetterRecipe recipe)
        {
            this.recipe = recipe;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            if (recipe == null) recipe = Recipes.NULL;
            ByteBufUtils.writeUTF8String(buf, recipe.translationKey());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            recipe = Recipes.get(ByteBufUtils.readUTF8String(buf));
        }
    }

    public static class SetRecipePacketHandler implements IMessageHandler<SetRecipePacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(SetRecipePacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (player == null) return;

                    Container container = player.openContainer;
                    if (!(container instanceof ContainerBetterCraftingTable)) return;

                    ((ContainerBetterCraftingTable) container).setClientRecipe(packet.recipe);
                });
            }

            return null;
        }
    }
}
