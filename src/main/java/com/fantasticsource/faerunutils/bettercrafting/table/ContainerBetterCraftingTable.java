package com.fantasticsource.faerunutils.bettercrafting.table;

import com.fantasticsource.faerunutils.BlocksAndItems;
import com.fantasticsource.faerunutils.bettercrafting.recipes.BetterRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ContainerBetterCraftingTable extends Container
{
    public final EntityPlayer player;
    public final World world;
    public final BlockPos position;

    public InventoryBetterCraftingInput invInput = new InventoryBetterCraftingInput(this, 3, 3);
    public InventoryBetterCraftingOutput invOutput = new InventoryBetterCraftingOutput();
    public BetterRecipe recipe = null;

    private ArrayList<ItemStack> previousItems = new ArrayList<>();


    public ContainerBetterCraftingTable(EntityPlayer player, World world, BlockPos position)
    {
        this.player = player;
        this.world = world;
        this.position = position;

        addSlotToContainer(new BetterCraftingResultSlot(player, invInput, invOutput, 0, 124, 35));

        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 3; ++x)
            {
                addSlotToContainer(new BetterCraftingGridSlot(invInput, x + y * 3, 30 + x * 18, 17 + y * 18));
            }
        }

        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for (int x = 0; x < 9; ++x)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142));
        }

        for (int i = invInput.getSizeInventory(); i >= 0; i--)
        {
            previousItems.add(ItemStack.EMPTY);
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory ignored)
    {
    }

    @Override
    public void onContainerClosed(EntityPlayer ignored)
    {
        super.onContainerClosed(player);

        if (!player.world.isRemote)
        {
            clearContainer(player, player.world, invInput);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer ignored)
    {
        if (player.world != world) return false;

        if (player.world.getBlockState(position).getBlock() != BlocksAndItems.blockBetterCraftingTable) return false;

        return player.getDistanceSq((double) position.getX() + 0.5, (double) position.getY() + 0.5, (double) position.getZ() + 0.5) <= 64;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer ignored, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        BetterCraftingGridSlot slot = (BetterCraftingGridSlot) inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                itemstack1.getItem().onCreated(itemstack1, player.world, player);

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

            ItemStack itemstack2 = slot.onTake(player, itemstack1);

            if (index == 0)
            {
                player.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != invOutput && super.canMergeSlot(stack, slotIn);
    }

    public void update()
    {
        if (player.world.isRemote) return;


        //Check previous item snapshot and return if same
        ArrayList<Integer> changedIndices = new ArrayList<>();
        int i = 0;
        for (ItemStack stack : invInput.stackList)
        {
            if (previousItems.get(i) != stack) changedIndices.add(i);
        }
        if (changedIndices.size() == 0) return;


        System.out.println("Changed!");

        //Determine which recipe to use
        if (recipe != null)
        {
            if (!recipe.matches(invInput)) recipe = null;
        }

        if (recipe == null)
        {
            for (BetterRecipe recipe : BetterRecipe.betterRecipes)
            {
                if (recipe.matches(invInput))
                {
                    this.recipe = recipe;
                    break;
                }
            }
        }

        if (recipe != null) recipe.craft(invInput, invOutput, true);


        //Update all changed slots for client
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(this.windowId, 0, invOutput.getStackInSlot(0)));
        for (int index : changedIndices)
        {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(this.windowId, i + 1, invInput.stackList.get(index)));
        }


        //Save snapshot of current items
        i = 0;
        for (ItemStack stack : invInput.stackList) previousItems.set(i, stack);
    }
}