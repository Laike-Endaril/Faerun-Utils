package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.professions.Professions;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

public class InteractionQuitProfession extends AInteraction
{
    public final String profession, type;

    public InteractionQuitProfession(String profession, String type)
    {
        super("Quit " + profession);
        this.profession = profession;
        this.type = type;
    }

    @Override
    public boolean available(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(Professions.CRAFTING_PROFESSION_NPCS[Tools.indexOf(Professions.CRAFTING_PROFESSIONS, profession)])) return false;

        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        if (inventory == null) return false;

        if (type.equals("crafting"))
        {
            for (ItemStack stack : inventory.getCraftingProfessions()) if (profession.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()))) return true;
        }
        else
        {
            for (ItemStack stack : inventory.getCraftingProfessions()) if (profession.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()))) return true;
        }
        return false;
    }

    @Override
    public boolean available(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
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
                if (profession.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()))) inventory.setCraftingProfession(i, ItemStack.EMPTY);
                i++;
            }
        }
        else
        {
            for (ItemStack stack : inventory.getGatheringProfessions())
            {
                if (profession.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()))) inventory.setGatheringProfession(i, ItemStack.EMPTY);
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
