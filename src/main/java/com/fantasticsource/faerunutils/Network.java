package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.professions.interactions.InteractionCreatePalette;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.mctools.gui.screen.ItemstackSelectionGUI;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
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
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    protected static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(OpenBagPacketHandler.class, OpenBagPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestPaletteTargetPacketHandler.class, RequestPaletteTargetPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ApplyPalettePacketHandler.class, ApplyPalettePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(ControlPacketHandler.class, ControlPacket.class, discriminator++, Side.SERVER);
    }


    public static class OpenBagPacket implements IMessage
    {
        String itemType = null;
        int size;
        ItemStack bag;

        public OpenBagPacket() //Required; probably for when the packet is received
        {
        }

        public OpenBagPacket(String itemType, int size, ItemStack bag)
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

    public static class OpenBagPacketHandler implements IMessageHandler<OpenBagPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(OpenBagPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() -> ClientProxy.showBagGUI(packet.itemType, packet.size, packet.bag));
            }

            return null;
        }
    }


    public static class RequestPaletteTargetPacket implements IMessage
    {
        boolean mainhand;

        public RequestPaletteTargetPacket() //Required; probably for when the packet is received
        {
        }

        public RequestPaletteTargetPacket(boolean mainhand)
        {
            this.mainhand = mainhand;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(mainhand);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            mainhand = buf.readBoolean();
        }
    }

    public static class RequestPaletteTargetPacketHandler implements IMessageHandler<RequestPaletteTargetPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(RequestPaletteTargetPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() ->
                {
                    ArrayList<ItemStack> options = GlobalInventory.getAllNonSkinItems(mc.player);
                    options.removeIf(stack -> !InteractionCreatePalette.canApplyPaletteTo(stack));
                    if (options.size() == 0) mc.player.sendMessage(new TextComponentString("No items to apply the palette to!"));
                    else
                    {
                        GUIScreen fakeScreen = new GUIScreen()
                        {
                            @Override
                            public String title()
                            {
                                return null;
                            }
                        };
                        GUIItemStack element = new GUIItemStack(fakeScreen, 16, 16, ItemStack.EMPTY);
                        new ItemstackSelectionGUI(element, "Select item to apply palette to...", options.toArray(new ItemStack[0])).addOnClosedActions(() ->
                        {
                            ItemStack stack = element.getItemStack();
                            if (!stack.isEmpty()) WRAPPER.sendToServer(new ApplyPalettePacket(packet.mainhand, stack));
                        });
                    }
                });
            }

            return null;
        }
    }


    public static class ApplyPalettePacket implements IMessage
    {
        boolean mainhand;
        ItemStack stack;

        public ApplyPalettePacket()
        {
            //Required
        }

        public ApplyPalettePacket(boolean mainhand, ItemStack stack)
        {
            this.mainhand = mainhand;
            this.stack = stack;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(mainhand);
            new CItemStack().set(stack).write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            mainhand = buf.readBoolean();
            stack = new CItemStack().read(buf).value;
        }
    }

    public static class ApplyPalettePacketHandler implements IMessageHandler<ApplyPalettePacket, IMessage>
    {
        @Override
        public IMessage onMessage(ApplyPalettePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                ItemStack query = packet.stack, palette = packet.mainhand ? player.getHeldItemMainhand() : player.inventory.offHandInventory.get(0);
                if (!TextFormatting.getTextWithoutFormattingCodes(palette.getDisplayName()).equals("Palette") || !InteractionCreatePalette.canApplyPaletteTo(query)) return;

                ItemStack target = null;
                for (ItemStack stack : GlobalInventory.getAllNonSkinItems(player))
                {
                    if (ItemMatcher.stacksMatch(stack, query))
                    {
                        target = stack;
                        break;
                    }
                }
                if (target == null) return;


                //Apply dyes and dye overrides to target item
                LinkedHashMap<Integer, Color> dyeOverrides = MiscTags.getDyeOverrides(palette);
                String nbtString = target.getTagCompound().toString();
                for (Map.Entry<Integer, Color> entry : dyeOverrides.entrySet())
                {
                    int index = entry.getKey();
                    nbtString = nbtString.replaceAll("dye" + index + "r:[^b]*", "dye" + index + "r:" + ((byte) entry.getValue().r()));
                    nbtString = nbtString.replaceAll("dye" + index + "g:[^b]*", "dye" + index + "g:" + ((byte) entry.getValue().g()));
                    nbtString = nbtString.replaceAll("dye" + index + "b:[^b]*", "dye" + index + "b:" + ((byte) entry.getValue().b()));
                    nbtString = nbtString.replaceAll("dye" + index + "t:[^b]*", "dye" + index + "t:" + ((byte) entry.getValue().a()));
                }
                try
                {
                    target.setTagCompound(JsonToNBT.getTagFromJson(nbtString));
                }
                catch (NBTException e)
                {
                    e.printStackTrace();
                    return;
                }
                if (AssemblyTags.hasInternalCore(target))
                {
                    ItemStack core = AssemblyTags.getInternalCore(target);
                    MiscTags.setDyeOverrides(core, dyeOverrides);
                    AssemblyTags.setInternalCore(target, core);
                }
                MiscTags.setDyeOverrides(target, dyeOverrides);


                //Mark target item so it can't have palettes extracted from it
                InteractionCreatePalette.blockPaletteCreation(target);
            });
            return null;
        }
    }


    public static class ControlPacket implements IMessage
    {
        String control;

        public ControlPacket()
        {
            //Required
        }

        public ControlPacket(String control)
        {
            this.control = control;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, control);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            control = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class ControlPacketHandler implements IMessageHandler<ControlPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ControlPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                switch (packet.control)
                {
                    case "mainhand":
                        FaerunUtils.tryUseItemAction(player, true, 0);
                        break;

                    case "mainhand2":
                        FaerunUtils.tryUseItemAction(player, true, 1);
                        break;

                    case "offhand":
                        FaerunUtils.tryUseItemAction(player, false, 0);
                        break;

                    case "offhand2":
                        FaerunUtils.tryUseItemAction(player, false, 1);
                        break;

                    case "kick":
                        FaerunUtils.tryUseAction(player, CAction.ALL_ACTIONS.get("faerunaction.unarmed.kick"), ItemStack.EMPTY);
                        break;

                    case "comboCancel":
                        FaerunUtils.cancelCombo(player);
                        break;

                    default:
                        System.err.println(TextFormatting.RED + "Unknown control received: " + packet.control);
                        System.err.println(TextFormatting.RED + "From player: " + player.getName() + " (" + player.getUniqueID() + ")");
                }
            });
            return null;
        }
    }
}
