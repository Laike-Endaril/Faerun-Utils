package com.fantasticsource.faerunutils;

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
import net.minecraft.init.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import java.io.File;
import java.io.IOException;

@Mod(modid = FaerunUtils.MODID, name = FaerunUtils.NAME, version = FaerunUtils.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021h,)", acceptableRemoteVersions = "[1.12.2.005," + FaerunUtils.VERSION + "]")
public class FaerunUtils
{
    public static final String MODID = "faerunutils";
    public static final String NAME = "Faerun Utils";
    public static final String VERSION = "1.12.2.005e";

    public static boolean faerun;

    private static double firstX = Double.NaN, firstY = Double.NaN, firstZ = Double.NaN;

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
        if (entity instanceof EntityLivingBase) entity.entityCollisionReduction = 1;
        else if (!event.getWorld().isRemote)
        {
            //Reduce lifespan of diamond swords and chestplates
            if (event.getEntity() instanceof EntityItem)
            {
                EntityItem item = (EntityItem) event.getEntity();

                if (item.getItem().getItem() == Items.DIAMOND_CHESTPLATE || item.getItem().getItem() == Items.DIAMOND_SWORD) item.lifespan = 1800;
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
}
