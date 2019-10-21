package com.fantasticsource.faerunutils;

import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.faerunutils.bettercrafting.recipe.Recipes;
import com.fantasticsource.faerunutils.bettercrafting.recipes.RecipeRepairObfResult;
import com.fantasticsource.faerunutils.bettercrafting.recipes.RecipeSalvaging;
import com.fantasticsource.faerunutils.bettercrafting.recipes.RecipeSell;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;

import java.io.File;
import java.io.IOException;

import static net.minecraftforge.common.util.Constants.NBT.TAG_STRING;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021h,)", acceptableRemoteVersions = "[1.12.2.005," + FaerunUtils.VERSION + "]")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.005k";

    public static boolean faerun;

    private static double firstX = Double.NaN, firstY = Double.NaN, firstZ = Double.NaN;
    private static Class CNPCClass = null;
    private static final String TAG_NO_LOOT_OR_EXP = MODID + "NoLootOrExp";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        MinecraftForge.EVENT_BUS.register(FaerunUtils.class);
        MinecraftForge.EVENT_BUS.register(DamageBlocker.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);

        GCMessageFixer.init();
        Network.init();
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        faerun = Loader.isModLoaded("bluerpg");


        //Register recipes
        if (faerun)
        {
            Recipes.add(new RecipeRepairObfResult());
            Recipes.add(new RecipeSalvaging());
            Recipes.add(new RecipeSell());
        }


        String[] tokens = Tools.fixedSplit(FaerunUtilsConfig.firstTimeSpawn, ",");
        if (tokens.length == 3)
        {
            try
            {
                firstX = Double.parseDouble(tokens[0]);
                firstY = Double.parseDouble(tokens[1]);
                firstZ = Double.parseDouble(tokens[2]);
            }
            catch (NumberFormatException e)
            {
                firstX = Double.NaN;
                firstY = Double.NaN;
                firstZ = Double.NaN;
            }
        }

        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("customnpcs", "customnpc"));
        if (entry != null) CNPCClass = entry.getEntityClass();
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

    @SubscribeEvent
    public static void entityJoin(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        //Prevent entities from nudging each other
//        if (entity instanceof EntityLivingBase) entity.entityCollisionReduction = 1;

        if (!event.getWorld().isRemote)
        {
            //Reduce lifespan of ethereal items
            if (entity instanceof EntityItem)
            {
                EntityItem item = (EntityItem) entity;
                if (isEthereal(item.getItem())) item.lifespan = 1800;
            }
        }
    }

    @SubscribeEvent
    public static void playerInteractEntity(PlayerInteractEvent.EntityInteractSpecific event)
    {
        EntityPlayer player = event.getEntityPlayer();
        Entity other = event.getTarget();
        if (other instanceof EntityLivingBase && other instanceof IJumpingMount && other.getPassengers().size() == 0)
        {
            //Fix yaw and pitch when mounting an entity
            other.setLocationAndAngles(other.posX, other.posY, other.posZ, player.rotationYawHead, player.rotationPitch);
        }
        else if (player.isRiding())
        {
            //Dismount players on interact
            player.dismountRidingEntity();
        }
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        File file = new File(MCTools.getDataDir(FMLCommonHandler.instance().getMinecraftServerInstance()) + ".." + File.separator + "playerdata");
        boolean doIt = !file.exists();
        if (!doIt)
        {
            file = new File(file.getAbsolutePath() + File.separator + event.player.getPersistentID() + ".dat");
            doIt = !file.exists();
        }

        if (doIt)
        {
            //Very first time this player has logged into the server
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

            //Show "first time join" message
            TextComponentString message = new TextComponentString(TextFormatting.GOLD + event.player.getName() + " just joined the server for the very first time!");
            for (EntityPlayerMP player : server.getPlayerList().getPlayers())
            {
                player.sendMessage(message);
            }

            //Teleport player depending on config
            if (!Double.isNaN(firstX)) server.commandManager.executeCommand(server, "/tp " + event.player.getName() + " " + firstX + " " + firstY + " " + firstZ);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        EntityPlayer player = event.player;
        if (player instanceof EntityPlayerMP && event.fromDim != event.toDim)
        {
            Runnable runnable = () -> MinecraftForge.EVENT_BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player, player.dimension, player.dimension));
            ServerTickTimer.schedule(2, runnable);
        }
    }

    @SubscribeEvent
    public static void fatalDamage(LivingHurtEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (!livingBase.world.isRemote && livingBase.getClass() == CNPCClass && event.getAmount() > livingBase.getMaxHealth())
        {
            DamageSource source = event.getSource();
            Entity killer = source.getTrueSource();
            if (killer == null) killer = source.getImmediateSource();
            if (killer.getClass() == CNPCClass) livingBase.addTag(TAG_NO_LOOT_OR_EXP);
        }
    }

    @SubscribeEvent
    public static void lootDrop(LivingDropsEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase.getTags().contains(TAG_NO_LOOT_OR_EXP))
        {
            event.getDrops().clear();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void expDrop(LivingExperienceDropEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase.getTags().contains(TAG_NO_LOOT_OR_EXP))
        {
            event.setDroppedExperience(0);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event)
    {
        event.getEntityLiving().removeTag(TAG_NO_LOOT_OR_EXP);
    }

    @SubscribeEvent
    public static void livingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase.getClass() == CNPCClass)
        {
            ICustomNpc npc = (ICustomNpc) NpcAPI.Instance().getIEntity(livingBase);
            if (npc != null && livingBase.getDistanceSq(npc.getHomeX(), npc.getHomeY(), npc.getHomeZ()) > 10000 && Threat.getThreat(livingBase) > 0)
            {
                npc.reset();
                Threat.setThreat(livingBase, 0);
            }
        }
    }

    public static boolean isEthereal(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound tagCompound = stack.getTagCompound();
        if (!tagCompound.hasKey("display")) return false;

        tagCompound = tagCompound.getCompoundTag("display");
        if (!tagCompound.hasKey("Lore")) return false;

        NBTTagList list = tagCompound.getTagList("Lore", TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++)
        {
            if (list.get(i).toString().toLowerCase().equals("\"ethereal\"")) return true;
        }
        return false;
    }
}
