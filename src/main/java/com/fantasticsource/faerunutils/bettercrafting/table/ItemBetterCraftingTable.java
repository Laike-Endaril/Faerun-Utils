package com.fantasticsource.faerunutils.bettercrafting.table;

import com.fantasticsource.faerunutils.BlocksAndItems;
import com.fantasticsource.faerunutils.FaerunUtils;
import net.minecraft.item.ItemBlock;

public class ItemBetterCraftingTable extends ItemBlock
{
    public ItemBetterCraftingTable()
    {
        super(BlocksAndItems.blockBetterCraftingTable);

        setUnlocalizedName(FaerunUtils.MODID + ":bettercraftingtable");
        setRegistryName("bettercraftingtable");
    }
}
