package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.assembler.table.GUIAssembler;
import net.minecraft.client.Minecraft;

public class ClientProxy
{
    public static void showAssemblerGUI()
    {
        Minecraft.getMinecraft().displayGuiScreen(new GUIAssembler());
    }
}
