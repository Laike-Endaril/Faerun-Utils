package com.fantasticsource.faerunutils.bettercrafting.table;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BetterCraftingResultSlot extends Slot
{
    private final ContainerBetterCraftingTable container;

    public BetterCraftingResultSlot(ContainerBetterCraftingTable container, int slotIndex, int xPosition, int yPosition)
    {
        super(container.invOutput, slotIndex, xPosition, yPosition);
        this.container = container;
    }

    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    public ItemStack onTake(EntityPlayer player, ItemStack grabbedStack)
    {
        if (container.getRecipe() == null) return ItemStack.EMPTY;

        for (ItemStack stack : container.getRecipe().craft(container.invInput, container.invOutput, grabbedStack))
        {
            stack.onCrafting(player.world, player, stack.getCount());
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, container.invInput);
        }

        container.update();

        return grabbedStack;
    }
}
