package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.bettercrafting.recipe.Recipes;
import com.fantasticsource.faerunutils.bettercrafting.recipes.RecipeRepair;
import com.fantasticsource.faerunutils.bettercrafting.recipes.RecipeSalvaging;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import java.io.IOException;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021c,)")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.002a";

    public static boolean faerun;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        MinecraftForge.EVENT_BUS.register(FaerunUtils.class);
        MinecraftForge.EVENT_BUS.register(DamageBlocker.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);

        GCMessageFixer.init();
        Network.init();

        Recipes.add(new RecipeRepair());
        Recipes.add(new RecipeSalvaging());
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        faerun = Loader.isModLoaded("bluerpg");
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void recipeRegistry(RegistryEvent.Register<IRecipe> event)
    {
        ForgeRegistry recipes = (ForgeRegistry) ForgeRegistries.RECIPES;
        for (ResourceLocation rl : (ResourceLocation[]) recipes.getKeys().toArray(new ResourceLocation[0])) recipes.remove(rl);
    }

//    @SubscribeEvent
//    public static void entityJoin(EntityJoinWorldEvent event)
//    {
//        if (event.getEntity() instanceof EntityItem)
//        {
//            EntityItem item = (EntityItem) event.getEntity();
//
//            if (item.getItem().getItem() == Items.DIAMOND_CHESTPLATE || item.getItem().getItem() == Items.DIAMOND_SWORD)
//            {
//                item.setDead();
//            }
//        }
//    }
}
