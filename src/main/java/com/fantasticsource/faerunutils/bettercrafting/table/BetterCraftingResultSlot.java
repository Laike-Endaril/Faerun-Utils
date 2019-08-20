package com.fantasticsource.faerunutils.bettercrafting.table;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BetterCraftingResultSlot extends Slot
{
    private final InventoryBetterCraftingInput craftMatrix;
    private final EntityPlayer player;
    private int amountCrafted;

    public BetterCraftingResultSlot(EntityPlayer player, InventoryBetterCraftingInput craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
    {
        super(inventoryIn, slotIndex, xPosition, yPosition);
        this.player = player;
        craftMatrix = craftingInventory;
    }

    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    public ItemStack decrStackSize(int amount)
    {
        if (getHasStack()) amountCrafted += Math.min(amount, getStack().getCount());

        return super.decrStackSize(amount);
    }

    protected void onSwapCraft(int p_190900_1_)
    {
        System.out.println("onSwapCraft");
        amountCrafted += p_190900_1_;
    }

    protected void onCrafting(ItemStack stack, int amount)
    {
        System.out.println("onCrafting");
        amountCrafted += amount;
        if (amountCrafted > 0)
        {
            stack.onCrafting(player.world, player, amountCrafted);
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
            amountCrafted = 0;
        }
    }

    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
    {
        System.out.println("onTake");
//        if (amountCrafted > 0)
//        {
//            stack.onCrafting(player.world, player, amountCrafted);
//            FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
//            amountCrafted = 0;
//        }
//
//
//        for (BetterRecipe recipe : BetterRecipe.betterRecipes)
//        {
//            if (recipe.matches(craftMatrix, thePlayer.world))
//            {
//                break;
//            }
//        }
//
//        nonnulllist = NonNullList.withSize(craftMatrix.getSizeInventory(), ItemStack.EMPTY);
//
//        for (int i = 0; i < nonnulllist.size(); ++i)
//        {
//            nonnulllist.set(i, craftMatrix.getStackInSlot(i));
//        }
//
//
//        for (int i = 0; i < nonnulllist.size(); ++i)
//        {
//            ItemStack itemstack = craftMatrix.getStackInSlot(i);
//            ItemStack itemstack1 = nonnulllist.get(i);
//
//            if (!itemstack.isEmpty())
//            {
//                craftMatrix.decrStackSize(i, 1);
//                itemstack = craftMatrix.getStackInSlot(i);
//            }
//
//            if (!itemstack1.isEmpty())
//            {
//                if (itemstack.isEmpty())
//                {
//                    craftMatrix.setInventorySlotContents(i, itemstack1);
//                }
//                else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
//                {
//                    itemstack1.grow(itemstack.getCount());
//                    craftMatrix.setInventorySlotContents(i, itemstack1);
//                }
//                else if (!player.inventory.addItemStackToInventory(itemstack1))
//                {
//                    player.dropItem(itemstack1, false);
//                }
//            }
//        }
//
        return stack;
    }
}