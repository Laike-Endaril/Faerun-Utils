package com.fantasticsource.faerunutils.interaction.trading;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.mctools.inventory.slot.BetterSlot;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class ContainerTrade extends Container
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(FaerunUtils.MODID, "textures/gui/trade.png");

    public final EntityPlayer player;
    public final World world;

    public final int playerInventoryStartIndex, cargoInventorySize, hotbarStartIndex;
    public final int fullInventoryStart, fullInventoryEnd;

    public final InventoryTrade inventory;


    public ContainerTrade(EntityPlayer player, World world)
    {
        this.player = player;
        this.world = world;


        inventory = new InventoryTrade(this);


        //Slot indices
        cargoInventorySize = player.inventory.mainInventory.size() - 9;

        hotbarStartIndex = 18;
        playerInventoryStartIndex = hotbarStartIndex + 9;

        fullInventoryStart = hotbarStartIndex;
        fullInventoryEnd = fullInventoryStart + 9 + cargoInventorySize - 1;


        //Your slots
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new FilteredSlot(inventory, x, 8 + x * 18, 8, TEXTURE, 256, 256, 240, 0, false, 64, stack -> false));
        }


        //My slots
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new BetterSlot(inventory, x + 9, 8 + x * 18, 44, TEXTURE, 256, 256, 240, 0));
        }


        //Inventory
        for (int i = 0; i < 27; i++)
        {
            addSlotToContainer(new Slot(player.inventory, 9 + i, 8 + (i % 9) * 18, 84 + (i / 9) * 18));
        }


        //Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer ignored)
    {
        super.onContainerClosed(player);

        if (!player.world.isRemote)
        {
            clearContainer(player, player.world, inventory);
            Trading.TradeData data = Trading.TRADE_DATA.remove(player);
            if (data != null)
            {
                EntityPlayerMP other = data.playerBesides((EntityPlayerMP) player);
                Trading.TRADE_DATA.remove(other);
                other.closeScreen();
            }
        }
    }

    @Override
    protected void clearContainer(EntityPlayer player, World world, IInventory inventory)
    {
        if (!player.isEntityAlive() || player instanceof EntityPlayerMP && ((EntityPlayerMP) player).hasDisconnected())
        {
            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {
                player.dropItem(inventory.removeStackFromSlot(i), false);
            }
        }
        else
        {
            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {
                player.inventory.placeItemBackInInventory(world, inventory.removeStackFromSlot(i));
            }
        }
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

        ItemStack itemstack2 = slot.onTake(player, itemstack1);

        if (index == 0) player.dropItem(itemstack2, false);


        return new ItemStack(Items.BOW);
    }


    public static class InterfaceTrade implements IInteractionObject
    {
        private final World world;

        public InterfaceTrade(World world)
        {
            this.world = world;
        }

        public String getName()
        {
            return "Trade";
        }

        public boolean hasCustomName()
        {
            return false;
        }

        public ITextComponent getDisplayName()
        {
            return new TextComponentString(getName());
        }

        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerTrade(playerIn, world);
        }

        public String getGuiID()
        {
            return MODID + ":bag";
        }
    }
}
