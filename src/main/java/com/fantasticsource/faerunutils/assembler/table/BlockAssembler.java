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
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
            ContainerAssembler.InterfaceAssembler iface = new ContainerAssembler.InterfaceAssembler(worldIn, pos);

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
}
