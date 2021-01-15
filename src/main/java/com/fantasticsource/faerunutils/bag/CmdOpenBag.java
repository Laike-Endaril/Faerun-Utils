package com.fantasticsource.faerunutils.bag;

import com.fantasticsource.faerunutils.Network;
import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class CmdOpenBag extends CommandBase
{
    @Override
    public String getName()
    {
        return "openBag";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender.canUseCommand(getRequiredPermissionLevel(), getName())) return MODID + ".cmd." + getName() + ".usage";

        return "commands.generic.permission";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            notifyCommandListener(sender, this, getUsage(sender));
            return;
        }


        String type = args.length == 1 ? null : args[1];
        EntityPlayerMP player = (EntityPlayerMP) PlayerData.getEntity(args[0]);
        ItemStack stack = player.getHeldItemMainhand();
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack);
        if (partSlots.size() == 0 || !partSlots.get(0).getSlotType().equals(type))
        {
            stack = player.getHeldItemOffhand();
            partSlots = AssemblyTags.getPartSlots(stack);
        }

        if (partSlots.size() == 0 || !partSlots.get(0).getSlotType().equals(type)) return;


        Network.WRAPPER.sendTo(new Network.BagPacket(type, partSlots.size(), stack), player);

        InterfaceBag iface = new InterfaceBag(player.world, type, partSlots.size(), stack);

        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, iface.getGuiID(), iface.getDisplayName()));

        player.openContainer = iface.createContainer(player.inventory, player);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }


    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
    }


    public static class InterfaceBag implements IInteractionObject
    {
        private final World world;
        private final String itemType;
        private final int size;
        private final ItemStack bag;

        public InterfaceBag(World world, String itemType, int size, ItemStack bag)
        {
            this.world = world;
            this.itemType = itemType;
            this.size = size;
            this.bag = bag;
        }

        public String getName()
        {
            return itemType == null ? "Bag" : itemType + " Bag";
        }

        public boolean hasCustomName()
        {
            return false;
        }

        public ITextComponent getDisplayName()
        {
            return new TextComponentString(itemType == null ? "Bag" : itemType + " Bag");
        }

        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerBag(playerIn, world, itemType, size, bag);
        }

        public String getGuiID()
        {
            return MODID + ":bag";
        }
    }
}
