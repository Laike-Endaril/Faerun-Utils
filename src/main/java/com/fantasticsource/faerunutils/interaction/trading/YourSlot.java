package com.fantasticsource.faerunutils.interaction.trading;

import com.fantasticsource.mctools.inventory.slot.BetterSlot;
import net.minecraft.entity.player.EntityPlayer;

public class YourSlot extends BetterSlot
{
    private final ContainerTrade container;

    public YourSlot(ContainerTrade container, int index, int x, int y, int u, int v)
    {
        super(container.inventory, index, x, y, ContainerTrade.TEXTURE, 256, 256, u, v);
        this.container = container;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        container.update(slotNumber);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return false;
    }
}
