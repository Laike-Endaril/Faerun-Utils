package com.fantasticsource.faerunutils.bettercrafting.table;

import net.minecraft.inventory.Slot;

public class BetterCraftingGridSlot extends Slot
{
    public BetterCraftingGridSlot(InventoryBetterCraftingInput inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        ((ContainerBetterCraftingTable) (((InventoryBetterCraftingInput) inventory).container)).update();
    }
}
