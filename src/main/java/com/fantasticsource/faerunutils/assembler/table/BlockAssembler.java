package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.faerunutils.BlocksAndItems;
import com.fantasticsource.faerunutils.ClientProxy;
import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.tiamatitems.Network;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        else Network.LAST_ASSEMBLER_INTERACTION_POSITIONS.put(playerIn.getUniqueID(), pos);

        return true;
    }
}
