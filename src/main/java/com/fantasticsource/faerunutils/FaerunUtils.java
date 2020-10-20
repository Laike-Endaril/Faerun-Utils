package com.fantasticsource.faerunutils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.038i,);required-after:tiamatitems@[1.12.2.000zb,)")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.Era2.000a";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(FaerunUtils.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CmdDie());
    }

    @SubscribeEvent
    public static void cancelHPRegen(LivingHealEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer) event.setCanceled(true);
    }
}
