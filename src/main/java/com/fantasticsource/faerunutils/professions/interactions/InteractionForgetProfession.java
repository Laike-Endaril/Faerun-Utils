package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.professions.Professions;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class InteractionForgetProfession extends AInteraction
{
    public final String profession;

    public InteractionForgetProfession(String profession)
    {
        super("Forget " + profession);
        this.profession = profession;
    }

    @Override
    public boolean available(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(Professions.LABOTOMIST_NAME)) return false;

        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        if (inventory == null) return false;

        for (ItemStack stack : inventory.getCraftingProfessions()) if (profession.equals(stack.getDisplayName())) return true;
        for (ItemStack stack : inventory.getGatheringProfessions()) if (profession.equals(stack.getDisplayName())) return true;
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
        for (ItemStack stack : inventory.getCraftingProfessions())
        {
            if (profession.equals(stack.getDisplayName())) inventory.setCraftingProfession(i, ItemStack.EMPTY);
            i++;
        }
        i = 0;
        for (ItemStack stack : inventory.getGatheringProfessions())
        {
            if (profession.equals(stack.getDisplayName())) inventory.setGatheringProfession(i, ItemStack.EMPTY);
            i++;
        }
        return true;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
    }
}
