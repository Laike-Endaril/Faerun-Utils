package com.fantasticsource.faerunutils.bettercrafting.recipes;

import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingInput;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.faerunutils.bettercrafting.recipes.Recipes.POWDER;
import static com.fantasticsource.faerunutils.bettercrafting.recipes.Recipes.TOKEN;

public class RecipeSalvaging extends BetterRecipe
{
    @Override
    public boolean matches(InventoryBetterCraftingInput inv)
    {
        boolean found = false;
        for (ItemStack stack : inv.stackList)
        {
            if (stack.isEmpty()) continue;


            found = true;


            String name = stack.getDisplayName();
            if (stack.getItem() == TOKEN.getItem())
            {
                String lvl = name.replace(TOKEN.getDisplayName(), "");
                for (char c : lvl.toCharArray())
                {
                    if (c < '0' || c > '9') return false;
                }
            }
            else if (stack.getItem() == POWDER.getItem())
            {
                String lvl = name.replace(POWDER.getDisplayName(), "");
                for (char c : lvl.toCharArray())
                {
                    if (c < '0' || c > '9') return false;
                }
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
                int lvl = stats.getInteger("ilvl");
                if (lvl <= 0) return false;


                switch (stats.getString("rarity"))
                {
                    case "COMMON":
                    case "UNCOMMON":
                    case "RARE":
                    case "EPIC":
                    case "LEGENDARY":
                    case "MYTHIC":
                    case "GODLIKE":
                        break;

                    default:
                        System.out.println("Unknown rarity: " + stats.getString("rarity"));
                        return false;
                }
            }
        }
        return found;
    }

    @Override
    public void preview(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out)
    {
        int maxLvl = 0, quantity = 0;
        for (ItemStack stack : in.stackList)
        {
            if (stack.isEmpty()) continue;


            String name = stack.getDisplayName();
            int lvl;
            if (stack.getItem() == TOKEN.getItem())
            {
                lvl = Integer.parseInt(name.replace(TOKEN.getDisplayName(), ""));
                if (lvl < maxLvl) continue;

                if (lvl > maxLvl)
                {
                    maxLvl = lvl;
                    quantity = stack.getCount() * 9;
                }
                else quantity += stack.getCount() * 9;
            }
            else if (stack.getItem() == POWDER.getItem())
            {
                lvl = Integer.parseInt(name.replace(POWDER.getDisplayName(), ""));
                if (lvl < maxLvl) continue;

                if (lvl > maxLvl)
                {
                    maxLvl = lvl;
                    quantity = stack.getCount();
                }
                else quantity += stack.getCount();
            }
            else
            {
                NBTTagCompound compound = stack.serializeNBT();
                compound = compound.getCompoundTag("ForgeCaps");
                compound = compound.getCompoundTag("Parent");

                NBTTagCompound stats = compound.getCompoundTag("bluerpg:gear_stats");
                lvl = stats.getInteger("ilvl");
                if (lvl < maxLvl) continue;


                int q = 0;
                switch (stats.getString("rarity"))
                {
                    case "COMMON":
                        q = 1;
                        break;
                    case "UNCOMMON":
                        q = 1;
                        break;
                    case "RARE":
                        q = 3;
                        break;
                    case "EPIC":
                        q = 3;
                        break;
                    case "LEGENDARY":
                        q = 5;
                        break;
                    case "MYTHIC":
                        q = 5;
                        break;
                    case "GODLIKE":
                        q = 10;
                        break;
                }


                if (lvl > maxLvl)
                {
                    maxLvl = lvl;
                    quantity = q;
                }
                else quantity += q;
            }
        }

        if (quantity >= 9)
        {
            ItemStack stack = TOKEN.copy();
            stack.setStackDisplayName(stack.getDisplayName() + maxLvl);
            stack.setCount(quantity / 9);
            out.setInventorySlotContents(0, stack);
        }
        else
        {
            ItemStack stack = POWDER.copy();
            stack.setStackDisplayName(stack.getDisplayName() + maxLvl);
            stack.setCount(quantity);
            out.setInventorySlotContents(0, stack);
        }
    }

    @Override
    public void craft(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out)
    {
    }
}
