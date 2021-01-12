package com.fantasticsource.faerunutils;

import com.fantasticsource.instances.Destination;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.tags.entity.EscapePoint;
import com.fantasticsource.mctools.ServerTickTimer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.044q,);required-after:instances@[1.12.2.001e,);required-after:tiamatitems@[1.12.2.000zz,);required-after:tiamatinventory@[1.12.2.000zt,)")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.Era2.001";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(FaerunUtils.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);
        Network.init();
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
        event.registerServerCommand(new CmdInteract());
        event.registerServerCommand(new CmdJoinInstanceType());
    }

    @SubscribeEvent
    public static void cancelHPRegen(LivingHealEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerDeath(PlayerEvent.Clone event)
    {
        if (!event.isWasDeath()) return;

        Destination destination = EscapePoint.getEscapePoint(event.getEntityPlayer());
        ServerTickTimer.schedule(1, () ->
        {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            player.setHealth(1);
            EscapePoint.setEscapePoint(player, destination);
            Teleport.escape(player);
        });
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        event.player.getFoodStats().setFoodLevel(20);
    }
}
