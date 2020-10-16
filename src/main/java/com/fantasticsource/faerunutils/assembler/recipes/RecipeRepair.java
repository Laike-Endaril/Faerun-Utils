package com.fantasticsource.faerunutils.assembler.recipes;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.assembler.recipe.BetterRecipe;
import com.fantasticsource.faerunutils.assembler.table.InventoryAssemblerInput;
import com.fantasticsource.faerunutils.assembler.table.InventoryAssemblerOutput;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class RecipeRepair extends BetterRecipe
{
    protected static String[] removableStatTags = new String[]
            {
                    "LIFE_STEAL_CHANCE",
                    "LIFE_STEAL",
                    "REFLECT",
                    "RESISTANCE",
                    "REGEN",
                    "INCREASED_DAMAGE",
                    "CRIT_DAMAGE",
                    "MANA_LEECH_CHANCE",
                    "PARRY",
                    "SPIRIT",
                    "COOLDOWN_REDUCTION",
                    "CRIT_CHANCE",
                    "SLOW",
                    "SPEED",
                    "DODGE",
                    "MANA_REDUCTION",
                    "ROOT",
                    "BONUS_DAMAGE",
                    "BLOCK",
                    "MANA_LEECH"
            };

    protected static boolean filterTag(NBTBase nbt)
    {
        String s = nbt.toString();
        for (String stat : removableStatTags)
        {
            if (s.contains('"' + stat + '"')) return true;
        }
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
    public boolean matches(InventoryAssemblerInput inv)
    {
        boolean found = false;
        for (ItemStack stack : inv.stackList)
        {
            if (stack.isEmpty()) continue;


            if (found) return false; //Only allow one non-empty item in crafting grid
            found = true;


            NBTTagCompound compound = stack.serializeNBT().copy();
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
    public Pair<ItemStack, ItemStack> prepareToCraft(InventoryAssemblerInput in)
    {
        for (ItemStack stack : in.stackList)
        {
            if (stack.isEmpty()) continue;

            NBTTagCompound compound = stack.serializeNBT().copy();
            NBTTagCompound blueRPGTag = compound.getCompoundTag("ForgeCaps").getCompoundTag("Parent").getCompoundTag("bluerpg:gear_stats");
            NBTTagList statsTagList = blueRPGTag.getCompoundTag("stats").getTagList("collection", 10); //Type is NBTTagCompound


            //Remove pseudorandom stat based on display name (and technically number of stats as well)
            int pseudorandom = 0;
            for (char c : stack.getDisplayName().toCharArray()) pseudorandom += c;

            ArrayList<Integer> stats = new ArrayList<>();
            int i = 0;
            for (NBTBase stat : statsTagList)
            {
                if (filterTag(stat)) stats.add(i);
                i++;
            }

            statsTagList.removeTag(stats.get(Tools.posMod(pseudorandom, stats.size())));


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
                    name = "" + TextFormatting.RED + TextFormatting.BOLD + TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
                    break;

                default:
                    throw new IllegalArgumentException("Unknown rarity: " + rarity);
            }


            //Create with new rarity, set name and current durability, and return
            ItemStack result = new ItemStack(compound);
            result.setStackDisplayName(name);
            result.setItemDamage(0);
            return new Pair<>(result, result);
        }

        return null;
    }

    @Override
    public ArrayList<ItemStack> craft(InventoryAssemblerInput in, InventoryAssemblerOutput out, ItemStack grabbedStack)
    {
        for (int i = in.getSizeInventory() - 1; i >= 0; i--)
        {
            in.setInventorySlotContents(i, ItemStack.EMPTY);
        }

        return new ArrayList<>();
    }
}
