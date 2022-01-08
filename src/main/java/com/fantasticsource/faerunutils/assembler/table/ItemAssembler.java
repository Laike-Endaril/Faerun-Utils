package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.faerunutils.BlocksAndItems;
import com.fantasticsource.faerunutils.FaerunUtils;
import net.minecraft.item.ItemBlock;

public class ItemAssembler extends ItemBlock
{
    public ItemAssembler()
    {
        super(BlocksAndItems.blockAssembler);

        setUnlocalizedName(FaerunUtils.MODID + ":assembler");
        setRegistryName("assembler");
    }
}
