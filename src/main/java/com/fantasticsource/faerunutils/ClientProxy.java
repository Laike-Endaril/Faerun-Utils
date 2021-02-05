package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.assembler.table.AssemblerGUI;
import com.fantasticsource.faerunutils.bag.BagGUI;
import com.fantasticsource.faerunutils.professions.crafting.CraftingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ClientProxy
{
    public static void showAssemblerGUI()
    {
        Minecraft.getMinecraft().displayGuiScreen(new AssemblerGUI());
    }

    public static void showBagGUI(String itemType, int size, ItemStack bag)
    {
        Minecraft.getMinecraft().displayGuiScreen(new BagGUI(itemType, size, bag));
    }

    public static void showCraftGUI(ItemStack professionItem)
    {
        new CraftingGUI(professionItem);
    }
}
