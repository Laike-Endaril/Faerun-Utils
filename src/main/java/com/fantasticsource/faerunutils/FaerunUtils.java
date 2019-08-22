package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.bettercrafting.recipe.Recipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021a,)")
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
        Recipes.init();
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
