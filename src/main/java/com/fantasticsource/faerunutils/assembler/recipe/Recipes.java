package com.fantasticsource.faerunutils.assembler.recipe;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.assembler.table.InventoryAssemblerInput;
import com.fantasticsource.faerunutils.assembler.table.InventoryAssemblerOutput;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
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
        public boolean matches(InventoryAssemblerInput inv)
        {
            return false;
        }

        @Override
        public Pair<ItemStack, ItemStack> prepareToCraft(InventoryAssemblerInput in)
        {
            return new Pair<>(ItemStack.EMPTY, ItemStack.EMPTY);
        }

        @Override
        public ArrayList<ItemStack> craft(InventoryAssemblerInput in, InventoryAssemblerOutput out, ItemStack grabbedStack)
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
