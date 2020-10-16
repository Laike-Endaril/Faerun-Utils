package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.function.Predicate;

public class AssemblerResultSlot extends FilteredSlot
{
    private final ContainerAssembler container;

    public AssemblerResultSlot(ContainerAssembler container, int slotIndex, int x, int y, int u, int v, Predicate<ItemStack> predicate)
    {
        super(container.invOutput, slotIndex, x, y, u, v, false, 1, predicate);
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
