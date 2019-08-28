package com.fantasticsource.faerunutils.bettercrafting.recipe;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingInput;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingOutput;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Recipes
{
    public static final LinkedHashMap<String, BetterRecipe> recipeList = new LinkedHashMap<>();

    public static final BetterRecipe NULL = new BetterRecipe()
    {
        @Override
        public String translationKey()
        {
            return FaerunUtils.MODID + ":recipe.null";
        }

        @Override
        public Color color()
        {
            return Color.GRAY;
        }

        @Override
        public boolean matches(InventoryBetterCraftingInput inv)
        {
            return false;
        }

        @Override
        public void preview(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out)
        {
            out.setInventorySlotContents(0, ItemStack.EMPTY);
        }

        @Override
        public ArrayList<ItemStack> craft(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out, ItemStack grabbedStack)
        {
            return new ArrayList<>();
        }
    };


    public static void add(BetterRecipe recipe)
    {
        recipeList.put(recipe.translationKey(), recipe);
    }

    public static void remove(BetterRecipe recipe)
    {
        recipeList.remove(recipe.translationKey());
    }

    public static BetterRecipe get(String translationKey)
    {
        return recipeList.get(translationKey);
    }
}
