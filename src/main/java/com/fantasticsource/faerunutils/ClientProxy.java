package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.bettercrafting.table.GUIBetterCrafting;
import net.minecraft.client.Minecraft;

public class ClientProxy
{
    public static void showBetterCraftingGUI()
    {
        Minecraft.getMinecraft().displayGuiScreen(new GUIBetterCrafting());
    }
}
