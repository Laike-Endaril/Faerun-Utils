package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.faerunutils.BlocksAndItems;
import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatitems.assembly.ItemAssembly;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ContainerAssembler extends Container
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(FaerunUtils.MODID, "textures/gui/assembler.png");

    protected static final String[] PRIMARY_PART_ITEM_TYPES = new String[]
            {
                    "Dagger Blade",
                    "Sword Blade",
                    "Greatsword Blade",
                    "Greataxe Head",
                    "Axehead",
                    "Fist Blade",
                    "Spearhead",
                    "Staff Head",
                    "Bowlimbs",
                    "Mace Head",
                    "Shield Center",
                    "Focus Head",
            };

    protected static final String[] SECONDARY_PART_ITEM_TYPES = new String[]
            {
                    "Dagger Hilt",
                    "Sword Hilt",
                    "Greatsword Hilt",
                    "Greataxe Handle",
                    "Axe Handle",
                    "Fist Handle",
                    "Spearshaft",
                    "Staff Shaft",
                    "Bowstring",
                    "Mace Handle",
                    "Shield Rim",
                    "Focus Handle",
            };

    public final EntityPlayer player;
    public final World world;
    public final BlockPos position;

    public final int assemblerInventorySize, playerInventoryStartIndex, cargoInventorySize, hotbarStartIndex;
    public final int fullInventoryStart, fullInventoryEnd;

    public InventoryAssembler inventory = new InventoryAssembler(this);

    protected boolean updating = false;
    protected ItemStack[] previous = new ItemStack[5];


    public ContainerAssembler(EntityPlayer player, World world, BlockPos position)
    {
        this.player = player;
        this.world = world;
        this.position = position;


        //Slot indices
        assemblerInventorySize = inventory.getSizeInventory();

        cargoInventorySize = player.inventory.mainInventory.size() - 9;

        hotbarStartIndex = assemblerInventorySize;
        playerInventoryStartIndex = hotbarStartIndex + 9;

        fullInventoryStart = hotbarStartIndex;
        fullInventoryEnd = fullInventoryStart + 9 + cargoInventorySize - 1;


        //Set previous
        for (int i = 0; i < previous.length; i++) previous[i] = ItemStack.EMPTY;


        //Crafting slots
        addSlotToContainer(new AssemblerSlot(this, 0, 132, 35, 176, 0, stack ->
        {
            if (AssemblyTags.getState(stack) != AssemblyTags.STATE_FULL || AssemblyTags.getPartSlots(stack).size() <= 0) return false;
            if (inventorySlots.get(0).getStack().isEmpty())
            {
                boolean empty = inventorySlots.get(1).getStack().isEmpty();
                for (int i = 2; i <= 4; i++)
                {
                    if (inventorySlots.get(i).getStack().isEmpty() != empty) return false;
                }
            }
            return true;
        }));

        addSlotToContainer(new AssemblerSlot(this, 1, 20, 35, 208, 240, stack -> AssemblyTags.getState(stack) == AssemblyTags.STATE_EMPTY));
        addSlotToContainer(new AssemblerSlot(this, 2, 38, 35, 224, 240, stack -> MiscTags.getItemTypeName(stack).contains("Soul")));
        addSlotToContainer(new AssemblerSlot(this, 3, 56, 35, 240, 240, stack ->
        {
            String type = MiscTags.getItemTypeName(stack);
            return type.contains("Core") || Tools.contains(PRIMARY_PART_ITEM_TYPES, type);
        }));
        addSlotToContainer(new AssemblerSlot(this, 4, 74, 35, 240, 240, stack ->
        {
            String type = MiscTags.getItemTypeName(stack);
            return type.contains("Trim") || Tools.contains(SECONDARY_PART_ITEM_TYPES, type);
        }));


        //Inventory
        for (int i = 0; i < cargoInventorySize; i++)
        {
            addSlotToContainer(new Slot(player.inventory, 9 + i, 8 + (i % 9) * 18, 84 + (i / 9) * 18));
        }


        //Hotbar
        int hotbarY = 88 + ((cargoInventorySize + 9 - 1) / 9) * 18; //Pseudo-ceil function

        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, hotbarY));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer ignored)
    {
        super.onContainerClosed(player);

        if (!player.world.isRemote)
        {
            clearContainer(player, player.world, inventory);
        }
    }

    @Override
    protected void clearContainer(EntityPlayer player, World world, IInventory inventory)
    {
        if (!player.isEntityAlive() || player instanceof EntityPlayerMP && ((EntityPlayerMP) player).hasDisconnected())
        {
            if (inventory.getStackInSlot(0).isEmpty())
            {
                for (int i = 1; i < inventory.getSizeInventory(); i++)
                {
                    player.dropItem(inventory.removeStackFromSlot(i), false);
                }
            }
            else
            {
                player.dropItem(inventory.removeStackFromSlot(0), false);
                for (int i = 1; i < inventory.getSizeInventory(); i++)
                {
                    inventory.removeStackFromSlot(i);
                }
            }
        }
        else
        {
            if (inventory.getStackInSlot(0).isEmpty())
            {
                for (int i = 1; i < inventory.getSizeInventory(); i++)
                {
                    player.inventory.placeItemBackInInventory(world, inventory.removeStackFromSlot(i));
                }
            }
            else
            {
                player.inventory.placeItemBackInInventory(world, inventory.removeStackFromSlot(0));
                for (int i = 1; i < inventory.getSizeInventory(); i++)
                {
                    inventory.removeStackFromSlot(i);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer ignored)
    {
        if (player.world != world) return false;

        if (player.world.getBlockState(position).getBlock() != BlocksAndItems.blockAssembler) return false;

        return player.getDistanceSq((double) position.getX() + 0.5, (double) position.getY() + 0.5, (double) position.getZ() + 0.5) <= 64;
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

        if (slot instanceof AssemblerSlot) //From part or assembly slot
        {
            //To inventory or hotbar
            if (!mergeItemStack(itemstack1, fullInventoryStart, fullInventoryEnd + 1, false)) return ItemStack.EMPTY;
        }
        else if (index >= fullInventoryStart && index <= fullInventoryEnd) //From inventory or hotbar
        {
            //To crafting grid / input
            if (!mergeItemStack(itemstack1, 0, assemblerInventorySize, false)) return ItemStack.EMPTY;
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

        if (slotNumber == 0) //Assembly changed by player
        {
            //Set server-side parts
            ItemStack core = MCTools.cloneItemStack(inventorySlots.get(0).getStack());
            ArrayList<ItemStack> parts = ItemAssembly.disassemble(core);


            inventorySlots.get(1).putStack(core);
            syncSlot(1);
            for (int i = 2; i < 5; i++)
            {
                Slot slot = inventorySlots.get(i);
                slot.putStack(ItemStack.EMPTY);
                for (ItemStack part : parts)
                {
                    if (slot.isItemValid(part))
                    {
                        slot.putStack(part);
                        break;
                    }
                }
                syncSlot(i);
            }
        }
        else //Part changed by player
        {
            //Set server-side assembly
            ItemStack assembly = MCTools.cloneItemStack(inventorySlots.get(1).getStack());
            ArrayList<ItemStack> unusedParts = ItemAssembly.assemble(assembly, MCTools.cloneItemStack(inventorySlots.get(2).getStack()), MCTools.cloneItemStack(inventorySlots.get(3).getStack()), MCTools.cloneItemStack(inventorySlots.get(4).getStack()));
            inventorySlots.get(0).putStack(unusedParts.size() > 0 ? ItemStack.EMPTY : assembly);


            //Update assembly for client
            syncSlot(0);
        }


        for (int i = 0; i < previous.length; i++) previous[i] = MCTools.cloneItemStack(inventorySlots.get(i).getStack());
        updating = false;
    }
}
