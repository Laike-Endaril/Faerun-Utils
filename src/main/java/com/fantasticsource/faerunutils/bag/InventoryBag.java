package com.fantasticsource.faerunutils.bag;

import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;

public class InventoryBag implements IInventory
{
    public final NonNullList<ItemStack> stackList;
    public final ContainerBag container;
    public final String itemType;

    public InventoryBag(ContainerBag containerBag, ArrayList<IPartSlot> partSlots)
    {
        container = containerBag;
        stackList = NonNullList.withSize(partSlots.size(), ItemStack.EMPTY);
        itemType = containerBag.itemType;

        for (int i = 0; i < stackList.size(); i++) stackList.set(i, partSlots.get(i).getPart());
    }

    public int getSizeInventory()
    {
        return stackList.size();
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : stackList)
        {
            if (!itemstack.isEmpty()) return false;
        }

        return true;
    }

    public ItemStack getStackInSlot(int index)
    {
        return index >= getSizeInventory() ? ItemStack.EMPTY : stackList.get(index);
    }

    public String getName()
    {
        return "faerunutils.bag";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public ITextComponent getDisplayName()
    {
        return (hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName()));
    }

    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(stackList, index);
    }

    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(stackList, index, count);
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        stackList.set(index, stack);
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
        return itemType == null || MiscTags.getItemTypeName(stack).equals(itemType);
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
        stackList.clear();
    }
}