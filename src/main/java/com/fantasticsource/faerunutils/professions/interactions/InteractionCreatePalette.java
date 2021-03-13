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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.tiamatinventory.TiamatInventory.CURRENCY_CAPABILITY;

public class InteractionCreatePalette extends AInteraction
{
    protected static final int COST = 500;
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
        if (!MiscTags.getItemTypeName(stack).contains("Blueprint")) return null;

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


        LinkedHashMap<Integer, Color> dyeOverrides = MiscTags.getDyeOverrides(getItem(player));

        ItemStack palette = CSettings.LOCAL_SETTINGS.itemTypes.get("Palette").generateItem(0, CSettings.LOCAL_SETTINGS.rarities.get("Fine"));
        MiscTags.setDyeOverrides(palette, dyeOverrides);
        ArrayList<String> textureLayers = TextureTags.getItemLayers(palette, AssemblyTags.STATE_FULL);
        for (int i = 0; i < textureLayers.size(); i++)
        {
            String[] tokens = Tools.fixedSplit(textureLayers.get(i), ":");
            int colorIndex = Integer.parseInt(tokens[1]);
            if (dyeOverrides.containsKey(colorIndex)) textureLayers.set(i, tokens[0] + colorIndex + dyeOverrides.get(colorIndex).hex8());
        }
        TextureTags.setItemLayers(palette, AssemblyTags.STATE_FULL, textureLayers);

        MCTools.give(player, palette);

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

            case "Helm":
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
}
