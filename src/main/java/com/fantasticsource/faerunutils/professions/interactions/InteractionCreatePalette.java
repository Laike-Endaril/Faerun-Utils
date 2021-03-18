package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.Network;
import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.TextureTags;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import moe.plushie.rpg_framework.api.RpgEconomyAPI;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
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
import java.util.LinkedHashMap;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;
import static com.fantasticsource.tiamatinventory.TiamatInventory.CURRENCY_CAPABILITY;

public class InteractionCreatePalette extends AInteraction
{
    protected static final int COST = 2500;
    protected static final String COST_STRING = "5s";

    protected String type;

    public InteractionCreatePalette(String type)
    {
        super("Make dye palette from " + type + " (costs " + COST_STRING + ")");
        this.type = type;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(ProfessionsAndInteractions.MASTER_DESIGNER)) return null;


        ItemStack stack = getItem(player);
        if (!canMakePaletteFrom(stack)) return null;

        return name;
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
        if (!player.isCreative())
        {
            ICurrency[] currencies = RpgEconomyAPI.getCurrencyManager().getCurrencies();
            ICurrency currency = currencies[0];
            ICurrencyCapability capability = player.getCapability(CURRENCY_CAPABILITY, null);
            IWallet wallet = capability.getWallet(currency);
            int money = wallet.getAmount();
            if (COST > money)
            {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have enough money!"));
                return false;
            }
            wallet.setAmount(money - COST);
            capability.syncToOwner(player, true);
        }


        //Set dye override colors and layer colors
        ItemStack source = getItem(player);
        LinkedHashMap<Integer, Color> dyeOverrides = MiscTags.getDyeOverrides(source);
        ItemStack palette = CSettings.LOCAL_SETTINGS.itemTypes.get("Palette").generateItem(0, CSettings.LOCAL_SETTINGS.rarities.get("Fine"));
        MiscTags.setDyeOverrides(palette, dyeOverrides);
        ArrayList<String> textureLayers = TextureTags.getItemLayers(palette, AssemblyTags.STATE_FULL);
        for (int i = 0; i < textureLayers.size(); i++)
        {
            String[] tokens = Tools.fixedSplit(textureLayers.get(i), ":");
            int colorIndex = Integer.parseInt(tokens[1]);
            if (dyeOverrides.containsKey(colorIndex)) textureLayers.set(i, tokens[0] + ":" + colorIndex + ":" + dyeOverrides.get(colorIndex).hex8());
        }
        TextureTags.setItemLayers(palette, AssemblyTags.STATE_FULL, textureLayers);


        //Remove item core and type so it doesn't recalc and lose colors
        AssemblyTags.removeInternalCore(palette);
        palette.getTagCompound().getCompoundTag("tiamatrpg").removeTag("type");


        //Give palette
        MCTools.give(player, palette);


        //Mark item it came from so it can't have another palette made from it
        blockPaletteCreation(source);


        return true;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
    }


    protected ItemStack getItem(EntityPlayerMP player)
    {
        ITiamatPlayerInventory inv = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        switch (type)
        {
            case "Mainhand":
                return player.getHeldItemMainhand();

            case "Offhand":
                return player.inventory.offHandInventory.get(0);

            case "Sheathed Mainhand 1":
                if (inv != null) return inv.getSheathedMainhand1();

            case "Sheathed Offhand 1":
                if (inv != null) return inv.getSheathedOffhand1();

            case "Sheathed Mainhand 2":
                if (inv != null) return inv.getSheathedMainhand2();

            case "Sheathed Offhand 2":
                if (inv != null) return inv.getSheathedOffhand2();

            case "Headpiece":
                return player.inventory.armorInventory.get(3);

            case "Shoulder Armor":
                if (inv != null) return inv.getShoulders();

            case "Cape":
                if (inv != null) return inv.getCape();

            case "Chestpiece":
                return player.inventory.armorInventory.get(2);

            case "Leg Armor":
                return player.inventory.armorInventory.get(1);

            case "Boots":
                return player.inventory.armorInventory.get(0);
        }

        return null;
    }


    public static void usePalette(EntityPlayerMP player, boolean mainhand)
    {
        //To be called from the LC / RC action on the Tiamat Item
        Network.WRAPPER.sendTo(new Network.RequestPaletteTargetPacket(mainhand), player);
    }

    public static boolean canMakePaletteFrom(ItemStack stack)
    {
        if (MiscTags.getDyeOverrides(stack) == null) return false;
        if (TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()).equals("Palette")) return false;
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        return compound == null || !compound.getBoolean("blockPaletteCreation");
    }

    public static void blockPaletteCreation(ItemStack stack)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();
        MCTools.getOrGenerateSubCompound(compound, MODID).setBoolean("blockPaletteCreation", true);
        if (AssemblyTags.hasInternalCore(stack)) MCTools.getOrGenerateSubCompound(compound, "tiamatrpg", "core", "tag", MODID).setBoolean("blockPaletteCreation", true);
    }
}
