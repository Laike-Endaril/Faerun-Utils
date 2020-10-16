package com.fantasticsource.faerunutils.bettercrafting.table;

import com.fantasticsource.faerunutils.BlocksAndItems;
import com.fantasticsource.faerunutils.Network;
import com.fantasticsource.faerunutils.bettercrafting.recipe.BetterRecipe;
import com.fantasticsource.faerunutils.bettercrafting.recipe.Recipes;
import com.fantasticsource.faerunutils.bettercrafting.recipes.RecipeSell;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Pair;
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
    private BetterRecipe recipe = null;
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

        playerInventoryWidth = 9;
        hotbarSize = 9;

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

    protected ArrayList<Integer> mergeItemStackBetter(ItemStack stack, int startIndex, int endIndex)
    {
        ArrayList<Integer> result = new ArrayList<>();

        int i = startIndex;

        if (stack.isStackable())
        {
            while (!stack.isEmpty())
            {
                if (i >= endIndex) break;

                Slot slot = inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();

                if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack))
                {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

                    if (j <= maxSize)
                    {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        result.add(i);
                    }
                    else if (itemstack.getCount() < maxSize)
                    {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        result.add(i);
                    }
                }

                ++i;
            }
        }

        if (!stack.isEmpty())
        {
            i = startIndex;

            while (true)
            {
                if (i >= endIndex) break;

                Slot slot1 = inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();

                if (itemstack1.isEmpty() && slot1.isItemValid(stack))
                {
                    if (stack.getCount() > slot1.getSlotStackLimit())
                    {
                        slot1.putStack(stack.splitStack(slot1.getSlotStackLimit()));
                    }
                    else
                    {
                        slot1.putStack(stack.splitStack(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    result.add(i);
                    break;
                }

                ++i;
            }
        }

        return result;
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

    public int getSlotIndex(Slot slot)
    {
        for (int i = 0; i < inventorySlots.size(); i++)
        {
            if (inventorySlots.get(i) == slot) return i;
        }

        return -1;
    }

    protected void syncSlot(int slotIndex)
    {
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(windowId, slotIndex, inventorySlots.get(slotIndex).getStack()));
    }

    protected void syncSlot(Slot slot)
    {
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(windowId, getSlotIndex(slot), slot.getStack()));
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
            if (recipe instanceof RecipeSell) recipe.craft(invInput, invOutput, itemstack1);
            else
            {
                itemstack1.getItem().onCreated(itemstack1, player.world, player);

                //To inventory or hotbar
                ArrayList<Integer> indices = mergeItemStackBetter(itemstack1, fullInventoryStart, fullInventoryEnd + 1);
                if (indices.size() == 0) return ItemStack.EMPTY;

                slot.onSlotChange(itemstack1, itemstack);
                if (!world.isRemote) for (int i : indices) syncSlot(i);
            }
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
        update(false);
    }

    public void update(boolean actionChange)
    {
        if (player.world.isRemote) return;


        //Check previous item snapshot
        //If same, return
        //If different, track changed indices and update item snapshot
        ArrayList<Integer> changedIndices = new ArrayList<>();
        if (actionChange)
        {
            if (DEBUG) System.out.println("Action Change");
        }
        else
        {
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
        }


        //Determine which recipe to use
        if (recipe != null)
        {
            if (!recipe.matches(invInput)) setServerRecipe(null);
        }

        if (recipe == null)
        {
            for (BetterRecipe recipe : Recipes.recipeList.values())
            {
                if (recipe.matches(invInput))
                {
                    setServerRecipe(recipe);
                    break;
                }
            }
        }


        //Set server-side output via recipe method
        Pair<ItemStack, ItemStack> result = recipe == null ? new Pair<>(ItemStack.EMPTY, ItemStack.EMPTY) : recipe.prepareToCraft(invInput);
        invOutput.setInventorySlotContents(0, result.getKey());


        //Update all changed slots for client
        for (int index : changedIndices)
        {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(windowId, index + 1, invInput.stackList.get(index)));
        }
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(windowId, 0, result.getValue()));

        //For higher-than-byte stack sizes in output
        int count = result.getValue().getCount();
        if (count > 127) Network.WRAPPER.sendTo(new Network.RecipeOutputCountPacket(result.getValue().getCount()), (EntityPlayerMP) player);
    }

    public void switchRecipe(int offset)
    {
        ArrayList<BetterRecipe> validRecipes = new ArrayList<>();
        for (BetterRecipe r : Recipes.recipeList.values())
        {
            if (r.matches(invInput)) validRecipes.add(r);
        }


        if (validRecipes.size() == 0) return;

        if (validRecipes.size() == 1)
        {
            setServerRecipe(validRecipes.get(0));
            return;
        }


        int index = validRecipes.indexOf(recipe);
        if (index >= 0) index += offset;
        else
        {
            if (offset > 0) offset--;
            index = offset;
        }

        setServerRecipe(validRecipes.get(Tools.posMod(index, validRecipes.size())));
        update(true);
    }

    public BetterRecipe getRecipe()
    {
        return recipe;
    }

    public void setServerRecipe(BetterRecipe recipe)
    {
        if (!world.isRemote)
        {
            this.recipe = recipe;
            Network.WRAPPER.sendTo(new Network.SetRecipePacket(recipe), (EntityPlayerMP) player);
        }
    }

    public void setClientRecipe(BetterRecipe recipe)
    {
        if (world.isRemote)
        {
            if (recipe == null) recipe = Recipes.NULL;
            this.recipe = recipe;
        }
    }
}
