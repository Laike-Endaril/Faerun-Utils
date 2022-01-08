package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.bag.BagGUI;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tiamatitems.assembly.AssemblerGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ClientProxy
{
    public static void showAssemblerGUI()
    {
        GUIScreen.showUnstacked(new AssemblerGUI());
    }

    public static void showBagGUI(String itemType, int size, ItemStack bag)
    {
        Minecraft.getMinecraft().displayGuiScreen(new BagGUI(itemType, size, bag));
    }
}
