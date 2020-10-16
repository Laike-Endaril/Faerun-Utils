package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class AssemblerGridSlot extends FilteredSlot
{
    public AssemblerGridSlot(InventoryAssemblerInput inventoryIn, int index, int x, int y, int u, int v, Predicate<ItemStack> predicate)
    {
        super(inventoryIn, index, x, y, u, v, false, 1, predicate);
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        ((ContainerAssembler) (((InventoryAssemblerInput) inventory).container)).update();
    }
}
