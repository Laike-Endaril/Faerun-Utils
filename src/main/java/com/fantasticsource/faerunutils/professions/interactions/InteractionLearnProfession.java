package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

public class InteractionLearnProfession extends AInteraction
{
    public final String profession, type;

    public InteractionLearnProfession(String profession, String type)
    {
        super("Learn " + profession);
        this.profession = profession;
        this.type = type;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(ProfessionsAndInteractions.CRAFTING_PROFESSION_NPCS[Tools.indexOf(ProfessionsAndInteractions.CRAFTING_PROFESSIONS, profession)])) return null;

        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        if (inventory == null) return null;

        boolean emptyFound = false;
        if (type.equals("crafting"))
        {
            for (ItemStack stack : inventory.getCraftingProfessions())
            {
                if (stack.isEmpty()) emptyFound = true;
                else if (profession.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()))) return null;
            }
        }
        else if (type.equals("gathering"))
        {
            for (ItemStack stack : inventory.getGatheringProfessions())
            {
                if (stack.isEmpty()) emptyFound = true;
                else if (profession.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()))) return null;
            }
        }
        return emptyFound ? name : null;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return null;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        if (inventory == null) return true;

        int i = 0;
        if (type.equals("crafting"))
        {
            for (ItemStack stack : inventory.getCraftingProfessions())
            {
                if (stack.isEmpty())
                {
                    inventory.setCraftingProfession(i, CSettings.LOCAL_SETTINGS.itemTypes.get(profession).generateItem(0, CSettings.LOCAL_SETTINGS.rarities.get("Crude")));
                    return true;
                }
                i++;
            }
        }
        else if (type.equals("gathering"))
        {
            for (ItemStack stack : inventory.getGatheringProfessions())
            {
                if (stack.isEmpty())
                {
                    inventory.setGatheringProfession(i, CSettings.LOCAL_SETTINGS.itemTypes.get(profession).generateItem(0, CSettings.LOCAL_SETTINGS.rarities.get("Crude")));
                    return true;
                }
                i++;
            }
        }
        return true;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
    }
}
