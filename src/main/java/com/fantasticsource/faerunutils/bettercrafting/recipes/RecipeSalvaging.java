package com.fantasticsource.faerunutils.bettercrafting.recipes;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.bettercrafting.table.ContainerBetterCraftingTable;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static com.fantasticsource.faerunutils.bettercrafting.recipes.Recipes.POWDER;
import static com.fantasticsource.faerunutils.bettercrafting.recipes.Recipes.TOKEN;

public class RecipeSalvaging extends BetterRecipe
{
    private static final ResourceLocation RL = new ResourceLocation(FaerunUtils.MODID, "recipe_skin_powders");
    private static Field stackListField;

    static
    {
        try
        {
            stackListField = ReflectionTool.getField(InventoryCrafting.class, "field_70466_a", "stackList");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            MCTools.crash(e, 2001, false);
        }
    }

    private InventoryCrafting craftGrid = null;

    private int[] powderCounts = new int[100];
    private ArrayList<ItemStack> extraResults = new ArrayList<>();
    private ItemStack maxLvlStack = ItemStack.EMPTY;


    public RecipeSalvaging()
    {
        setRegistryName(RL);
        MinecraftForge.EVENT_BUS.register(this);
    }


    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        System.out.println("matches");
        craftGrid = inv;

        Arrays.fill(powderCounts, 0);
        extraResults.clear();
        maxLvlStack = ItemStack.EMPTY;


        int maxLvl = 0, freeSlots = inv.getSizeInventory();
        for (int i = inv.getSizeInventory() - 1; i >= 0; i--)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;

            int lvl, quantity;
            if (stack.getItem() == TOKEN.getItem())
            {
                String name = stack.getDisplayName();
                try
                {
                    lvl = Integer.parseInt(name.substring(name.lastIndexOf(' ') + 1));
                }
                catch (NumberFormatException e)
                {
                    return false;
                }
                quantity = stack.getCount() * 9;
            }
            else if (stack.getItem() == POWDER.getItem())
            {
                String name = stack.getDisplayName();
                try
                {
                    lvl = Integer.parseInt(name.substring(name.lastIndexOf(' ') + 1));
                }
                catch (NumberFormatException e)
                {
                    return false;
                }
                quantity = stack.getCount();
            }
            else
            {
                NBTTagCompound compound = stack.serializeNBT();
                if (!compound.hasKey("ForgeCaps")) return false;

                compound = compound.getCompoundTag("ForgeCaps");
                if (!compound.hasKey("Parent")) return false;

                compound = compound.getCompoundTag("Parent");
                if (!compound.hasKey("bluerpg:gear_stats")) return false;

                NBTTagCompound stats = compound.getCompoundTag("bluerpg:gear_stats");
                lvl = stats.getInteger("ilvl");
                if (lvl == 0) return false;


                switch (stats.getString("rarity"))
                {
                    case "COMMON":
                        quantity = 1;
                        break;

                    case "UNCOMMON":
                        quantity = 1;
                        break;

                    case "RARE":
                        quantity = 3;
                        break;

                    case "EPIC":
                        quantity = 3;
                        break;

                    case "LEGENDARY":
                        quantity = 5;
                        break;

                    case "MYTHIC":
                        quantity = 5;
                        break;

                    case "GODLIKE":
                        quantity = 10;
                        break;

                    default:
                        System.out.println("Unknown rarity: " + stats.getString("rarity"));
                        continue;
                }
            }


            if (maxLvl < lvl) maxLvl = lvl;

            int before = powderCounts[lvl];
            powderCounts[lvl] += quantity;

            if (before == 0) freeSlots--;
        }


        if (maxLvl == 0) return false;


        ItemStack stack;
        int powders = powderCounts[maxLvl];
        if (powders < 9)
        {
            maxLvlStack = POWDER.copy();
            maxLvlStack.setCount(powders);
            maxLvlStack.setStackDisplayName("Equipment Powder Level " + maxLvl);

            freeSlots++;
        }
        else
        {
            maxLvlStack = TOKEN.copy();
            maxLvlStack.setCount(powders / 9);
            maxLvlStack.setStackDisplayName("Skin Token Level " + maxLvl);

            powders -= maxLvlStack.getCount() * 9;
            if (powders == 0) freeSlots++;
            else
            {
                stack = POWDER.copy();
                stack.setCount(powders);
                stack.setStackDisplayName("Equipment Powder Level " + maxLvl);
                extraResults.add(stack);
            }
        }

        for (int lvl = maxLvl - 1; lvl > 0; lvl--)
        {
            powders = powderCounts[lvl];
            if (powders == 0) continue;

            if (powders % 9 == 0)
            {
                stack = TOKEN.copy();
                stack.setCount(powders / 9);
                stack.setStackDisplayName("Skin Token Level " + lvl);
                extraResults.add(stack);
            }
            else if (freeSlots == 0)
            {
                stack = POWDER.copy();
                stack.setCount(powders);
                stack.setStackDisplayName("Equipment Powder Level " + maxLvl);
                extraResults.add(stack);
            }
            else
            {
                stack = TOKEN.copy();
                stack.setCount(powders / 9);
                stack.setStackDisplayName("Skin Token Level " + lvl);
                extraResults.add(stack);

                powders -= stack.getCount() * 9;
                stack = POWDER.copy();
                stack.setCount(powders);
                stack.setStackDisplayName("Equipment Powder Level " + maxLvl);
                extraResults.add(stack);

                freeSlots--;
            }
        }

        return true;
    }

    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        System.out.println("getCraftingResult");
        //Called whenever a crafting slot is changed

        craftGrid = inv;

        return maxLvlStack.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        System.out.println("getIngredients");
        //Seems to be exclusively for recipe scrapers to display recipes; might play with this later

        return NonNullList.create();
    }

    public ItemStack getRecipeOutput()
    {
        System.out.println("getRecipeOutput");
        //Called all over the place, especially for recipe scrapers

        if (craftGrid == null) return TOKEN.copy();
        return maxLvlStack.copy();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        System.out.println("getRemainingItems");

        //Only called from CraftingManager.getRemainingItems(), which is only called from SlotCrafting.onTake()

        //Use reflection, because the AT for this is failing on Faerun pack.  Maybe another AT overriding, or ASM, or who knows
        NonNullList<ItemStack> stackList = null;
        try
        {
            stackList = (NonNullList<ItemStack>) stackListField.get(inv);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 2002, false);
        }

        //Hack past MC's default handling for this by setting inv slots directly and by accounting for its auto-shrink "feature" by adding 1 to each stack count
        int i = 0, size = inv.getSizeInventory();
        for (ItemStack stack : extraResults)
        {
            ItemStack copy = stack.copy();
            copy.setCount(copy.getCount() + 1);

            stackList.set(i++, ItemStack.EMPTY);
        }
        while (i < size)
        {
            stackList.set(i++, ItemStack.EMPTY);
        }


        //Manually sync slots to client, because MC sucks
        EntityPlayer player = ForgeHooks.getCraftingPlayer();
        if (player instanceof EntityPlayerMP)
        {
            ContainerBetterCraftingTable betterCraftingTable = (ContainerBetterCraftingTable) player.openContainer;
            NetHandlerPlayServer connection = ((EntityPlayerMP) player).connection;

            //The crafting grid contents *after* this crafting is complete (because we just crafted it)
            for (int slot = inv.getSizeInventory() - 1; slot >= 0; slot--)
            {
                ItemStack clientStack = inv.getStackInSlot(slot).copy();
                clientStack.shrink(1);
                connection.sendPacket(new SPacketSetSlot(betterCraftingTable.windowId, slot + 1, clientStack));
            }

            //Calculate and send the maxLvlStack for *after* this crafting is complete (because we just crafted the current one and it's now the client's "held item")
            matches(inv, player.world);
            connection.sendPacket(new SPacketSetSlot(betterCraftingTable.windowId, 0, maxLvlStack));
        }

        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    public boolean isDynamic()
    {
        System.out.println("isDynamic");
        return true;
    }

    public boolean canFit(int width, int height)
    {
        System.out.println("canFit");
        return width * height >= 1;
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (craftGrid == null)
            {
            }
            else
            {
            }
        }
    }
}
