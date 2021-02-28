package com.fantasticsource.faerunutils.professions.interactions;

import com.fantasticsource.faerunutils.Network;
import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class InteractionForgetRecipe extends AInteraction
{
    public final String profession, recipe;

    public InteractionForgetRecipe(String profession, String recipe)
    {
        super("Forget " + recipe);
        this.profession = profession;
        this.recipe = recipe;
    }

    @Override
    public String titleIfAvailable(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        if (!target.getName().equals(ProfessionsAndInteractions.CRAFTING_PROFESSION_NPCS[Tools.indexOf(ProfessionsAndInteractions.CRAFTING_PROFESSIONS, profession)])) return null;

        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        if (inventory == null) return null;

        for (ItemStack stack : inventory.getCraftingRecipes()) if (recipe.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()))) return name;
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
        Network.WRAPPER.sendTo(new Network.RequestConfirmForgetPacket(recipe), player);
        return true;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
    }

    public static void forget(EntityPlayerMP player, String recipe)
    {
        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(player);
        if (inventory == null) return;

        int i = 0, foundIndex = -1, foundLevel = -1, foundExp = -1;
        ItemStack found = null;
        for (ItemStack stack : inventory.getCraftingRecipes())
        {
            if (recipe.equals(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName())))
            {
                NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
                int level = MiscTags.getItemLevel(stack), exp = compound == null ? 0 : compound.getInteger("exp");

                if (found == null || level > foundLevel || (level == foundLevel && exp > foundExp))
                {
                    found = stack;
                    foundIndex = i;
                    foundLevel = level;
                    foundExp = exp;
                }
            }
            i++;
        }

        if (found != null)
        {
            inventory.setCraftingRecipe(foundIndex, ItemStack.EMPTY);
        }
    }
}
