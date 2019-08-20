package com.fantasticsource.faerunutils.bettercrafting.recipes;

import com.fantasticsource.faerunutils.bettercrafting.table.ContainerBetterCraftingTable;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingInput;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.Arrays;

import static com.fantasticsource.faerunutils.bettercrafting.recipes.Recipes.POWDER;
import static com.fantasticsource.faerunutils.bettercrafting.recipes.Recipes.TOKEN;

public class RecipeSalvaging extends BetterRecipe
{
    private InventoryBetterCraftingInput craftGrid = null;

    private int[] powderCounts = new int[100];
    private ArrayList<ItemStack> extraResults = new ArrayList<>();
    private ItemStack maxLvlStack = ItemStack.EMPTY;


    @Override
    public boolean matches(InventoryBetterCraftingInput inv)
    {
//        craftGrid = inv;
//
//        Arrays.fill(powderCounts, 0);
//        extraResults.clear();
//        maxLvlStack = ItemStack.EMPTY;
//
//
//        int maxLvl = 0, freeSlots = inv.getSizeInventory();
//        for (int i = inv.getSizeInventory() - 1; i >= 0; i--)
//        {
//            ItemStack stack = inv.getStackInSlot(i);
//            if (stack == ItemStack.EMPTY) continue;
//
//            int lvl, quantity;
//            if (stack.getItem() == TOKEN.getItem())
//            {
//                String name = stack.getDisplayName();
//                try
//                {
//                    lvl = Integer.parseInt(name.substring(name.lastIndexOf(' ') + 1));
//                }
//                catch (NumberFormatException e)
//                {
//                    return false;
//                }
//                quantity = stack.getCount() * 9;
//            }
//            else if (stack.getItem() == POWDER.getItem())
//            {
//                String name = stack.getDisplayName();
//                try
//                {
//                    lvl = Integer.parseInt(name.substring(name.lastIndexOf(' ') + 1));
//                }
//                catch (NumberFormatException e)
//                {
//                    return false;
//                }
//                quantity = stack.getCount();
//            }
//            else
//            {
//                NBTTagCompound compound = stack.serializeNBT();
//                if (!compound.hasKey("ForgeCaps")) return false;
//
//                compound = compound.getCompoundTag("ForgeCaps");
//                if (!compound.hasKey("Parent")) return false;
//
//                compound = compound.getCompoundTag("Parent");
//                if (!compound.hasKey("bluerpg:gear_stats")) return false;
//
//                NBTTagCompound stats = compound.getCompoundTag("bluerpg:gear_stats");
//                lvl = stats.getInteger("ilvl");
//                if (lvl == 0) return false;
//
//
//                switch (stats.getString("rarity"))
//                {
//                    case "COMMON":
//                        quantity = 1;
//                        break;
//
//                    case "UNCOMMON":
//                        quantity = 1;
//                        break;
//
//                    case "RARE":
//                        quantity = 3;
//                        break;
//
//                    case "EPIC":
//                        quantity = 3;
//                        break;
//
//                    case "LEGENDARY":
//                        quantity = 5;
//                        break;
//
//                    case "MYTHIC":
//                        quantity = 5;
//                        break;
//
//                    case "GODLIKE":
//                        quantity = 10;
//                        break;
//
//                    default:
//                        System.out.println("Unknown rarity: " + stats.getString("rarity"));
//                        continue;
//                }
//            }
//
//
//            if (maxLvl < lvl) maxLvl = lvl;
//
//            int before = powderCounts[lvl];
//            powderCounts[lvl] += quantity;
//
//            if (before == 0) freeSlots--;
//        }
//
//
//        if (maxLvl == 0) return false;
//
//
//        ItemStack stack;
//        int powders = powderCounts[maxLvl];
//        if (powders < 9)
//        {
//            maxLvlStack = POWDER.copy();
//            maxLvlStack.setCount(powders);
//            maxLvlStack.setStackDisplayName("Equipment Powder Level " + maxLvl);
//
//            freeSlots++;
//        }
//        else
//        {
//            maxLvlStack = TOKEN.copy();
//            maxLvlStack.setCount(powders / 9);
//            maxLvlStack.setStackDisplayName("Skin Token Level " + maxLvl);
//
//            powders -= maxLvlStack.getCount() * 9;
//            if (powders == 0) freeSlots++;
//            else
//            {
//                stack = POWDER.copy();
//                stack.setCount(powders);
//                stack.setStackDisplayName("Equipment Powder Level " + maxLvl);
//                extraResults.add(stack);
//            }
//        }
//
//        for (int lvl = maxLvl - 1; lvl > 0; lvl--)
//        {
//            powders = powderCounts[lvl];
//            if (powders == 0) continue;
//
//            if (powders % 9 == 0)
//            {
//                stack = TOKEN.copy();
//                stack.setCount(powders / 9);
//                stack.setStackDisplayName("Skin Token Level " + lvl);
//                extraResults.add(stack);
//            }
//            else if (freeSlots == 0)
//            {
//                stack = POWDER.copy();
//                stack.setCount(powders);
//                stack.setStackDisplayName("Equipment Powder Level " + maxLvl);
//                extraResults.add(stack);
//            }
//            else
//            {
//                stack = TOKEN.copy();
//                stack.setCount(powders / 9);
//                stack.setStackDisplayName("Skin Token Level " + lvl);
//                extraResults.add(stack);
//
//                powders -= stack.getCount() * 9;
//                stack = POWDER.copy();
//                stack.setCount(powders);
//                stack.setStackDisplayName("Equipment Powder Level " + maxLvl);
//                extraResults.add(stack);
//
//                freeSlots--;
//            }
//        }
//
        return true;
    }

    @Override
    public void craft(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out, boolean preview)
    {

    }

    public ItemStack getRecipeOutput()
    {
        System.out.println("getRecipeOutput");
        //Called all over the place, especially for recipe scrapers

        if (craftGrid == null) return TOKEN.copy();
        return maxLvlStack.copy();
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryBetterCraftingInput inv)
    {
        System.out.println("getRemainingItems");

        //Only called from CraftingManager.getRemainingItems(), which is only called from SlotCrafting.onTake()

        //Use reflection, because the AT for this is failing on Faerun pack.  Maybe another AT overriding, or ASM, or who knows
        NonNullList<ItemStack> stackList = inv.stackList;

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
            matches(inv);
            connection.sendPacket(new SPacketSetSlot(betterCraftingTable.windowId, 0, maxLvlStack));
        }

        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }
}
