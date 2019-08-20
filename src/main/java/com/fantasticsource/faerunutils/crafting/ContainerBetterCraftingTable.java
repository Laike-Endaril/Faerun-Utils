package com.fantasticsource.faerunutils.crafting;

import com.fantasticsource.faerunutils.BlocksAndItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerBetterCraftingTable extends Container
{
    public final World world;
    public final EntityPlayer player;
    public final BlockPos position;
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public InventoryCraftResult craftResult = new InventoryCraftResult();

    public ContainerBetterCraftingTable(InventoryPlayer playerInventory, World worldIn, BlockPos posIn)
    {
        world = worldIn;
        position = posIn;
        player = playerInventory.player;

        addSlotToContainer(new SlotCrafting(playerInventory.player, craftMatrix, craftResult, 0, 124, 35));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                addSlotToContainer(new Slot(craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
    }

    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!world.isRemote)
        {
            clearContainer(playerIn, world, craftMatrix);
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        if (world.getBlockState(position).getBlock() != BlocksAndItems.blockBetterCraftingTable) return false;

        return playerIn.getDistanceSq((double) position.getX() + 0.5D, (double) position.getY() + 0.5D, (double) position.getZ() + 0.5D) <= 64.0D;
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                itemstack1.getItem().onCreated(itemstack1, world, playerIn);

                if (!mergeItemStack(itemstack1, 10, 46, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37)
            {
                if (!mergeItemStack(itemstack1, 37, 46, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!mergeItemStack(itemstack1, 10, 37, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!mergeItemStack(itemstack1, 10, 46, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0)
            {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != craftResult && super.canMergeSlot(stack, slotIn);
    }
}