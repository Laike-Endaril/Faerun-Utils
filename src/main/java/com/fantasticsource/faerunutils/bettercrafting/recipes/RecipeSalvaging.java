package com.fantasticsource.faerunutils.bettercrafting.recipes;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.bettercrafting.recipe.BetterRecipe;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingInput;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingOutput;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;

import java.util.ArrayList;

public class RecipeSalvaging extends BetterRecipe
{
    private static final ItemStack POWDER = new ItemStack(Items.GOLD_NUGGET);
    private static final ItemStack TOKEN = new ItemStack(Items.SLIME_BALL);

    static
    {
        POWDER.setStackDisplayName("Equipment Powder Level ");
        POWDER.setTagInfo("RepairCost", new NBTTagInt(0));

        TOKEN.setStackDisplayName("Skin Token Level ");
        TOKEN.setTagInfo("RepairCost", new NBTTagInt(0));
    }

    private static int getValue(String rarity)
    {
        switch (rarity)
        {
            case "COMMON":
            case "UNCOMMON":
                return 1;

            case "RARE":
            case "EPIC":
                return 3;

            case "LEGENDARY":
            case "MYTHIC":
                return 5;

            case "GODLIKE":
                return 10;

            default:
                throw new IllegalArgumentException("Unknown rarity: " + rarity);
        }
    }

    @Override
    public String translationKey()
    {
        return FaerunUtils.MODID + ":recipe.salvage";
    }

    @Override
    public Color color()
    {
        return Color.RED;
    }

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
            else if (stack.getItem().getRegistryName().toString().equals("armourers_workshop:item.skin"))
            {
                String[] tokens = Tools.fixedSplit(name, " ");
                if (!tokens[0].equals("[Skin]")) return false;

                if (!tokens[1].equals("Mod"))
                {
                    if (tokens[tokens.length - 1].equals("R") || tokens[tokens.length - 1].equals("L")) tokens[tokens.length - 1] = tokens[tokens.length - 2]; //Shoulders have L or R after number
                    try
                    {
                        Integer.parseInt(tokens[tokens.length - 1]);
                    }
                    catch (NumberFormatException e)
                    {
                        return false;
                    }
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
            int q;
            if (stack.getItem() == TOKEN.getItem())
            {
                lvl = Integer.parseInt(name.replace(TOKEN.getDisplayName(), ""));
                q = stack.getCount() * 9;
            }
            else if (stack.getItem() == POWDER.getItem())
            {
                lvl = Integer.parseInt(name.replace(POWDER.getDisplayName(), ""));
                q = stack.getCount();
            }
            else if (stack.getItem().getRegistryName().toString().equals("armourers_workshop:item.skin"))
            {
                String[] tokens = Tools.fixedSplit(name, " ");

                lvl = 5; //"Mod" skins are level 5
                q = 2;

                if (!tokens[1].equals("Mod"))
                {
                    if (tokens[tokens.length - 1].equals("R") || tokens[tokens.length - 1].equals("L"))
                    {
                        //Shoulders have L or R after number
                        q = 1;
                        tokens[tokens.length - 1] = tokens[tokens.length - 2];
                    }
                    lvl = Tools.max(1, Integer.parseInt(tokens[tokens.length - 1]) - 7);
                }
            }
            else
            {
                NBTTagCompound statsTag = stack.serializeNBT().getCompoundTag("ForgeCaps").getCompoundTag("Parent").getCompoundTag("bluerpg:gear_stats");

                lvl = statsTag.getInteger("ilvl");
                q = getValue(statsTag.getString("rarity"));
            }


            if (lvl < maxLvl) continue;

            if (lvl > maxLvl)
            {
                maxLvl = lvl;
                quantity = q;
            }
            else quantity += q;
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
    public ArrayList<ItemStack> craft(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out, ItemStack grabbedStack)
    {
        int maxLvl = 0;
        int[] quantities = new int[100];
        int freeSlots = in.getSizeInventory();
        for (ItemStack stack : in.stackList)
        {
            if (stack.isEmpty()) continue;


            String name = stack.getDisplayName();
            int lvl;
            if (stack.getItem() == TOKEN.getItem())
            {
                lvl = Integer.parseInt(name.replace(TOKEN.getDisplayName(), ""));

                if (quantities[lvl] == 0) freeSlots--;
                quantities[lvl] += stack.getCount() * 9;
            }
            else if (stack.getItem() == POWDER.getItem())
            {
                lvl = Integer.parseInt(name.replace(POWDER.getDisplayName(), ""));

                if (quantities[lvl] == 0) freeSlots--;
                quantities[lvl] += stack.getCount();
            }
            else if (stack.getItem().getRegistryName().toString().equals("armourers_workshop:item.skin"))
            {
                String[] tokens = Tools.fixedSplit(name, " ");

                int q = 2;
                lvl = 5; //"Mod" skins are level 5
                if (!tokens[1].equals("Mod"))
                {
                    if (tokens[tokens.length - 1].equals("R") || tokens[tokens.length - 1].equals("L"))
                    {
                        //Shoulders have L or R after number
                        q = 1;
                        tokens[tokens.length - 1] = tokens[tokens.length - 2];
                    }
                    lvl = Tools.max(1, Integer.parseInt(tokens[tokens.length - 1]) - 7);
                }

                if (quantities[lvl] == 0) freeSlots--;
                quantities[lvl] += q;
            }
            else
            {
                NBTTagCompound statsTag = stack.serializeNBT().getCompoundTag("ForgeCaps").getCompoundTag("Parent").getCompoundTag("bluerpg:gear_stats");
                lvl = statsTag.getInteger("ilvl");

                if (quantities[lvl] == 0) freeSlots--;
                quantities[lvl] += getValue(statsTag.getString("rarity"));
            }

            if (lvl > maxLvl) maxLvl = lvl;
        }


        ItemStack stack;
        ArrayList<ItemStack> crafted = new ArrayList<>();
        int inputIndex = 0;

        int quantity = quantities[maxLvl];
        if (quantity <= 9 || quantity % 9 == 0)
        {
            //Only taking the one slot we already reserved
            //Which is already in output in this case, so add to free slot count if we aren't splitting the max level stack
            freeSlots++;

            if (quantity % 9 == 0)
            {
                stack = TOKEN.copy();
                stack.setCount(quantity / 9);
            }
            else
            {
                stack = POWDER.copy();
                stack.setCount(quantity);
            }

            stack.setStackDisplayName(stack.getDisplayName() + maxLvl);
            crafted.add(stack);
        }
        else
        {
            //Taking one input slot in addition to output slot
            stack = TOKEN.copy();
            stack.setStackDisplayName(stack.getDisplayName() + maxLvl);
            stack.setCount(quantity / 9);
            crafted.add(stack);

            quantity -= stack.getCount() * 9;
            stack = POWDER.copy();
            stack.setStackDisplayName(stack.getDisplayName() + maxLvl);
            stack.setCount(quantity);
            in.setInventorySlotContents(inputIndex++, stack);
            crafted.add(stack);
        }


        for (int lvl = maxLvl - 1; lvl >= 0; lvl--)
        {
            quantity = quantities[lvl];
            if (quantity <= 0) continue;

            if (freeSlots == 0 || quantity <= 9 || quantity % 9 == 0)
            {
                //Only taking the one slot we already reserved
                if (quantity % 9 == 0)
                {
                    stack = TOKEN.copy();
                    stack.setCount(quantity / 9);
                }
                else
                {
                    stack = POWDER.copy();
                    stack.setCount(quantity);
                }

                stack.setStackDisplayName(stack.getDisplayName() + lvl);
                in.setInventorySlotContents(inputIndex++, stack);
                crafted.add(stack);
            }
            else
            {
                //Taking one additional free slot
                freeSlots--;

                stack = TOKEN.copy();
                stack.setStackDisplayName(stack.getDisplayName() + lvl);
                stack.setCount(quantity / 9);
                in.setInventorySlotContents(inputIndex++, stack);
                crafted.add(stack);

                quantity -= stack.getCount() * 9;
                stack = POWDER.copy();
                stack.setStackDisplayName(stack.getDisplayName() + lvl);
                stack.setCount(quantity);
                in.setInventorySlotContents(inputIndex++, stack);
                crafted.add(stack);
            }
        }

        while (inputIndex < in.getSizeInventory())
        {
            in.setInventorySlotContents(inputIndex++, ItemStack.EMPTY);
        }

        return crafted;
    }
}
