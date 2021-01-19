package com.fantasticsource.faerunutils.interaction.trading;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.mctools.inventory.slot.BetterSlot;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
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

public class ContainerTrade extends Container
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(FaerunUtils.MODID, "textures/gui/trade.png");

    public final EntityPlayer p1, p2;
    public final World world;

    public final int playerInventoryStartIndex, cargoInventorySize, hotbarStartIndex;
    public final int fullInventoryStart, fullInventoryEnd;

    public final InventoryTrade inventory;


    public ContainerTrade(EntityPlayer p1, EntityPlayer p2, World world)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.world = world;


        inventory = new InventoryTrade(this);


        //Slot indices
        cargoInventorySize = p1.inventory.mainInventory.size() - 9;

        hotbarStartIndex = 18;
        playerInventoryStartIndex = hotbarStartIndex + 9;

        fullInventoryStart = hotbarStartIndex;
        fullInventoryEnd = fullInventoryStart + 9 + cargoInventorySize - 1;


        //Your slots
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new FilteredSlot(inventory, x, 8 + x * 18, 8, TEXTURE, 256, 256, 240, 240, false, 64, stack -> false));
        }


        //My slots
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new BetterSlot(inventory, x + 9, 8 + x * 18, 44, TEXTURE, 256, 256, 240, 240));
        }


        //Inventory
        for (int i = 0; i < 27; i++)
        {
            addSlotToContainer(new Slot(p1.inventory, 9 + i, 8 + (i % 9) * 18, 84 + (i / 9) * 18));
        }


        //Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(p1.inventory, x, 8 + x * 18, 142));
        }
    }

    @Override
    protected void clearContainer(EntityPlayer player, World world, IInventory inventory)
    {
    }

    @Override
    public boolean canInteractWith(EntityPlayer ignored)
    {
        return p1.isEntityAlive() && p1.world == world;
    }

    protected void syncSlot(int slotIndex)
    {
        ((EntityPlayerMP) p1).connection.sendPacket(new SPacketSetSlot(windowId, slotIndex, inventorySlots.get(slotIndex).getStack()));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer ignored, int index)
    {
        Slot slot = inventorySlots.get(index);
        if (slot == null) return ItemStack.EMPTY;

        ItemStack itemstack1 = slot.getStack();
        if (itemstack1.isEmpty()) return ItemStack.EMPTY;


        ItemStack itemstack = itemstack1.copy();

        if (slot instanceof FilteredSlot) //Your slots (blocked for me)
        {
            return ItemStack.EMPTY;
        }
        else if (index < 18) //From my slots
        {
            //To inventory or hotbar
            if (!mergeItemStack(itemstack1, fullInventoryStart, fullInventoryEnd + 1, false)) return ItemStack.EMPTY;
        }
        else if (index >= fullInventoryStart && index <= fullInventoryEnd) //From inventory or hotbar
        {
            //To my slots
            if (!mergeItemStack(itemstack1, 9, 18, false)) return ItemStack.EMPTY;
        }
        else
        {
            throw new IllegalStateException("Unsupported custom inventory detected!");
        }


        if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();

        if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;

        ItemStack itemstack2 = slot.onTake(p1, itemstack1);

        if (index == 0) p1.dropItem(itemstack2, false);


        return new ItemStack(Items.BOW);
    }
}
