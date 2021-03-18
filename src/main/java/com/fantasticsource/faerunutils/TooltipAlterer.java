package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.professions.interactions.InteractionCreatePalette;
import com.fantasticsource.faerunutils.professions.interactions.InteractionInsure;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
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
        int i = 0;
        while (!"".equals(tooltip.get(i)) && i < tooltip.size()) i++;

        //Add palette block tag if applicable
        if (!InteractionCreatePalette.canMakePaletteFrom(stack))
        {
            tooltip.add(i++, TextFormatting.RED + "Palette creation blocked");
        }

        //Add insurance tag if applicable
        if (InteractionInsure.isFullyInsured(Minecraft.getMinecraft().player.getUniqueID(), stack))
        {
            tooltip.add(i++, TextFormatting.GREEN + "Fully Insured");
        }
    }
}
