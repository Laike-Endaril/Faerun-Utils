package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.faerunutils.actions.Cooldown;
import com.fantasticsource.faerunutils.bag.CmdOpenBag;
import com.fantasticsource.faerunutils.potions.PotionDeepWounds;
import com.fantasticsource.faerunutils.potions.PotionDefinitions;
import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.faerunutils.professions.interactions.InteractionInsure;
import com.fantasticsource.instances.Destination;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.tags.entity.EscapePoint;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.tiamatactions.action.ActionQueue;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatitems.TransientAttributeModEvent;
import com.fantasticsource.tools.Tools;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

import java.util.ArrayList;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.044zzzh,);required-after:instances@[1.12.2.001e,);required-after:tiamatitems@[1.12.2.000zzq,);required-after:tiamatinventory@[1.12.2.000zzc,);required-after:tiamatinteractions@[1.12.2.000d,);required-after:tiamatactions@[1.12.2.000zzzf,);required-after:dynamicstealth@[1.12.2.113e,)")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.Era2.028";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Attributes.init();
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
            Keys.init(event);
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

            //Remove vanilla knockback
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


    public static boolean canBlock(Entity entity)
    {
        ArrayList<CAction> actions = ActionQueue.get(entity, "Main").queue;
        return actions.size() == 0 || actions.get(0).getClass() == Cooldown.class;
    }

    public static void tryUseItemAction(EntityLivingBase livingBase, boolean mainhand, int index)
    {
        ItemStack stack, other;
        if (mainhand)
        {
            stack = livingBase.getHeldItemMainhand();
            other = livingBase.getHeldItemOffhand();
        }
        else
        {
            stack = livingBase.getHeldItemOffhand();
            other = livingBase.getHeldItemMainhand();
        }
        if (stack.isEmpty() && Slottings.isTwoHanded(other)) stack = other;

        String actionName = null;
        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), "tiamatitems", "generic");
            if (compound != null) actionName = compound.getString((mainhand ? "mainhand" : "offhand") + index);
        }
        if (actionName == null || actionName.equals("")) actionName = mainhand ? "faerunutils.skill.unarmed.straight" : "faerunutils.skill.unarmed.jab";

        tryUseAction(livingBase, CAction.ALL_ACTIONS.get(actionName));
    }

    public static void tryUseAction(EntityLivingBase livingBase, CAction action)
    {
        if (!(action instanceof CFaerunAction) || canUseAction(livingBase, (CFaerunAction) action)) action.queue(livingBase, "Main");
    }

    public static boolean canUseAction(Entity entity, CFaerunAction action)
    {
        if (Attributes.COMBO_USAGE.getCurrentAmount(entity) + action.attributes.getOrDefault(Attributes.COMBO_USAGE, 0d) >= 100) return false;

        ArrayList<CAction> queue = ActionQueue.get(entity, "Main").queue;
        if (queue.size() == 0) return true;

        CAction currentAction = queue.get(0);
        if (currentAction instanceof Cooldown) return false;
        if (!(currentAction instanceof CFaerunAction)) return true;
        return ((CFaerunAction) currentAction).canComboTo.contains(action.name);
    }
}
