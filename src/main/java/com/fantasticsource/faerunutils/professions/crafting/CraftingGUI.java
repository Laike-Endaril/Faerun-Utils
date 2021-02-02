package com.fantasticsource.faerunutils.professions.crafting;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import net.minecraft.item.ItemStack;

public class CraftingGUI extends GUIScreen
{
    public final String profession;

    public CraftingGUI(ItemStack professionItem)
    {
        profession = professionItem.getDisplayName();

        root.add(new GUIItemStack(this, 16, 16, professionItem));
    }

    @Override
    public String title()
    {
        return "Crafting (" + profession + ")";
    }
}
