package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.faerunutils.professions.interactions.InteractionCreatePalette;
import com.fantasticsource.faerunutils.professions.interactions.InteractionInsure;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class TooltipAlterer
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void itemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();

        //Alterations

        for (int i = 0; i < tooltip.size(); i++)
        {
            String line = tooltip.get(i);

            String oldValueString = line.replaceAll(".*" + TextFormatting.YELLOW + "Value: " + "([0-9]+).*", "$1");
            if (!oldValueString.equals("") && oldValueString.replaceAll("[0-9]", "").equals(""))
            {
                int c = Integer.parseInt(oldValueString);

                int g = c / 10000;
                c -= g * 10000;

                int s = c / 100;
                c -= s * 100;

                String newValueString = (g > 0 ? TextFormatting.GOLD + "" + g + "g " : "") + (s > 0 ? TextFormatting.DARK_GRAY + "" + s + "s " : "") + (c > 0 || (s == 0 && g == 0) ? TextFormatting.RED + "" + c + "c" : "");

                tooltip.set(i, line.replaceAll(TextFormatting.YELLOW + "Value: " + oldValueString, "Value: " + newValueString));
            }
        }


        //Additions

        //Find first empty line
        int i = 0;
        while (i < tooltip.size() && !"".equals(tooltip.get(i))) i++;

        //Add palette block tag if applicable
        if (!InteractionCreatePalette.canMakePaletteFrom(stack))
        {
            tooltip.add(i++, TextFormatting.RED + "Palette creation blocked");
        }

        //Add insurance tag if applicable
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && InteractionInsure.isFullyInsured(Minecraft.getMinecraft().player.getUniqueID(), stack))
        {
            tooltip.add(i++, TextFormatting.GREEN + "Fully Insured");
        }


        //Above is prepended, below is appended


        //Add skill tooltips, if applicable
        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), "tiamatitems", "generic");
            if (compound != null)
            {
                CAction mainhand0 = CAction.ALL_ACTIONS.get(compound.getString("mainhand0"));
                CAction mainhand1 = CAction.ALL_ACTIONS.get(compound.getString("mainhand1"));
                CAction offhand0 = CAction.ALL_ACTIONS.get(compound.getString("offhand0"));
                CAction offhand1 = CAction.ALL_ACTIONS.get(compound.getString("offhand1"));

                if (mainhand0 != null)
                {
                    if (Keyboard.isKeyDown(Keyboard.KEY_1)) tooltip.clear();
                    else tooltip.add("");

                    if (offhand0 == mainhand0) tooltip.add(TextFormatting.LIGHT_PURPLE + "Action 1: " + I18n.translateToLocal(mainhand0.name));
                    else tooltip.add(TextFormatting.LIGHT_PURPLE + "Mainhand Action 1: " + I18n.translateToLocal(mainhand0.name));

                    if (mainhand0 instanceof CFaerunAction)
                    {
                        if (Keyboard.isKeyDown(Keyboard.KEY_1))
                        {
                            addFaerunActionDetailTooltips(tooltip, (CFaerunAction) mainhand0);
                            return;
                        }
                        else tooltip.add(TextFormatting.BLUE + "Hold 1 to see skill stats");
                    }
                }

                if (mainhand1 != null)
                {
                    if (Keyboard.isKeyDown(Keyboard.KEY_2)) tooltip.clear();
                    else tooltip.add("");

                    if (offhand1 == mainhand1) tooltip.add(TextFormatting.LIGHT_PURPLE + "Action 2: " + I18n.translateToLocal(mainhand1.name));
                    else tooltip.add(TextFormatting.LIGHT_PURPLE + "Mainhand Action 2: " + I18n.translateToLocal(mainhand1.name));

                    if (mainhand1 instanceof CFaerunAction)
                    {
                        if (Keyboard.isKeyDown(Keyboard.KEY_2))
                        {
                            addFaerunActionDetailTooltips(tooltip, (CFaerunAction) mainhand1);
                            return;
                        }
                        else tooltip.add(TextFormatting.BLUE + "Hold 2 to see skill stats");
                    }
                }

                if (offhand0 != null && offhand0 != mainhand0)
                {
                    if (Keyboard.isKeyDown(Keyboard.KEY_3)) tooltip.clear();
                    else tooltip.add("");

                    tooltip.add(TextFormatting.LIGHT_PURPLE + "Offhand Action 1: " + I18n.translateToLocal(offhand0.name));

                    if (offhand0 instanceof CFaerunAction)
                    {
                        if (Keyboard.isKeyDown(Keyboard.KEY_3))
                        {
                            addFaerunActionDetailTooltips(tooltip, (CFaerunAction) offhand0);
                            return;
                        }
                        else tooltip.add(TextFormatting.BLUE + "Hold 3 to see skill stats");
                    }
                }

                if (offhand1 != null && offhand1 != mainhand1)
                {
                    if (Keyboard.isKeyDown(Keyboard.KEY_4)) tooltip.clear();
                    else tooltip.add("");

                    tooltip.add(TextFormatting.LIGHT_PURPLE + "Offhand Action 2: " + I18n.translateToLocal(offhand1.name));

                    if (offhand1 instanceof CFaerunAction)
                    {
                        if (Keyboard.isKeyDown(Keyboard.KEY_4))
                        {
                            addFaerunActionDetailTooltips(tooltip, (CFaerunAction) offhand1);
                            return;
                        }
                        else tooltip.add(TextFormatting.BLUE + "Hold 4 to see skill stats");
                    }
                }
            }
        }
    }

    protected static void addFaerunActionDetailTooltips(List<String> tooltip, CFaerunAction action)
    {
        tooltip.add("Use Time: " + Tools.formatNicely(action.useTime) + "s");
        tooltip.add("Combo Usage: " + Tools.formatNicely(action.comboUsage));
        if (action.hpCost > 0) tooltip.add("HP Cost: " + Tools.formatNicely(action.hpCost));
        if (action.mpCost > 0) tooltip.add("MP Cost: " + Tools.formatNicely(action.mpCost));
        if (action.staminaCost > 0) tooltip.add("Stamina Cost: " + Tools.formatNicely(action.staminaCost));

        double minMeleeRange = 0, maxMeleeRange = 0, meleeAngle = 0, meleeTargets = 0;
        ArrayList<BetterAttributeMod> mods = new ArrayList<>(action.attributeMods);
        for (BetterAttributeMod mod : action.attributeMods)
        {
            if (mod.operation == 0)
            {
                if (mod.betterAttributeName.equals(Attributes.MAX_MELEE_RANGE.name))
                {
                    maxMeleeRange = mod.amount;
                    mods.remove(mod);
                }
                else if (mod.betterAttributeName.equals(Attributes.MIN_MELEE_RANGE.name))
                {
                    minMeleeRange = mod.amount;
                    mods.remove(mod);
                }
                else if (mod.betterAttributeName.equals(Attributes.MAX_MELEE_ANGLE.name))
                {
                    meleeAngle = mod.amount;
                    mods.remove(mod);
                }
                else if (mod.betterAttributeName.equals(Attributes.MAX_MELEE_TARGETS.name))
                {
                    meleeTargets = mod.amount;
                    mods.remove(mod);
                }
            }
            else if (mod.operation == 2)
            {
                if (mod.amount <= 0) mods.remove(mod);
            }
        }

        if (maxMeleeRange > 0)
        {
            //Melee attack
            tooltip.add("Range: " + (minMeleeRange > 0 ? Tools.formatNicely(minMeleeRange) + " to " : "") + Tools.formatNicely(maxMeleeRange));
            tooltip.add("Angle Tolerance: " + Tools.formatNicely(meleeAngle * 2));
            tooltip.add("Targets Hit: " + Tools.formatNicely(meleeTargets));
        }

        for (BetterAttributeMod mod : mods) tooltip.add(mod.toString());
    }
}
