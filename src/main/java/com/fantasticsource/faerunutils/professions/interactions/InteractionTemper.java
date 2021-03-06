package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_GenericDouble;
import com.fantasticsource.tools.Tools;
import moe.plushie.rpg_framework.api.RpgEconomyAPI;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.IWallet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

import static com.fantasticsource.tiamatinventory.TiamatInventory.CURRENCY_CAPABILITY;

public class InteractionTemper extends AInteraction
{
    protected String type;

    public InteractionTemper(String type)
    {
        super("Temper " + type);
        this.type = type;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(ProfessionsAndInteractions.MASTER_SMITH)) return null;

        ItemStack stack = null;
        ITiamatPlayerInventory inv = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        switch (type)
        {
            case "Mainhand":
                stack = player.getHeldItemMainhand();
                break;

            case "Offhand":
                stack = player.inventory.offHandInventory.get(0);
                break;

            case "Sheathed Mainhand 1":
                if (inv != null) stack = inv.getSheathedMainhand1();
                break;

            case "Sheathed Offhand 1":
                if (inv != null) stack = inv.getSheathedOffhand1();
                break;

            case "Sheathed Mainhand 2":
                if (inv != null) stack = inv.getSheathedMainhand2();
                break;

            case "Sheathed Offhand 2":
                if (inv != null) stack = inv.getSheathedOffhand2();
                break;
        }
        if (stack == null || stack.getItem() != TiamatItems.tiamatItem) return null;

        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack);
        if (partSlots.size() == 0)
        {
            CItemType itemType = CSettings.LOCAL_SETTINGS.itemTypes.get(MiscTags.getItemTypeName(stack));
            if (itemType == null) return null;

            boolean found = false;
            for (CRecalculableTrait recalculableTrait : itemType.staticRecalculableTraits.values())
            {
                for (CRecalculableTraitElement element : recalculableTrait.elements)
                {
                    if (element instanceof CRTraitElement_GenericDouble && ((CRTraitElement_GenericDouble) element).name.equals("WeaponPower"))
                    {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) return null;


            return MiscTags.getItemValue(stack) > 0 ? name + TextFormatting.RED + " (costs " + getCost(stack) + ")" : null;
        }


        //Assembly
        for (IPartSlot partSlot : partSlots)
        {
            ItemStack part = partSlot.getPart();
            if (part.isEmpty()) continue;


            CItemType itemType = CSettings.LOCAL_SETTINGS.itemTypes.get(MiscTags.getItemTypeName(part));
            if (itemType == null) continue;

            boolean found = false;
            for (CRecalculableTrait recalculableTrait : itemType.staticRecalculableTraits.values())
            {
                for (CRecalculableTraitElement element : recalculableTrait.elements)
                {
                    if (element instanceof CRTraitElement_GenericDouble && ((CRTraitElement_GenericDouble) element).name.equals("WeaponPower"))
                    {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) continue;


            return MiscTags.getItemValue(part) > 0 ? name + TextFormatting.RED + " (costs " + getCost(part) + ")" : null;
        }


        return null;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return null;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        //Get item
        ItemStack stack = null;
        ITiamatPlayerInventory inv = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        switch (type)
        {
            case "Mainhand":
                stack = player.getHeldItemMainhand();
                break;

            case "Offhand":
                stack = player.inventory.offHandInventory.get(0);
                break;

            case "Sheathed Mainhand 1":
                if (inv != null) stack = inv.getSheathedMainhand1();
                break;

            case "Sheathed Offhand 1":
                if (inv != null) stack = inv.getSheathedOffhand1();
                break;

            case "Sheathed Mainhand 2":
                if (inv != null) stack = inv.getSheathedMainhand2();
                break;

            case "Sheathed Offhand 2":
                if (inv != null) stack = inv.getSheathedOffhand2();
                break;
        }


        //Find out if we're dealing with a sub-part or not and calculate cost
        int cost = getCost(stack);
        ItemStack foundPart = null;
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack);
        for (IPartSlot partSlot : partSlots)
        {
            ItemStack part = partSlot.getPart();
            if (part.isEmpty()) continue;


            CItemType itemType = CSettings.LOCAL_SETTINGS.itemTypes.get(MiscTags.getItemTypeName(part));
            if (itemType == null) continue;

            boolean found = false;
            for (CRecalculableTrait recalculableTrait : itemType.staticRecalculableTraits.values())
            {
                for (CRecalculableTraitElement element : recalculableTrait.elements)
                {
                    if (element instanceof CRTraitElement_GenericDouble && ((CRTraitElement_GenericDouble) element).name.equals("WeaponPower"))
                    {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) continue;


            foundPart = part;
            cost = getCost(part);
            break;
        }


        //Reduce funds or reject attempt if too poor
        if (!player.isCreative())
        {
            ICurrency[] currencies = RpgEconomyAPI.getCurrencyManager().getCurrencies();
            ICurrency currency = currencies[0];
            IWallet wallet = player.getCapability(CURRENCY_CAPABILITY, null).getWallet(currency);
            int money = wallet.getAmount();
            if (cost > money)
            {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have enough money!"));
                return false;
            }
            wallet.setAmount(money - cost);
        }


        //Roll
        int newRoll = Tools.random(Integer.MAX_VALUE);


        //NBT alterations
        NBTTagCompound compound = foundPart == null ? stack.getTagCompound() : foundPart.getTagCompound();
        NBTTagCompound compound2 = MCTools.getSubCompoundIfExists(compound, "tiamatrpg");
        NBTTagList list = (NBTTagList) compound2.getTag("traits");
        for (int i = 0; i < list.tagCount(); i++)
        {
            String s = list.getStringTagAt(i);
            if (s.contains("Static Traits"))
            {
                list.removeTag(i);
                list.appendTag(new NBTTagString("Static:Static Traits:" + newRoll));
                break;
            }
        }

        compound2 = MCTools.getSubCompoundIfExists(compound, "tiamatrpg", "core", "tag", "tiamatrpg");
        if (compound2 != null)
        {
            list = (NBTTagList) compound2.getTag("traits");
            for (int i = 0; i < list.tagCount(); i++)
            {
                String s = list.getStringTagAt(i);
                if (s.contains("Static Traits"))
                {
                    list.removeTag(i);
                    list.appendTag(new NBTTagString("Static:Static Traits:" + newRoll));
                    break;
                }
            }
        }


        if (foundPart != null) AssemblyTags.setPartSlots(stack, partSlots);


        return true;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
    }


    protected int getCost(ItemStack stack)
    {
        return MiscTags.getItemValue(stack);
    }
}
