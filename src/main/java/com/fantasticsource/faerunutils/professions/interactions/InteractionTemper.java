package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.assembly.ItemAssembly;
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
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.tiamatinventory.TiamatInventory.CURRENCY_CAPABILITY;

public class InteractionTemper extends AInteraction
{
    protected boolean mainhand;

    public InteractionTemper(boolean mainhand)
    {
        super("Temper " + (mainhand ? "Mainhand" : "Offhand") + " Item");
        this.mainhand = mainhand;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(ProfessionsAndInteractions.MASTER_SMITH)) return null;

        ItemStack stack = mainhand ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
        if (stack.getItem() != TiamatItems.tiamatItem) return null;

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


        int money = 0;
        if (CURRENCY_CAPABILITY != null)
        {
            ICurrency[] currencies = RpgEconomyAPI.getCurrencyManager().getCurrencies();
            if (currencies.length > 0)
            {
                ICurrency currency = currencies[0];
                if (currency != null)
                {
                    money = player.getCapability(CURRENCY_CAPABILITY, null).getWallet(currency).getAmount();
                }
            }
        }
        int cost = getCost(stack);
        return cost > 0 && money >= MiscTags.getItemValue(stack) ? name + TextFormatting.RED + " (costs " + cost + ")" : null;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return null;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        //Reduce funds
        ItemStack stack = mainhand ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
        ICurrency[] currencies = RpgEconomyAPI.getCurrencyManager().getCurrencies();
        ICurrency currency = currencies[0];
        IWallet wallet = player.getCapability(CURRENCY_CAPABILITY, null).getWallet(currency);
        wallet.setAmount(wallet.getAmount() - getCost(stack));


        //Roll
        int newRoll = Tools.random(Integer.MAX_VALUE);


        //NBT alterations
        NBTTagCompound compound = stack.getTagCompound(), compound2 = MCTools.getSubCompoundIfExists(compound, "tiamatrpg");
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


        //Recalc
        ItemAssembly.recalc(player, stack, true);

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
