package com.fantasticsource.faerunutils.bag;

import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;


public class BagSlot extends FilteredSlot
{
    private final ContainerBag container;

    public BagSlot(ContainerBag container, int index, int x, int y, int u, int v, Predicate<ItemStack> predicate)
    {
        super(container.inventory, index, x, y, ContainerBag.TEXTURE, 256, 256, u, v, false, 64, predicate);
        this.container = container;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        container.update(slotNumber);
    }
}
