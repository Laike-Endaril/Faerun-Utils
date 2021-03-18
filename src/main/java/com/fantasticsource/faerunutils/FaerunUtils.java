package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.bag.CmdOpenBag;
import com.fantasticsource.faerunutils.potions.PotionDeepWounds;
import com.fantasticsource.faerunutils.potions.PotionDefinitions;
import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.faerunutils.professions.interactions.InteractionInsure;
import com.fantasticsource.instances.Destination;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.tags.entity.EscapePoint;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.tiamatitems.TransientAttributeModEvent;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.044zr,);required-after:instances@[1.12.2.001e,);required-after:tiamatitems@[1.12.2.000zzh,);required-after:tiamatinventory@[1.12.2.000zu,);required-after:tiamatinteractions@[1.12.2.000d,)")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.Era2.024";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(FaerunUtils.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);
        MinecraftForge.EVENT_BUS.register(ProfessionsAndInteractions.class);
        MinecraftForge.EVENT_BUS.register(InteractionInsure.class);
        MinecraftForge.EVENT_BUS.register(ServerChatAlterer.class);
        MinecraftForge.EVENT_BUS.register(PotionDefinitions.class);
        MinecraftForge.EVENT_BUS.register(PotionDeepWounds.class);
        Network.init();
        initConfig();
        ProfessionsAndInteractions.init();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(TooltipAlterer.class);
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void syncConfig(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) initConfig();
    }

    public static void initConfig()
    {
        ServerChatAlterer.PATREON_NAMES.clear();
        for (String s : FaerunConfig.patreonNames)
        {
            String tokens[] = Tools.fixedSplit(s, ",");
            ServerChatAlterer.PATREON_NAMES.put(Integer.parseInt(tokens[0].trim()), tokens[1].trim());
        }
    }

    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CmdDie());
        event.registerServerCommand(new CmdJoinInstanceType());
        event.registerServerCommand(new CmdOpenBag());
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
        EntityPlayer player = event.player;

        //Prevent hunger
        player.getFoodStats().setFoodLevel(20);

        //Prevent others from nudging you (via client hack...because Minecraft)
        if (player.world.isRemote) player.entityCollisionReduction = 1;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void entityJoin(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityItem) entity.setEntityInvulnerable(true);
        else if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase livingBase = (EntityLivingBase) entity;

            //Remove knockback
            AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();
            attributeMap.getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        }
    }


    @SubscribeEvent
    public static void transientAttributeMods(TransientAttributeModEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (!(livingBase instanceof EntityPlayerMP)) return;


        EntityPlayerMP player = (EntityPlayerMP) livingBase;
        InventoryPlayer inv = player.inventory;
        int filled = 0;
        for (int i = 0; i < 27; i++)
        {
            if (!inv.getStackInSlot(9 + i).isEmpty()) filled++;
        }

        event.applyTransientModifier(MODID + ":ItemWeight", "generic.movementSpeed", 1, -0.5 * filled / 27);
    }
}
