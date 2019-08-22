package com.fantasticsource.faerunutils.bettercrafting.recipe;

import java.util.LinkedHashMap;

public class Recipes
{
    public static final LinkedHashMap<String, BetterRecipe> recipeList = new LinkedHashMap<>();


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
