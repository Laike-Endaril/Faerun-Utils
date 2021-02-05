package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.professions.crafting.CraftingGUI;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_GenericDouble;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_GenericString;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_PassiveAttributeMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    protected static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(BagPacketHandler.class, BagPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(CraftPacketHandler.class, CraftPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestCraftOptionsPacketHandler.class, RequestCraftOptionsPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(CraftOptionsPacketHandler.class, CraftOptionsPacket.class, discriminator++, Side.CLIENT);
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


    public static class CraftPacket implements IMessage
    {
        ItemStack professionItem;

        public CraftPacket() //Required; probably for when the packet is received
        {
        }

        public CraftPacket(ItemStack professionItem)
        {
            this.professionItem = professionItem;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            new CItemStack(professionItem).write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            professionItem = new CItemStack().read(buf).value;
        }
    }

    public static class CraftPacketHandler implements IMessageHandler<CraftPacket, IMessage>
    {
        @Override
        public IMessage onMessage(CraftPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() -> ClientProxy.showCraftGUI(packet.professionItem));
            }

            return null;
        }
    }


    public static class RequestCraftOptionsPacket implements IMessage
    {
        public ItemStack recipe;

        public RequestCraftOptionsPacket()
        {
            //Required
        }

        public RequestCraftOptionsPacket(ItemStack recipe)
        {
            this.recipe = recipe;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            new CItemStack(recipe).write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            recipe = new CItemStack().read(buf).value;
        }
    }

    public static class RequestCraftOptionsPacketHandler implements IMessageHandler<RequestCraftOptionsPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestCraftOptionsPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
                if (inventory == null) return;

                boolean valid = false;
                for (ItemStack recipe : inventory.getCraftingRecipes())
                {
                    if (ItemMatcher.stacksMatch(recipe, packet.recipe))
                    {
                        valid = true;
                        break;
                    }
                }

                if (!valid)
                {
                    System.err.println(TextFormatting.RED + "Possible hack: recipe not found in inventory - " + packet.recipe.getDisplayName() + " for player " + player.getName());
                    return;
                }


                WRAPPER.sendTo(new CraftOptionsPacket(packet.recipe), player);
            });
            return null;
        }
    }


    public static class CraftOptionsPacket implements IMessage
    {
        ArrayList<String> options = new ArrayList<>();

        public CraftOptionsPacket() //Required; probably for when the packet is received
        {
        }

        public CraftOptionsPacket(ItemStack recipe)
        {
            if (recipe.isEmpty()) return;


            String s = !recipe.hasTagCompound() ? "" : recipe.getTagCompound().getCompoundTag("tiamatitems").getCompoundTag("generic").getString("product");
            if (s.equals("")) return;


            CRarity rarity = CSettings.LOCAL_SETTINGS.rarities.get("Crude");
            CItemType productType = CSettings.LOCAL_SETTINGS.itemTypes.get(s);
            if (productType == null) return;


            for (String poolSet : rarity.traitPoolSetRollCounts.keySet())
            {
                if (productType.randomRecalculableTraitPoolSets.containsKey(poolSet))
                {
                    for (String poolName : productType.randomRecalculableTraitPoolSets.get(poolSet))
                    {
                        CRecalculableTraitPool pool = CSettings.LOCAL_SETTINGS.recalcTraitPools.get(poolName);
                        if (pool == null)
                        {
                            System.err.println(TextFormatting.RED + "Trait pool option for recipe not found: " + poolName);
                            continue;
                        }

                        for (CRecalculableTrait trait : pool.traitGenWeights.keySet())
                        {
                            if (trait.elements.size() == 1)
                            {
                                CRecalculableTraitElement element = trait.elements.iterator().next();
                                if (element instanceof CRTraitElement_PassiveAttributeMod) options.add("attribute.name." + ((CRTraitElement_PassiveAttributeMod) element).attributeName);
                                else if (element instanceof CRTraitElement_GenericString) options.add(((CRTraitElement_GenericString) element).name);
                                else if (element instanceof CRTraitElement_GenericDouble) options.add(((CRTraitElement_GenericDouble) element).name);
                                else options.add(trait.name);
                            }
                            else options.add(trait.name);
                        }
                    }
                }
            }
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(options.size());
            for (String option : options) ByteBufUtils.writeUTF8String(buf, option);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            for (int i = buf.readInt(); i > 0; i--) options.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    public static class CraftOptionsPacketHandler implements IMessageHandler<CraftOptionsPacket, IMessage>
    {
        @Override
        public IMessage onMessage(CraftOptionsPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() ->
                {
                    if (mc.currentScreen instanceof CraftingGUI) ((CraftingGUI) mc.currentScreen).updateOptions(packet.options);
                });
            }

            return null;
        }
    }
}
