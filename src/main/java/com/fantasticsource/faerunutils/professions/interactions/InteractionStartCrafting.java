package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.Network;
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

public class InteractionStartCrafting extends AInteraction
{
    public final String profession;

    public InteractionStartCrafting(String profession)
    {
        super("Craft (" + profession + ")");
        this.profession = profession;
    }

    @Override
    public boolean available(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(Professions.CRAFTING_PROFESSION_NPCS[Tools.indexOf(Professions.CRAFTING_PROFESSIONS, profession)])) return false;

        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        if (inventory == null) return false;

        for (ItemStack stack : inventory.getCraftingProfessions()) if (profession.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()))) return true;
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

        for (ItemStack stack : inventory.getCraftingProfessions())
        {
            if (profession.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName())))
            {
                Network.WRAPPER.sendTo(new Network.CraftPacket(stack), player);
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
