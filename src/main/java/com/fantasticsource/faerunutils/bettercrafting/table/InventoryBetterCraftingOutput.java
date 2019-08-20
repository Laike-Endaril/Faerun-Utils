package com.fantasticsource.faerunutils.bettercrafting.table;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryBetterCraftingOutput implements IInventory
{
    private final NonNullList<ItemStack> stackResult = NonNullList.withSize(1, ItemStack.EMPTY);

    public int getSizeInventory()
    {
        return 1;
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.stackResult)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    public ItemStack getStackInSlot(int index)
    {
        return this.stackResult.get(0);
    }

    public String getName()
    {
        return "Result";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public ITextComponent getDisplayName()
    {
        return (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName()));
    }

    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndRemove(this.stackResult, 0);
    }

    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.stackResult, 0);
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.stackResult.set(0, stack);
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public void markDirty()
    {
    }

    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        this.stackResult.clear();
    }
}