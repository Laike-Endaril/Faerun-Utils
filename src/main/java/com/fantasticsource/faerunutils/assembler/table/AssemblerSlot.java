package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

import static com.fantasticsource.faerunutils.assembler.table.ContainerAssembler.TEXTURE;

public class AssemblerSlot extends FilteredSlot
{
    private final ContainerAssembler container;

    public AssemblerSlot(ContainerAssembler container, int index, int x, int y, int u, int v, Predicate<ItemStack> predicate)
    {
        super(container.inventory, index, x, y, TEXTURE, 256, 256, u, v, false, 1, predicate);
        this.container = container;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        container.update(slotNumber);
    }
}
