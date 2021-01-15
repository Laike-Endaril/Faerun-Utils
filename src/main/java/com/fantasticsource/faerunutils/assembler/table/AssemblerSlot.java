package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class AssemblerSlot extends FilteredSlot
{
    private final ContainerAssembler container;

    public AssemblerSlot(ContainerAssembler container, int index, int x, int y, int u, int v, Predicate<ItemStack> predicate)
    {
        super(container.inventory, index, x, y, ContainerAssembler.TEXTURE, 256, 256, u, v, false, 1, predicate);
        this.container = container;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        container.update(slotNumber);
    }
}
