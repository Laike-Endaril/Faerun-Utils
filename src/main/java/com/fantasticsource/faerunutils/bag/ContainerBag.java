package com.fantasticsource.faerunutils.bag;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ContainerBag extends Container
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(FaerunUtils.MODID, "textures/gui/bag.png");

    public final EntityPlayer player;
    public final World world;
    public final String itemType;

    public final int bagInventorySize, playerInventoryStartIndex, cargoInventorySize, hotbarStartIndex;
    public final int fullInventoryStart, fullInventoryEnd;

    public final InventoryBag inventory;

    public final ItemStack bag;

    protected boolean updating = false;
    protected ItemStack[] previous = new ItemStack[5];


    public ContainerBag(EntityPlayer player, World world, String itemType, int size, ItemStack bag)
    {
        this.player = player;
        this.world = world;
        this.itemType = itemType;
        bagInventorySize = size;
        this.bag = bag;


        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(bag);
        inventory = new InventoryBag(this, partSlots);


        //Slot indices
        cargoInventorySize = player.inventory.mainInventory.size() - 9;

        hotbarStartIndex = bagInventorySize;
        playerInventoryStartIndex = hotbarStartIndex + 9;

        fullInventoryStart = hotbarStartIndex;
        fullInventoryEnd = fullInventoryStart + 9 + cargoInventorySize - 1;


        //Bag slots
        int i = 0;
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                IPartSlot partSlot = partSlots.get(i);
                addSlotToContainer(new BagSlot(this, i++, 8 + x * 18, 8 + y * 18, 208, 240, partSlot::partIsValidForSlot));
                if (i >= bagInventorySize) break;
            }
            if (i >= bagInventorySize) break;
        }


        //Inventory
        for (i = 0; i < cargoInventorySize; i++)
        {
            addSlotToContainer(new Slot(player.inventory, 9 + i, 8 + (i % 9) * 18, 84 + (i / 9) * 18));
        }


        //Hotbar
        int hotbarY = 88 + ((cargoInventorySize + 9 - 1) / 9) * 18; //Pseudo-ceil function

        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, hotbarY));
        }


        //Set previous
        for (i = 0; i < previous.length; i++) previous[i] = MCTools.cloneItemStack(inventorySlots.get(i).getStack());
    }

    @Override
    protected void clearContainer(EntityPlayer player, World world, IInventory inventory)
    {
    }

    @Override
    public boolean canInteractWith(EntityPlayer ignored)
    {
        return player.isEntityAlive() && player.world == world;
    }

    protected void syncSlot(int slotIndex)
    {
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(windowId, slotIndex, inventorySlots.get(slotIndex).getStack()));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer ignored, int index)
    {
        Slot slot = inventorySlots.get(index);
        if (slot == null) return ItemStack.EMPTY;

        ItemStack itemstack1 = slot.getStack();
        if (itemstack1.isEmpty()) return ItemStack.EMPTY;


        ItemStack itemstack = itemstack1.copy();

        if (slot instanceof BagSlot) //From bag
        {
            //To inventory or hotbar
            if (!mergeItemStack(itemstack1, fullInventoryStart, fullInventoryEnd + 1, false)) return ItemStack.EMPTY;
        }
        else if (index >= fullInventoryStart && index <= fullInventoryEnd) //From inventory or hotbar
        {
            //To bag
            if (!mergeItemStack(itemstack1, 0, bagInventorySize, false)) return ItemStack.EMPTY;
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


        return new ItemStack(Items.BOW);
    }

    public void update(int slotNumber)
    {
        if (updating || player.world.isRemote || ItemMatcher.stacksMatch(previous[slotNumber], inventorySlots.get(slotNumber).getStack())) return;

        updating = true;

        //Set internal data
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(bag);
        partSlots.get(slotNumber).setPart(inventorySlots.get(slotNumber).getStack());
        AssemblyTags.setPartSlots(bag, partSlots);

        for (int i = 0; i < previous.length; i++) previous[i] = MCTools.cloneItemStack(inventorySlots.get(i).getStack());
        updating = false;
    }
}
