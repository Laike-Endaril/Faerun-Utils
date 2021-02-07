package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.professions.Professions;
import com.fantasticsource.faerunutils.professions.crafting.CraftingGUI;
import com.fantasticsource.faerunutils.professions.interactions.InteractionQuitProfession;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.screen.YesNoGUI;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_GenericDouble;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_GenericString;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_PassiveAttributeMod;
import com.fantasticsource.tools.Tools;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    protected static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(OpenBagPacketHandler.class, OpenBagPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(OpenCraftingPacketHandler.class, OpenCraftingPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestCraftOptionsPacketHandler.class, RequestCraftOptionsPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(CraftOptionsPacketHandler.class, CraftOptionsPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestConfirmQuitPacketHandler.class, RequestConfirmQuitPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ConfirmQuitPacketHandler.class, ConfirmQuitPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(CraftPacketHandler.class, CraftPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(CraftResultPacketHandler.class, CraftResultPacket.class, discriminator++, Side.CLIENT);
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


    public static class OpenCraftingPacket implements IMessage
    {
        ItemStack professionItem;

        public OpenCraftingPacket() //Required; probably for when the packet is received
        {
        }

        public OpenCraftingPacket(ItemStack professionItem)
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

    public static class OpenCraftingPacketHandler implements IMessageHandler<OpenCraftingPacket, IMessage>
    {
        @Override
        public IMessage onMessage(OpenCraftingPacket packet, MessageContext ctx)
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


    public static class RequestConfirmQuitPacket implements IMessage
    {
        public String profession, type;

        public RequestConfirmQuitPacket() //Required; probably for when the packet is received
        {
        }

        public RequestConfirmQuitPacket(String profession, String type)
        {
            this.profession = profession;
            this.type = type;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, profession);
            ByteBufUtils.writeUTF8String(buf, type);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            profession = ByteBufUtils.readUTF8String(buf);
            type = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class RequestConfirmQuitPacketHandler implements IMessageHandler<RequestConfirmQuitPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestConfirmQuitPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() ->
                {
                    YesNoGUI gui = new YesNoGUI("Quit " + packet.profession + "?", "This will remmove the " + packet.profession + " profession and any crafting recipes for it (including levels gained)!  Are you sure you want to quit this profession?");
                    gui.addOnClosedActions(() ->
                    {
                        if (gui.pressedYes) WRAPPER.sendToServer(new ConfirmQuitPacket(packet.profession, packet.type));
                    });
                });
            }

            return null;
        }
    }


    public static class ConfirmQuitPacket implements IMessage
    {
        public String profession, type;

        public ConfirmQuitPacket()
        {
            //Required
        }

        public ConfirmQuitPacket(String profession, String type)
        {
            this.profession = profession;
            this.type = type;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, profession);
            ByteBufUtils.writeUTF8String(buf, type);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            profession = ByteBufUtils.readUTF8String(buf);
            type = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class ConfirmQuitPacketHandler implements IMessageHandler<ConfirmQuitPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ConfirmQuitPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> InteractionQuitProfession.forget(ctx.getServerHandler().player, packet.profession, packet.type));
            return null;
        }
    }


    public static class CraftPacket implements IMessage
    {
        public String profession, targetTrait;
        public ItemStack recipe, mats[];

        public CraftPacket()
        {
            //Required
        }

        public CraftPacket(String profession, ItemStack recipe, String targetTrait, ItemStack... mats)
        {
            this.profession = profession;
            this.targetTrait = targetTrait;
            this.recipe = recipe;
            this.mats = mats;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, profession);
            ByteBufUtils.writeUTF8String(buf, targetTrait);
            CItemStack cItemStack = new CItemStack(recipe).write(buf);
            buf.writeInt(mats.length);
            for (ItemStack stack : mats) cItemStack.set(stack).write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            profession = ByteBufUtils.readUTF8String(buf);
            targetTrait = ByteBufUtils.readUTF8String(buf);
            CItemStack cItemStack = new CItemStack();
            recipe = cItemStack.read(buf).value;
            mats = new ItemStack[buf.readInt()];
            for (int i = 0; i < mats.length; i++) mats[i] = cItemStack.read(buf).value;
        }
    }

    public static class CraftPacketHandler implements IMessageHandler<CraftPacket, IMessage>
    {
        @Override
        public IMessage onMessage(CraftPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                //Check recipe <-> profession
                ItemStack recipe = packet.recipe;
                String profession = packet.profession;
                if (recipe.isEmpty() || !recipe.hasTagCompound() || !recipe.getTagCompound().getCompoundTag("tiamatitems").getCompoundTag("generic").getString("profession").equals(profession)) return;

                //Check recipe product
                CItemType productType = CSettings.LOCAL_SETTINGS.itemTypes.get(recipe.getTagCompound().getCompoundTag("tiamatitems").getCompoundTag("generic").getString("product"));
                if (productType == null) return;

                //Check materials (and queue mat rarities)
                ItemStack[] mats = packet.mats;
                ArrayList<String> matReqs = new ArrayList<>();
                ArrayList<CRarity> matRarities = new ArrayList<>();
                int i = 1;
                String key = "mat" + i;
                String value = recipe.getTagCompound().getCompoundTag("tiamatitems").getCompoundTag("generic").getString(key);
                while (!value.equals(""))
                {
                    matReqs.add(value);

                    key = "mat" + ++i;
                    value = recipe.getTagCompound().getCompoundTag("tiamatitems").getCompoundTag("generic").getString(key);
                }
                if (mats.length != matReqs.size()) return;
                for (ItemStack mat : mats)
                {
                    if (mat.isEmpty() || !mat.hasTagCompound()) return;
                    if (!matReqs.remove(MiscTags.getItemTypeName(mat))) return;
                    CRarity rarity = MiscTags.getItemRarity(mat);
                    if (rarity == null) return;
                    matRarities.add(rarity);
                }

                //Check for NPC
                int index = Tools.indexOf(Professions.CRAFTING_PROFESSIONS, profession);
                if (index == -1) return;

                EntityPlayerMP player = ctx.getServerHandler().player;
                String npcName = Professions.CRAFTING_PROFESSION_NPCS[index];
                boolean found = false;
                for (Entity entity : player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(4)))
                {
                    if (entity.getName().equals(npcName))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found) return;

                //Make sure inventory exists
                ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
                if (inventory == null) return;

                //Make sure inventory contains profession
                found = false;
                for (ItemStack stack : inventory.getCraftingProfessions())
                {
                    if (profession.equals(MiscTags.getItemTypeName(stack)))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found) return;

                //Make sure inventory contains recipe
                found = false;
                for (ItemStack stack : inventory.getCraftingRecipes())
                {
                    if (ItemMatcher.stacksMatch(stack, recipe))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found) return;

                //Make sure inventory contains mats (and queue actual mat stacks from inventory)
                ArrayList<ItemStack> matsToFind = new ArrayList<>(Arrays.asList(mats)), foundMats = new ArrayList<>();
                if (matsToFind.size() > 0)
                {
                    for (ItemStack stack : GlobalInventory.getAllNonSkinItems(player))
                    {
                        for (ItemStack mat : matsToFind.toArray(new ItemStack[0]))
                        {
                            if (ItemMatcher.stacksMatch(mat, stack))
                            {
                                foundMats.add(stack);
                                matsToFind.remove(mat);
                                break;
                            }
                        }
                    }
                }
                if (matsToFind.size() > 0) return;


                //Compute product rarity and check it
                i = 0;
                for (CRarity rarity : matRarities)
                {
                    i += rarity.ordering;
                }
                i /= matRarities.size();
                CRarity productRarity = null;
                for (CRarity rarity : CSettings.LOCAL_SETTINGS.rarities.values())
                {
                    if (rarity.ordering == i)
                    {
                        productRarity = rarity;
                        break;
                    }
                }
                if (productRarity == null) return;


                //Destroy materials
                for (ItemStack stack : foundMats) MCTools.destroyItemStack(stack);


                //Create item
                ItemStack product = productType.generateItem(0, productRarity);


                //TODO change one trait to target trait if successful


                //Add item to inventory and send notice of crafted item
                MCTools.give(player, product);
                WRAPPER.sendTo(new CraftResultPacket(product), player);
            });
            return null;
        }
    }


    public static class CraftResultPacket implements IMessage
    {
        public ItemStack result;

        public CraftResultPacket() //Required; probably for when the packet is received
        {
        }

        public CraftResultPacket(ItemStack result)
        {
            this.result = result;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            new CItemStack(result).write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            result = new CItemStack().read(buf).value;
        }
    }

    public static class CraftResultPacketHandler implements IMessageHandler<CraftResultPacket, IMessage>
    {
        @Override
        public IMessage onMessage(CraftResultPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() ->
                {
                    if (mc.currentScreen instanceof CraftingGUI) ((CraftingGUI) mc.currentScreen).setPreviousResult(packet.result);
                });
            }

            return null;
        }
    }
}
