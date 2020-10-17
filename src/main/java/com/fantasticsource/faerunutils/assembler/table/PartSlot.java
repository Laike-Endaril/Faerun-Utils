package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

import static com.fantasticsource.faerunutils.assembler.table.GUIAssembler.TEXTURE;

public class PartSlot extends FilteredSlot
{
    public PartSlot(InventoryAssemblerInput inventoryIn, int index, int x, int y, int u, int v, Predicate<ItemStack> predicate)
    {
        super(inventoryIn, index, x, y, TEXTURE, 256, 256, u, v, false, 1, predicate);
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        ((ContainerAssembler) (((InventoryAssemblerInput) inventory).container)).update();
    }
}
