package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.faerunutils.BlocksAndItems;
import com.fantasticsource.faerunutils.ClientProxy;
import com.fantasticsource.faerunutils.FaerunUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

public class BlockAssembler extends Block
{
    public BlockAssembler()
    {
        super(Material.WOOD);

        setCreativeTab(BlocksAndItems.creativeTab);
        setUnlocalizedName(FaerunUtils.MODID + ":assembler");
        setRegistryName("assembler");

        setHardness(2.5F);
        setSoundType(SoundType.WOOD);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote) ClientProxy.showAssemblerGUI();
        else
        {
            InterfaceAssembler iface = new InterfaceAssembler(worldIn, pos);

            EntityPlayerMP player = (EntityPlayerMP) playerIn;
            player.getNextWindowId();
            player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, iface.getGuiID(), iface.getDisplayName()));

            player.openContainer = iface.createContainer(player.inventory, player);
            player.openContainer.windowId = player.currentWindowId;
            player.openContainer.addListener(player);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));

            player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
        }

        return true;
    }

    public static class InterfaceAssembler implements IInteractionObject
    {
        private final World world;
        private final BlockPos position;

        public InterfaceAssembler(World world, BlockPos pos)
        {
            this.world = world;
            this.position = pos;
        }

        public String getName()
        {
            return "assembler";
        }

        public boolean hasCustomName()
        {
            return false;
        }

        public ITextComponent getDisplayName()
        {
            return new TextComponentTranslation(BlocksAndItems.blockAssembler.getUnlocalizedName() + ".name");
        }

        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerAssembler(playerIn, this.world, this.position);
        }

        public String getGuiID()
        {
            return FaerunUtils.MODID + ":assembler";
        }
    }
}
