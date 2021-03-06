package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.instances.InstanceData;
import com.fantasticsource.instances.world.WorldInstance;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.mojang.authlib.GameProfile;
import moe.plushie.rpg_framework.api.RpgEconomyAPI;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;
import static com.fantasticsource.tiamatinventory.TiamatInventory.CURRENCY_CAPABILITY;

public class InteractionInsure extends AInteraction
{
    protected static final HashMap<World, HashMap<UUID, ItemStack>> TRACKED_ITEMS = new HashMap<>();

    protected String type;

    public InteractionInsure(String type)
    {
        super("Insure " + type);
        this.type = type;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(ProfessionsAndInteractions.INSURANCE_AGENT)) return null;


        int cost = 0;
        ArrayList<ItemStack> stacks = getNonEmptyItems(player);
        for (ItemStack stack : stacks) cost += insuranceCostRecursive(player.getUniqueID(), stack);
        cost = adjustedCost(cost);


        return stacks.size() > 0 ? name + TextFormatting.RED + " (costs " + ProfessionsAndInteractions.getCostString(cost) + ")" : null;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return null;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        //Reduce funds or reject attempt if too poor
        ArrayList<ItemStack> stacks = getNonEmptyItems(player);
        if (!player.isCreative())
        {
            int totalCost = 0;
            for (ItemStack stack : stacks.toArray(new ItemStack[0])) totalCost += insuranceCostRecursive(player.getUniqueID(), stack);
            totalCost = adjustedCost(totalCost);
            ICurrency[] currencies = RpgEconomyAPI.getCurrencyManager().getCurrencies();
            ICurrency currency = currencies[0];
            IWallet wallet = player.getCapability(CURRENCY_CAPABILITY, null).getWallet(currency);
            int money = wallet.getAmount();
            if (totalCost > money)
            {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have enough money!"));
                return false;
            }
            wallet.setAmount(money - totalCost);
        }


        //NBT alterations
        for (ItemStack stack : stacks) insureRecursive(player.getUniqueID(), stack);


        return true;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
    }


    public ArrayList<ItemStack> getNonEmptyItems(EntityPlayerMP player)
    {
        ITiamatPlayerInventory inv = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        ArrayList<ItemStack> stacks = new ArrayList<>();
        switch (type)
        {
            case "Entire Inventory":
                stacks.addAll(player.inventory.mainInventory);
                stacks.addAll(player.inventory.armorInventory);
                stacks.addAll(player.inventory.offHandInventory);
                if (inv != null)
                {
                    stacks.add(inv.getSheathedMainhand1());
                    stacks.add(inv.getSheathedOffhand1());
                    stacks.add(inv.getSheathedMainhand2());
                    stacks.add(inv.getSheathedOffhand2());
                    stacks.addAll(inv.getTiamatArmor());
                }
                break;

            case "All Equipped Items":
                stacks.add(player.getHeldItemMainhand());
                stacks.addAll(player.inventory.armorInventory);
                stacks.addAll(player.inventory.offHandInventory);
                if (inv != null)
                {
                    stacks.add(inv.getSheathedMainhand1());
                    stacks.add(inv.getSheathedOffhand1());
                    stacks.add(inv.getSheathedMainhand2());
                    stacks.add(inv.getSheathedOffhand2());
                    stacks.addAll(inv.getTiamatArmor());
                }
                break;

            case "Mainhand":
                stacks.add(player.getHeldItemMainhand());
                break;

            case "Offhand":
                stacks.addAll(player.inventory.offHandInventory);
                break;

            case "Helm":
                stacks.add(player.inventory.armorInventory.get(3));
                break;

            case "Shoulder Armor":
                if (inv != null) stacks.add(inv.getShoulders());
                break;

            case "Cape":
                if (inv != null) stacks.add(inv.getCape());
                break;

            case "Chestpiece":
                stacks.add(player.inventory.armorInventory.get(2));
                break;

            case "Leg Armor":
                stacks.add(player.inventory.armorInventory.get(1));
                break;

            case "Boots":
                stacks.add(player.inventory.armorInventory.get(0));
                break;
        }
        stacks.removeIf(ItemStack::isEmpty);
        return stacks;
    }


    public static int insuranceCostRecursive(UUID ownerID, ItemStack stack)
    {
        int result = insuranceCost(ownerID, stack);
        for (IPartSlot partSlot : AssemblyTags.getPartSlots(stack))
        {
            ItemStack part = partSlot.getPart();
            if (!part.isEmpty()) result += insuranceCostRecursive(ownerID, part);
        }
        return result;
    }

    protected static int insuranceCost(UUID ownerID, ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        return compound != null && ownerID.equals(compound.getUniqueId("insuredFor")) ? 0 : getPartValue(stack);
    }

    protected static int getPartValue(ItemStack stack)
    {
        if (AssemblyTags.hasInternalCore(stack)) stack = AssemblyTags.getInternalCore(stack);
        return MiscTags.getItemValue(stack);
    }


    protected static void insureRecursive(UUID ownerID, ItemStack stack)
    {
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack);
        for (IPartSlot partSlot : partSlots)
        {
            ItemStack part = partSlot.getPart();
            if (!part.isEmpty()) insure(ownerID, part);
        }
        AssemblyTags.setPartSlots(stack, partSlots);
        insure(ownerID, stack);
    }

    protected static void insure(UUID ownerID, ItemStack stack)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = stack.getTagCompound();
        MCTools.getOrGenerateSubCompound(compound, MODID).setUniqueId("insuredFor", ownerID);

        compound = MCTools.getSubCompoundIfExists(compound, "tiamatrpg", "core", "tag");
        if (compound != null) MCTools.getOrGenerateSubCompound(compound, MODID).setUniqueId("insuredFor", ownerID);
    }


    public static void uninsureRecursive(ItemStack stack)
    {
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack);
        for (IPartSlot partSlot : partSlots)
        {
            ItemStack part = partSlot.getPart();
            if (!part.isEmpty()) uninsure(part);
        }
        AssemblyTags.setPartSlots(stack, partSlots);
        uninsure(stack);
    }

    public static void uninsure(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = stack.getTagCompound(), compound2 = MCTools.getSubCompoundIfExists(compound, MODID);
        if (compound2 != null)
        {
            compound2.removeTag("insuredFor");
            if (compound2.hasNoTags()) compound.removeTag(MODID);
        }


        compound = MCTools.getSubCompoundIfExists(compound, "tiamatrpg", "core", "tag");
        if (compound == null) return;

        compound2 = MCTools.getSubCompoundIfExists(compound, MODID);
        if (compound2 != null)
        {
            compound2.removeTag("insuredFor");
            if (compound2.hasNoTags()) compound.removeTag(MODID);
        }
    }


    public static HashMap<UUID, ArrayList<ItemStack>> splitItemIntoInsuredParts(ItemStack stack)
    {
        HashMap<UUID, ArrayList<ItemStack>> result = new HashMap<>();
        UUID owner = getInsuranceOwnerForPart(stack);
        if (owner != null) result.computeIfAbsent(owner, o -> new ArrayList<>()).add(stack);

        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack);
        for (IPartSlot partSlot : partSlots)
        {
            ItemStack part = partSlot.getPart();
            if (!part.isEmpty())
            {
                HashMap<UUID, ArrayList<ItemStack>> subResult = splitItemIntoInsuredParts(part);
                for (UUID key : subResult.keySet()) result.computeIfAbsent(key, o -> new ArrayList<>()).addAll(subResult.get(key));
            }
            partSlot.setPart(ItemStack.EMPTY);
        }

        AssemblyTags.setPartSlots(stack, partSlots);

        return result;
    }

    protected static UUID getInsuranceOwnerForPart(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        return compound == null || !compound.hasKey("insuredFor") ? null : compound.getUniqueId("insuredFor");
    }


    @SubscribeEvent
    public static void itemJoinTempInstance(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        World world = event.getWorld();
        if (world.isRemote || !(entity instanceof EntityItem) || !(world instanceof WorldInstance) || InstanceData.get(world).saves()) return;

        TRACKED_ITEMS.computeIfAbsent(world, o -> new HashMap<>()).put(entity.getUniqueID(), ((EntityItem) entity).getItem());
    }

    @SubscribeEvent
    public static void itemExpire(ItemExpireEvent event)
    {
        EntityItem entityItem = event.getEntityItem();
        if (entityItem.world.isRemote) return;

        HashMap<UUID, ItemStack> map = TRACKED_ITEMS.get(entityItem.world);
        if (map != null) map.remove(entityItem.getUniqueID());

        mailAllInsured(event.getEntityItem().getItem());
    }

    @SubscribeEvent
    public static void worldUnload(ChunkEvent.Unload event)
    {
        World world = event.getWorld();
        if (world.isRemote) return;

        HashMap<UUID, ItemStack> map = TRACKED_ITEMS.remove(world);
        if (map != null) mailAllInsured(map.values());
    }


    protected static void mailAllInsured(ItemStack... stacks)
    {
        mailAllInsured(Arrays.asList(stacks));
    }

    protected static void mailAllInsured(Collection<ItemStack> stacks)
    {
        HashMap<UUID, ArrayList<ItemStack>> allStacks = new HashMap<>();
        for (ItemStack itemStack : stacks)
        {
            for (Map.Entry<UUID, ArrayList<ItemStack>> entry : splitItemIntoInsuredParts(itemStack).entrySet())
            {
                allStacks.computeIfAbsent(entry.getKey(), o -> new ArrayList<>()).addAll(entry.getValue());
            }
        }
        if (allStacks.size() == 0) return;


        MailSystem mailSystem = (MailSystem) RpgEconomyAPI.getMailSystemManager().getMailSystems()[0];
        Date sendDateTime = Calendar.getInstance().getTime();
        String subject = "Insurance Returns";
        String message = "";


        for (Map.Entry<UUID, ArrayList<ItemStack>> entry : allStacks.entrySet())
        {
            PlayerData data = PlayerData.get(entry.getKey());

            GameProfile sender = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C7777777"), ProfessionsAndInteractions.INSURANCE_AGENT);

            NonNullList<ItemStack> attachments = NonNullList.create();
            attachments.addAll(entry.getValue());

            mailSystem.sendMailMessage(new MailMessage(-1, mailSystem, sender, new GameProfile(data.id, data.name), sendDateTime, subject, message, attachments, false));
        }
    }

    protected static int adjustedCost(int rawCost)
    {
        return rawCost;
    }
}
