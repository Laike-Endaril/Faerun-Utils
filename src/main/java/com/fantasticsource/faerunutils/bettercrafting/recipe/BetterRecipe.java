package com.fantasticsource.faerunutils.bettercrafting.recipe;

import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingInput;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingOutput;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public abstract class BetterRecipe
{
    /**
     * Unlike the vanilla version, this is *only* for checking whether a recipe matches, not for caching.  Do not save any data from within this method!
     */
    abstract public boolean matches(InventoryBetterCraftingInput inv);

    /**
     * This is for setting the crafting output preview
     */
    abstract public void preview(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out);

    /**
     * This should only ever be called when the item in the output is removed
     */
    abstract public ArrayList<ItemStack> craft(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out);
}
