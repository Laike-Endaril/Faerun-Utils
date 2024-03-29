package com.fantasticsource.faerunutils;

import com.fantasticsource.customentities.CustomLivingEntity;
import com.fantasticsource.dynamicstealth.server.ai.edited.AINearestAttackableTargetEdit;
import com.fantasticsource.faeruncharacters.VoiceSets;
import com.fantasticsource.faeruncharacters.nbt.CharacterTags;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.faerunutils.actions.ComboGracePeriod;
import com.fantasticsource.faerunutils.actions.Cooldown;
import com.fantasticsource.faerunutils.bag.CmdOpenBag;
import com.fantasticsource.faerunutils.potions.PotionDeepWounds;
import com.fantasticsource.faerunutils.potions.PotionDefinitions;
import com.fantasticsource.faerunutils.professions.ProfessionsAndInteractions;
import com.fantasticsource.faerunutils.professions.interactions.InteractionInsure;
import com.fantasticsource.instances.Destination;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.tags.entity.EscapePoint;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tiamatactions.action.ActionQueue;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.path.CPath;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.044zzzzm,);required-after:fantasticaw@[1.12.2.000d,);required-after:customentities@[1.12.2.000,);required-after:weightedspawnzones@[1.12.2.000,);;required-after:instances@[1.12.2.001e,);required-after:tiamatitems@[1.12.2.000zzq,);required-after:tiamatinventory@[1.12.2.000zzc,);required-after:tiamatinteractions@[1.12.2.000d,);required-after:tiamatactions@[1.12.2.000zzzf,);required-after:dynamicstealth@[1.12.2.113e,)")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.Era2.030";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Attributes.init();
        MinecraftForge.EVENT_BUS.register(FaerunUtils.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);
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

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        CFaerunAction.init(event);
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


            if (entity instanceof CustomLivingEntity)
            {
                CustomLivingEntity customEntity = (CustomLivingEntity) entity;
                customEntity.targetTasks.addTask(0, new AINearestAttackableTargetEdit<EntityPlayerMP>(new EntityAINearestAttackableTarget(customEntity, EntityPlayerMP.class, 0, true, false, e -> true)));
//                customEntity.tasks.addTask(0, new AIFaerunMelee(customEntity));
            }
        }
    }


    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayerMP)) return;


        EntityPlayerMP player = (EntityPlayerMP) entity;
        InventoryPlayer inv = player.inventory;
        int filled = 0;
        ItemStack stack;
        for (int i = 0; i < 27; i++) //27
        {
            stack = inv.getStackInSlot(9 + i);
            if (!stack.isEmpty())
            {
                filled++;
                if (Slottings.isTwoHanded(stack)) filled++;
            }
        }
        stack = player.getHeldItemMainhand(); //28
        if (!stack.isEmpty())
        {
            filled++;
            if (Slottings.isTwoHanded(stack)) filled++;
        }
        stack = player.getHeldItemOffhand(); //29
        if (!stack.isEmpty())
        {
            filled++;
            if (Slottings.isTwoHanded(stack)) filled++;
        }
        for (ItemStack armor : inv.armorInventory) //33
        {
            if (!armor.isEmpty())
            {
                filled++;
                if (Slottings.isTwoHanded(armor)) filled++;
            }
        }
        for (ItemStack armor : GlobalInventory.getTiamatArmor(player)) //35
        {
            if (!armor.isEmpty())
            {
                filled++;
                if (Slottings.isTwoHanded(armor)) filled++;
            }
        }

        //If all slots were filled with 2-handers (including armor, which won't happen), speed would be 0.  This is probably fine.  Only adjust if there is an issue during gameplay.
        BetterAttributeMod.addMods(entity, new BetterAttributeMod("inventoryWeight", Attributes.MOVE_SPEED.name, 100, 1, -0.5 * filled / 35));
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
        ArrayList<ItemStack> validItems = GlobalInventory.getValidEquippedItems(livingBase);
        if (!validItems.contains(stack) || !Slottings.isTwoHanded(stack) && (Slottings.isTwoHanded(other) || stack.isEmpty())) stack = other;
        if (!validItems.contains(stack)) stack = ItemStack.EMPTY;

        String actionName = null;
        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), "tiamatitems", "generic");
            if (compound != null) actionName = compound.getString((mainhand ? "mainhand" : "offhand") + index);
        }
        if (actionName == null || actionName.equals("")) actionName = index == 0 ? "faerunaction.unarmed.jab" : "faerunaction.unarmed.straight";

        tryUseAction(livingBase, CAction.ALL_ACTIONS.get(actionName), stack, mainhand);
    }

    public static void tryUseAction(EntityLivingBase livingBase, CAction action, ItemStack itemstackUsed, boolean mainhand)
    {
        if (!(action instanceof CFaerunAction)) action.queue(livingBase, "Main");
        else if (canUseAction(livingBase, (CFaerunAction) action))
        {
            ArrayList<CAction> queue = ActionQueue.get(livingBase, "Main").queue;
            if (queue.size() > 0)
            {
                CAction finalAction = queue.get(queue.size() - 1);
                if (finalAction instanceof ComboGracePeriod) finalAction.active = false;
            }

            action.queue(livingBase, "Main");
            ArrayList<CAction> actions = ActionQueue.get(livingBase, "Main").queue;
            CFaerunAction queuedAction = (CFaerunAction) actions.get(actions.size() - 1);
            queuedAction.itemstackUsed = itemstackUsed;
            queuedAction.mainhand = mainhand;
        }
    }

    public static boolean canUseAction(Entity entity, CFaerunAction action)
    {
        if (Attributes.HEALTH.getCurrentAmount(entity) - action.hpCost < 0) return false;
        if (Attributes.MANA.getCurrentAmount(entity) - action.mpCost < 0) return false;
        if (Attributes.STAMINA.getCurrentAmount(entity) - action.staminaCost < 0) return false;
        if (Attributes.COMBO.getCurrentAmount(entity) - action.comboUsage < 0) return false;

        ArrayList<CAction> queue = ActionQueue.get(entity, "Main").queue;
        if (queue.size() == 0) return true;

        CAction currentAction = queue.get(0);
        if (currentAction.getClass() == Cooldown.class) return false;
        if (!(currentAction instanceof CFaerunAction)) return true;

        CAction finalAction = queue.get(queue.size() - 1);
        if (finalAction instanceof ComboGracePeriod) return ((ComboGracePeriod) finalAction).previousAction.canComboTo.contains(action.name);
        return ((CFaerunAction) finalAction).canComboTo.contains(action.name);
    }

    public static void cancelCombo(Entity entity)
    {
        ArrayList<CAction> queue = ActionQueue.get(entity, "Main").queue;

        for (int i = 1; i < queue.size(); i++)
        {
            CAction action = queue.get(i);
            if (action instanceof Cooldown) return;
            if (!(action instanceof CFaerunAction)) continue;

            Attributes.COMBO.setCurrentAmount(entity, Attributes.COMBO.getCurrentAmount(entity) + ((CFaerunAction) action).comboUsage);
            queue.remove(action);
            i--;
        }
    }


    @SubscribeEvent
    public static void entityUpdate(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        boolean serverside = !entity.world.isRemote;
        if (entity.isOnLadder() && !entity.onGround)
        {
            int climbingLegs = 2, climbingArms = 2;
            CBipedAnimation animation = CBipedAnimation.getCurrent(entity);

            for (CPath.CPathData data : animation.rightLeg.getAllData())
            {
                if (data.path != null)
                {
                    climbingLegs--;
                    break;
                }
            }
            for (CPath.CPathData data : animation.leftLeg.getAllData())
            {
                if (data.path != null)
                {
                    climbingLegs--;
                    break;
                }
            }

            if (!entity.getHeldItemMainhand().isEmpty()) climbingArms--;
            else for (CPath.CPathData data : animation.rightArm.getAllData())
            {
                if (data.path != null)
                {
                    climbingArms--;
                    break;
                }
            }
            if (!entity.getHeldItemOffhand().isEmpty()) climbingArms--;
            else for (CPath.CPathData data : animation.leftArm.getAllData())
            {
                if (data.path != null)
                {
                    climbingArms--;
                    break;
                }
            }


            if (serverside)
            {
                int climbingLimbs = climbingLegs + climbingArms;
                if (climbingLimbs > 0) Attributes.STAMINA.setCurrentAmount(entity, Tools.max(0, Attributes.STAMINA.getCurrentAmount(entity) - (4.5 - climbingLimbs) * 0.25));
            }

            if (climbingArms == 0) entity.motionY = -5;
            else if (climbingArms == 1)
            {
                if (climbingLegs == 2)
                {
                    entity.motionY -= 0.08;
                }
                else
                {
                    entity.motionY -= 0.11760000228881837;
                }
            }
            else //2 arms
            {
                if (climbingLegs == 1)
                {
                    entity.motionY -= 0.11760000228881837;
                }
                else if (climbingLegs == 0)
                {
                    entity.motionY -= 0.11760000228881837;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientJump(LivingEvent.LivingJumpEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == Minecraft.getMinecraft().player)
        {
            entity.motionY *= Attributes.MOVE_SPEED.getTotalAmount(entity) / 5.1;

            if (entity.isSprinting())
            {
                float f = entity.rotationYaw * 0.017453292F;
                entity.motionX += (double) (MathHelper.sin(f) * 0.2F);
                entity.motionZ -= (double) (MathHelper.cos(f) * 0.2F);
            }

            Network.WRAPPER.sendToServer(new Network.JumpPacket());
        }
    }

    @SubscribeEvent
    public static void serverJump(LivingEvent.LivingJumpEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        if (!entity.world.isRemote && !(entity instanceof EntityPlayer)) onJump(entity);
    }

    public static void onJump(EntityLivingBase livingBase)
    {
        Attributes.STAMINA.setCurrentAmount(livingBase, Tools.max(0, Attributes.STAMINA.getCurrentAmount(livingBase) - 5));

        LinkedHashMap<String, ResourceLocation> voiceSet = VoiceSets.ALL_VOICE_SETS.get(CharacterTags.getCC(livingBase).getString("Voice"));
        if (voiceSet == null) return;

        ResourceLocation soundRL = voiceSet.get("jump");
        MCTools.playSimpleSoundForAll(soundRL, livingBase, 16, 2, 1, 0.8f + Tools.random(0.4f), SoundCategory.HOSTILE);

        livingBase.motionY *= Attributes.MOVE_SPEED.getTotalAmount(livingBase) / 5.1;

        if (livingBase.isSprinting())
        {
            float f = livingBase.rotationYaw * 0.017453292F;
            livingBase.motionX += (double) (MathHelper.sin(f) * 0.2F);
            livingBase.motionZ -= (double) (MathHelper.cos(f) * 0.2F);
        }
    }
}
