package com.fantasticsource.faerunutils.bettercrafting.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class BetterRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }
}
