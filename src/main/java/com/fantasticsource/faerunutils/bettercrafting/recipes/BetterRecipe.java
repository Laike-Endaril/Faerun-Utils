package com.fantasticsource.faerunutils.bettercrafting.recipes;

import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingInput;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingOutput;

import java.util.ArrayList;

public abstract class BetterRecipe
{
    public static final ArrayList<BetterRecipe> betterRecipes = new ArrayList<>();

    abstract public boolean matches(InventoryBetterCraftingInput inv);

    abstract public void preview(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out);

    abstract public void craft(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out);
}
