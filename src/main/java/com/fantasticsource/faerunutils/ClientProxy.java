package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.assembler.table.GUIAssembler;
import com.fantasticsource.faerunutils.bag.GUIBag;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ClientProxy
{
    public static void showAssemblerGUI()
    {
        Minecraft.getMinecraft().displayGuiScreen(new GUIAssembler());
    }

    public static void showBagGUI(String itemType, int size, ItemStack bag)
    {
        Minecraft.getMinecraft().displayGuiScreen(new GUIBag(itemType, size, bag));
    }
}
