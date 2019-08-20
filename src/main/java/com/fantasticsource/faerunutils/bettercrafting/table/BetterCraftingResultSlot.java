package com.fantasticsource.faerunutils.bettercrafting.table;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BetterCraftingResultSlot extends Slot
{
    private final ContainerBetterCraftingTable contianer;

    public BetterCraftingResultSlot(ContainerBetterCraftingTable container, int slotIndex, int xPosition, int yPosition)
    {
        super(container.invOutput, slotIndex, xPosition, yPosition);
        this.contianer = container;
    }

    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    public ItemStack onTake(EntityPlayer player, ItemStack outputStack)
    {
        if (contianer.recipe == null)
        {
            System.out.println("Null recipe");
            return ItemStack.EMPTY;
        }

        for (ItemStack stack : contianer.recipe.craft(contianer.invInput, contianer.invOutput))
        {
            stack.onCrafting(player.world, player, stack.getCount());
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, contianer.invInput);
        }

        return outputStack;
    }
}
