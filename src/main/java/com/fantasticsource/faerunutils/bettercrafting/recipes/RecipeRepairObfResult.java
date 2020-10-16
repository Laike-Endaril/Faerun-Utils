package com.fantasticsource.faerunutils.bettercrafting.recipes;

import com.fantasticsource.faerunutils.bettercrafting.table.InventoryBetterCraftingInput;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class RecipeRepairObfResult extends RecipeRepair
{
    protected static String[] removableStatTooltips = new String[]
            {
                    "HP Steal Chance",
                    "Life Steal",
                    "Reflect",
                    "Resistance",
                    "HP Regen",
                    "Increased Damage",
                    "Crit Damage",
                    "MP Leech Chance",
                    "Parry",
                    "Spirit",
                    "Cooldown Reduction",
                    "Crit Chance",
                    "Slow",
                    "Speed",
                    "Dodge",
                    "Mana Reduction",
                    "Root",
                    "Bonus Damage",
                    "Block",
                    "Mana Leech"
            };


    static
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(RecipeRepairObfResult.class);
        }
    }


    protected static String filterTooltip(String line)
    {
        for (String stat : removableStatTooltips)
        {
            if (line.contains(stat)) return stat;
        }
        return null;
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null || !compound.hasKey("faerunutilsHidden")) return;

        List<String> lines = event.getToolTip();
        int first = -1;
        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            String stat = filterTooltip(line);
            if (stat != null)
            {
                if (first == -1) first = i;
                lines.set(i, line.replaceAll(stat, TextFormatting.GOLD + stat));
            }
        }

        if (first != -1) lines.add(first, TextFormatting.RED + "WARNING: One of these stats will be removed randomly!");
    }


    @Override
    public Pair<ItemStack, ItemStack> prepareToCraft(InventoryBetterCraftingInput in)
    {
        for (ItemStack stack : in.stackList)
        {
            if (stack.isEmpty()) continue;


            //Generate actual result
            NBTTagCompound compound = stack.serializeNBT().copy();
            NBTTagCompound blueRPGTag = compound.getCompoundTag("ForgeCaps").getCompoundTag("Parent").getCompoundTag("bluerpg:gear_stats");
            NBTTagList statsTagList = blueRPGTag.getCompoundTag("stats").getTagList("collection", 10); //Type is NBTTagCompound


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

            NBTTagCompound previewCompound = compound.copy();


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


            //Create with new rarity, set name and current durability...
            ItemStack result = new ItemStack(compound);
            result.setStackDisplayName(name);
            result.setItemDamage(0);


            //...


            //Generate preview result
            ItemStack preview = new ItemStack(previewCompound);
            preview.setStackDisplayName(name);
            if (!preview.hasTagCompound()) preview.setTagCompound(new NBTTagCompound());
            preview.getTagCompound().setTag("faerunutilsHidden", new NBTTagByte((byte) 0));


            //Return
            return new Pair<>(result, preview);
        }

        return null;
    }
}
