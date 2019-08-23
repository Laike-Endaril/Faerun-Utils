package com.fantasticsource.faerunutils.bettercrafting.recipes;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.bettercrafting.recipe.BetterRecipe;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingInput;
import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingOutput;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class RecipeRepair extends BetterRecipe
{
    private static boolean filter(NBTBase nbt)
    {
        String s = nbt.toString();

        if (s.contains("\"LIFE_STEAL_CHANCE\"")) return true;
        if (s.contains("\"LIFE_STEAL\"")) return true;
        if (s.contains("\"REFLECT\"")) return true;
        if (s.contains("\"RESISTANCE\"")) return true;
        if (s.contains("\"REGEN\"")) return true;
        if (s.contains("\"INCREASED_DAMAGE\"")) return true;
        if (s.contains("\"CRIT_DAMAGE\"")) return true;
        if (s.contains("\"MANA_LEECH_CHANCE\"")) return true;
        if (s.contains("\"PARRY\"")) return true;
        if (s.contains("\"SPIRIT\"")) return true;
        if (s.contains("\"COOLDOWN_REDUCTION\"")) return true;
        if (s.contains("\"CRIT_CHANCE\"")) return true;
        if (s.contains("\"SLOW\"")) return true;
        if (s.contains("\"SPEED\"")) return true;
        if (s.contains("\"DODGE\"")) return true;
        if (s.contains("\"MANA_REDUCTION\"")) return true;
        if (s.contains("\"ROOT\"")) return true;
        if (s.contains("\"BONUS_DAMAGE\"")) return true;
        if (s.contains("\"BLOCK\"")) return true;
        if (s.contains("\"MANA_LEECH\"")) return true;

        return false;
    }

    @Override
    public String translationKey()
    {
        return FaerunUtils.MODID + ":recipe.repair";
    }

    @Override
    public Color color()
    {
        return Color.GREEN;
    }

    @Override
    public boolean matches(InventoryBetterCraftingInput inv)
    {
        boolean found = false;
        for (ItemStack stack : inv.stackList)
        {
            if (stack.isEmpty()) continue;


            if (found) return false; //Only allow one non-empty item in crafting grid
            found = true;


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
                    return false; //Cannot repair commons; can only salvage them instead

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
        return found;
    }

    @Override
    public void preview(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out)
    {
        for (ItemStack stack : in.stackList)
        {
            if (stack.isEmpty()) continue;

            NBTTagCompound compound = stack.serializeNBT().copy();
            NBTTagCompound blueRPGTag = compound.getCompoundTag("ForgeCaps").getCompoundTag("Parent").getCompoundTag("bluerpg:gear_stats");
            NBTTagList statsIntTagList = blueRPGTag.getCompoundTag("stats").getTagList("collection", 10); //Type is NBTTagCompound


            //Remove pseudorandom stat based on display name (and technically number of stats as well)
            int pseudorandom = 0;
            for (char c : stack.getDisplayName().toCharArray()) pseudorandom += c;

            ArrayList<Integer> stats = new ArrayList<>();
            int i = 0;
            for (NBTBase stat : statsIntTagList)
            {
                if (filter(stat)) stats.add(i);
                i++;
            }

            statsIntTagList.removeTag(stats.get(Tools.posMod(pseudorandom, stats.size())));


            //Generate new name and rarity
            String name;
            String rarity = blueRPGTag.getString("rarity");
            switch (rarity)
            {
                case "COMMON":
                    throw new IllegalArgumentException("Commons cannot be repaired!");

                case "UNCOMMON":
                    blueRPGTag.setString("rarity", "COMMON");
                    name = "" + TextFormatting.WHITE + TextFormatting.BOLD + TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
                    break;

                case "RARE":
                    blueRPGTag.setString("rarity", "UNCOMMON");
                    name = "" + TextFormatting.GREEN + TextFormatting.BOLD + TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
                    break;

                case "EPIC":
                    blueRPGTag.setString("rarity", "RARE");
                    name = "" + TextFormatting.AQUA + TextFormatting.BOLD + TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
                    break;

                case "LEGENDARY":
                    blueRPGTag.setString("rarity", "EPIC");
                    name = "" + TextFormatting.LIGHT_PURPLE + TextFormatting.BOLD + TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
                    break;

                case "MYTHIC":
                    blueRPGTag.setString("rarity", "LEGENDARY");
                    name = "" + TextFormatting.YELLOW + TextFormatting.BOLD + TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
                    break;

                case "GODLIKE":
                    blueRPGTag.setString("rarity", "MYTHIC");
                    name = "" + TextFormatting.WHITE + TextFormatting.BOLD + TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
                    break;

                default:
                    throw new IllegalArgumentException("Unknown rarity: " + rarity);
            }


            //Create with new rarity, set name and current durability, and return
            ItemStack result = new ItemStack(compound);
            result.setStackDisplayName(name);
            result.setItemDamage(0);
            out.setInventorySlotContents(0, result);
        }
    }

    @Override
    public ArrayList<ItemStack> craft(InventoryBetterCraftingInput in, InventoryBetterCraftingOutput out)
    {
        for (int i = in.getSizeInventory() - 1; i >= 0; i--)
        {
            in.setInventorySlotContents(i, ItemStack.EMPTY);
        }

        return new ArrayList<>();
    }
}
