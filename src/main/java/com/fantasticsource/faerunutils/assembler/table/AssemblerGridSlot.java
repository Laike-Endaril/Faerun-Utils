package com.fantasticsource.faerunutils.assembler.table;

import net.minecraft.inventory.Slot;

public class AssemblerGridSlot extends Slot
{
    public AssemblerGridSlot(InventoryAssemblerInput inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        ((ContainerAssembler) (((InventoryAssemblerInput) inventory).container)).update();
    }
}
