package com.fantasticsource.faerunutils.bettercrafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class Recipes
{
    public static final ItemStack POWDER = new ItemStack(Items.GOLD_NUGGET);
    public static final ItemStack TOKEN = new ItemStack(Items.SLIME_BALL);

    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(Recipes.class);

        BetterRecipe.betterRecipes.add(new RecipeSalvaging());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void recipeRegistry(RegistryEvent.Register<IRecipe> event)
    {
        ForgeRegistry recipes = (ForgeRegistry) ForgeRegistries.RECIPES;
        for (ResourceLocation rl : (ResourceLocation[]) recipes.getKeys().toArray(new ResourceLocation[0])) recipes.remove(rl);
    }
}
