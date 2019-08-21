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
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.Arrays;

public class ContainerBetterCraftingTable extends Container
{
    private static final boolean DEBUG = false;

    public final EntityPlayer player;
    public final World world;
    public final BlockPos position;

    public final int playerInventoryWidth;
    public final int outputIndex, craftingGridStartIndex, craftingGridSize, playerInventoryStartIndex, playerInventorySize, hotbarStartIndex, hotbarSize;
    public final int fullInventoryStart, fullInventoryEnd;

    public InventoryBetterCraftingInput invInput = new InventoryBetterCraftingInput(this, 3, 3);
    public InventoryBetterCraftingOutput invOutput = new InventoryBetterCraftingOutput();
    public BetterRecipe recipe = null;

    private ItemStack[] previousItems;


    public ContainerBetterCraftingTable(EntityPlayer player, World world, BlockPos position)
    {
        this.player = player;
        this.world = world;
        this.position = position;


        //Slot indices
        outputIndex = 0;

        craftingGridSize = invInput.getSizeInventory();
        craftingGridStartIndex = outputIndex + 1;

        boolean bluerpg = Loader.isModLoaded("bluerpg");
        if (bluerpg)
        {
            playerInventoryWidth = 13;
            hotbarSize = 4;
        }
        else
        {
            playerInventoryWidth = 9;
            hotbarSize = 9;
        }

        playerInventorySize = player.inventory.mainInventory.size() - hotbarSize;

        hotbarStartIndex = craftingGridStartIndex + craftingGridSize;
        playerInventoryStartIndex = hotbarStartIndex + hotbarSize;

        fullInventoryStart = hotbarStartIndex;
        fullInventoryEnd = fullInventoryStart + hotbarSize + playerInventorySize - 1;


        //Crafting slots
        addSlotToContainer(new BetterCraftingResultSlot(this, 0, 124, 35));

        for (int y = 0; y < invInput.inventoryWidth; ++y)
        {
            for (int x = 0; x < invInput.inventoryHeight; ++x)
            {
                addSlotToContainer(new BetterCraftingGridSlot(invInput, x + y * 3, 30 + x * 18, 17 + y * 18));
            }
        }


        //Inventory
        for (int i = 0; i < playerInventorySize; i++)
        {
            addSlotToContainer(new Slot(player.inventory, hotbarSize + i, 8 + (i % playerInventoryWidth) * 18, 84 + (i / playerInventoryWidth) * 18));
        }


        //Hotbar
        int hotbarY = 88 + ((playerInventorySize + playerInventoryWidth - 1) / playerInventoryWidth) * 18; //Pseudo-ceil function

        for (int x = 0; x < hotbarSize; x++)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, hotbarY));
        }


        previousItems = new ItemStack[invInput.getSizeInventory()];
        Arrays.fill(previousItems, ItemStack.EMPTY);
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
        Slot slot = inventorySlots.get(index);
        if (slot == null) return ItemStack.EMPTY;

        ItemStack itemstack = slot.getStack();
        if (itemstack.isEmpty()) return ItemStack.EMPTY;


        ItemStack itemstack1 = slot.getStack();
        itemstack = itemstack1.copy();

        if (slot instanceof BetterCraftingResultSlot) //From output
        {
            itemstack1.getItem().onCreated(itemstack1, player.world, player);

            //To inventory or hotbar
            if (!mergeItemStack(itemstack1, fullInventoryStart, fullInventoryEnd + 1, false)) return ItemStack.EMPTY;

            slot.onSlotChange(itemstack1, itemstack);
        }
        else if (slot instanceof BetterCraftingGridSlot) //From crafting grid / input
        {
            //To inventory or hotbar
            if (!mergeItemStack(itemstack1, fullInventoryStart, fullInventoryEnd + 1, false)) return ItemStack.EMPTY;
        }
        else if (index >= fullInventoryStart && index <= fullInventoryEnd) //From inventory or hotbar
        {
            //To crafting grid / input
            if (!mergeItemStack(itemstack1, craftingGridStartIndex, craftingGridStartIndex + craftingGridSize, false)) return ItemStack.EMPTY;
        }
        else
        {
            throw new IllegalStateException("Unsupported custom inventory detected!");
        }


        if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();

        if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;

        ItemStack itemstack2 = slot.onTake(player, itemstack1);

        if (index == 0) player.dropItem(itemstack2, false);


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


        //Check previous item snapshot
        //If same, return
        //If different, track changed indices and update item snapshot
        ArrayList<Integer> changedIndices = new ArrayList<>();
        int i = 0;
        for (ItemStack stack : invInput.stackList)
        {
            ItemStack previous = previousItems[i];
            if (previous.isEmpty() && stack.isEmpty())
            {
                i++;
                continue;
            }

            if (previous.getItem() != stack.getItem() || previous.getCount() != stack.getCount() || previous.getItemDamage() != stack.getItemDamage() || !previous.getDisplayName().equals(stack.getDisplayName()) || !previous.serializeNBT().toString().equals(stack.serializeNBT().toString()))
            {
                changedIndices.add(i);
                if (DEBUG) System.out.println(i + ": " + previous + " -> " + stack);
                previousItems[i] = stack.copy();
            }
            i++;
        }
        if (changedIndices.size() == 0)
        {
            if (DEBUG) System.out.println("Unchanged");
            return;
        }
        if (DEBUG) System.out.println("Changed");


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


        //Set server-side output via recipe method
        if (recipe == null) invOutput.setInventorySlotContents(0, ItemStack.EMPTY);
        else recipe.preview(invInput, invOutput);


        //Update all changed slots for client
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(this.windowId, 0, invOutput.getStackInSlot(0)));
        for (int index : changedIndices)
        {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(this.windowId, index + 1, invInput.stackList.get(index)));
        }
    }
}