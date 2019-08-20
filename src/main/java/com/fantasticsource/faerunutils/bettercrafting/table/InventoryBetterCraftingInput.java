package com.fantasticsource.faerunutils.bettercrafting.table;

import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryBetterCraftingInput implements IInventory
{
    public final NonNullList<ItemStack> stackList;
    public final int inventoryWidth;
    public final int inventoryHeight;
    public final Container container;

    public InventoryBetterCraftingInput(Container eventHandlerIn, int width, int height)
    {
        this.stackList = NonNullList.withSize(width * height, ItemStack.EMPTY);
        this.container = eventHandlerIn;
        this.inventoryWidth = width;
        this.inventoryHeight = height;
    }

    public int getSizeInventory()
    {
        return this.stackList.size();
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.stackList)
        {
            if (!itemstack.isEmpty()) return false;
        }

        return true;
    }

    public ItemStack getStackInSlot(int index)
    {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : (ItemStack) this.stackList.get(index);
    }

    public ItemStack getStackInRowAndColumn(int row, int column)
    {
        return row >= 0 && row < this.inventoryWidth && column >= 0 && column <= this.inventoryHeight ? this.getStackInSlot(row + column * this.inventoryWidth) : ItemStack.EMPTY;
    }

    public String getName()
    {
        return "container.crafting";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public ITextComponent getDisplayName()
    {
        return (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName()));
    }

    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.stackList, index);
    }

    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, index, count);

        if (!itemstack.isEmpty())
        {
            this.container.onCraftMatrixChanged(this);
        }

        return itemstack;
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.stackList.set(index, stack);
        this.container.onCraftMatrixChanged(this);
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
        this.stackList.clear();
    }

    public int getHeight()
    {
        return this.inventoryHeight;
    }

    public int getWidth()
    {
        return this.inventoryWidth;
    }

    public void fillStackedContents(RecipeItemHelper helper)
    {
        for (ItemStack itemstack : this.stackList)
        {
            helper.accountStack(itemstack);
        }
    }
}