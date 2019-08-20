package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.bettercrafting.recipes.Recipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021a,)")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.001a";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        MinecraftForge.EVENT_BUS.register(FaerunUtils.class);
        MinecraftForge.EVENT_BUS.register(DamageBlocker.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);
        MinecraftForge.EVENT_BUS.register(Recipes.class);

        GCMessageFixer.init();
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }
}
