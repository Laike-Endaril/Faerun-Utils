package com.fantasticsource.faerunutils.recipes;

import com.fantasticsource.faerunutils.FaerunUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

import static com.fantasticsource.faerunutils.recipes.Recipes.POWDER;
import static com.fantasticsource.faerunutils.recipes.Recipes.TOKEN;

public class RecipeSalvaging extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private static final ResourceLocation RL = new ResourceLocation(FaerunUtils.MODID, "recipe_skin_powders");

    private InventoryCrafting craftGrid = null;

    private int[] powderCounts = new int[100];
    private ArrayList<ItemStack> extraResults = new ArrayList<>();
    private ItemStack maxLvlStack = ItemStack.EMPTY;

    public RecipeSalvaging()
    {
        setRegistryName(RL);
    }

    public boolean matches(InventoryCrafting inv, World worldIn)
    {
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
        //Called whenever a crafting slot is changed

        craftGrid = inv;

        return maxLvlStack.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        //Seems to be exclusively for recipe scrapers to display recipes; might play with this later

        return null;
    }

    public ItemStack getRecipeOutput()
    {
        //Called all over the place, especially for recipe scrapers

        if (craftGrid == null) return TOKEN.copy();
        return maxLvlStack.copy();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        //Only called from CraftingManager.getRemainingItems(), which is only called from SlotCrafting.onTake()

        int i = 0, size = inv.getSizeInventory();
        for (ItemStack stack : extraResults)
        {
            ItemStack copy = stack.copy();
            copy.setCount(copy.getCount() + 1);
            inv.stackList.set(i++, copy);
        }
        while (i < size)
        {
            inv.stackList.set(i++, ItemStack.EMPTY);
        }


        inv.setInventorySlotContents(0, inv.getStackInSlot(0));

        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    public boolean isDynamic()
    {
        return true;
    }

    public boolean canFit(int width, int height)
    {
        return width * height >= 1;
    }
}
