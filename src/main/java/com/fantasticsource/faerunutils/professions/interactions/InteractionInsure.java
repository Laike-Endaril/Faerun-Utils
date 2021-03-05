package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import moe.plushie.rpg_framework.api.RpgEconomyAPI;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.IWallet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.fantasticsource.tiamatinventory.TiamatInventory.CURRENCY_CAPABILITY;
import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class InteractionInsure extends AInteraction
{
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


        return stacks.size() > 0 ? name + TextFormatting.RED + " (costs " + cost + ")" : null;
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


    protected int adjustedCost(int rawCost)
    {
        return rawCost >> 3;
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
                return stacks;

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
                return stacks;

            case "Mainhand":
                stacks.add(player.getHeldItemMainhand());
                return stacks;

            case "Offhand":
                stacks.addAll(player.inventory.offHandInventory);
                return stacks;

            case "Helm":
                stacks.add(player.inventory.armorInventory.get(3));
                return stacks;

            case "Shoulder Armor":
                if (inv != null) stacks.add(inv.getShoulders());
                return stacks;

            case "Cape":
                if (inv != null) stacks.add(inv.getCape());
                return stacks;

            case "Chestpiece":
                stacks.add(player.inventory.armorInventory.get(2));
                return stacks;

            case "Leg Armor":
                stacks.add(player.inventory.armorInventory.get(1));
                return stacks;

            case "Boots":
                stacks.add(player.inventory.armorInventory.get(0));
                return stacks;
        }
        stacks.removeIf(ItemStack::isEmpty);
        return stacks;
    }


    public int insuranceCostRecursive(UUID ownerID, ItemStack stack)
    {
        int result = insuranceCost(ownerID, stack);
        for (IPartSlot partSlot : AssemblyTags.getPartSlots(stack))
        {
            ItemStack part = partSlot.getPart();
            if (!part.isEmpty()) result += insuranceCostRecursive(ownerID, part);
        }
        return result;
    }

    protected int insuranceCost(UUID ownerID, ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        return compound != null && ownerID.equals(compound.getUniqueId("insured")) ? 0 : getPartValue(stack);
    }

    protected int getPartValue(ItemStack stack)
    {
        if (AssemblyTags.hasInternalCore(stack)) stack = AssemblyTags.getInternalCore(stack);
        return MiscTags.getItemValue(stack);
    }


    protected void insureRecursive(UUID ownerID, ItemStack stack)
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

    protected void insure(UUID ownerID, ItemStack stack)
    {
        NBTTagCompound compound = stack.getTagCompound();
        MCTools.getOrGenerateSubCompound(compound, MODID).setUniqueId("insuredFor", ownerID);

        compound = MCTools.getSubCompoundIfExists(compound, "tiamatrpg", "core", "tag");
        if (compound != null) MCTools.getOrGenerateSubCompound(compound, MODID).setUniqueId("insuredFor", ownerID);
    }


    public HashMap<UUID, ArrayList<ItemStack>> splitItemIntoInsuredParts(ItemStack stack)
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

    protected UUID getInsuranceOwnerForPart(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        return compound == null || !compound.hasKey("insuredFor") ? null : compound.getUniqueId("insuredFor");
    }
}
