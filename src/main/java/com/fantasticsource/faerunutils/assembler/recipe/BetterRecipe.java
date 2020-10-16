package com.fantasticsource.faerunutils.assembler.recipe;

import com.fantasticsource.faerunutils.assembler.table.InventoryAssemblerInput;
import com.fantasticsource.faerunutils.assembler.table.InventoryAssemblerOutput;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public abstract class BetterRecipe
{
    /**
     * This will display in the GUI when the recipe is selected
     */
    abstract public String translationKey();

    /**
     * This is the color of the name when it is displayed in the GUI when the recipe is selected
     */
    abstract public Color color();

    /**
     * Unlike the vanilla version, this is *only* for checking whether a recipe matches, not for caching.  Do not save any data from within this method!
     */
    abstract public boolean matches(InventoryAssemblerInput inv);

    /**
     * @return The resulting ItemStack, and the previewed ItemStack result, in that order
     */
    abstract public Pair<ItemStack, ItemStack> prepareToCraft(InventoryAssemblerInput in);

    /**
     * This should only ever be called when the item in the output is removed
     */
    abstract public ArrayList<ItemStack> craft(InventoryAssemblerInput in, InventoryAssemblerOutput out, ItemStack grabbedStack);
}
