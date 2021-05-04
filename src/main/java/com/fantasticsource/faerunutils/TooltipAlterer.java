package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.faerunutils.professions.interactions.InteractionCreatePalette;
import com.fantasticsource.faerunutils.professions.interactions.InteractionInsure;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatactions.action.CAction;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class TooltipAlterer
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void itemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();

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
                    tooltip.add("");
                    if (offhand0 == mainhand0) tooltip.add(TextFormatting.LIGHT_PURPLE + "Mainhand / Offhand Action 1: " + I18n.translateToLocal(mainhand0.name));
                    else tooltip.add(TextFormatting.LIGHT_PURPLE + "Mainhand Action 1: " + I18n.translateToLocal(mainhand0.name));

                    if (mainhand0 instanceof CFaerunAction)
                    {
                        //TODO
                    }
                }

                if (mainhand1 != null)
                {
                    tooltip.add("");
                    if (offhand1 == mainhand1) tooltip.add(TextFormatting.LIGHT_PURPLE + "Mainhand / Offhand Action 2: " + I18n.translateToLocal(mainhand0.name));
                    else tooltip.add(TextFormatting.LIGHT_PURPLE + "Mainhand Action 2: " + I18n.translateToLocal(mainhand0.name));

                    if (mainhand1 instanceof CFaerunAction)
                    {
                        //TODO
                    }
                }

                if (offhand0 != null && offhand0 != mainhand0)
                {
                    tooltip.add("");
                    tooltip.add(TextFormatting.LIGHT_PURPLE + "Offhand Action 1: " + I18n.translateToLocal(offhand0.name));

                    if (offhand0 instanceof CFaerunAction)
                    {
                        //TODO
                    }
                }

                if (offhand1 != null && offhand1 != mainhand1)
                {
                    tooltip.add("");
                    tooltip.add(TextFormatting.LIGHT_PURPLE + "Offhand Action 2: " + I18n.translateToLocal(offhand1.name));

                    if (offhand1 instanceof CFaerunAction)
                    {
                        //TODO
                    }
                }
            }
        }
    }
}
