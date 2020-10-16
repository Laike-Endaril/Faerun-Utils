package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.assembler.table.BlockAssembler;
import com.fantasticsource.faerunutils.assembler.table.ItemAssembler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class BlocksAndItems
{
    @GameRegistry.ObjectHolder("faerunutils:assembler")
    public static BlockAssembler blockAssembler;
    @GameRegistry.ObjectHolder("faerunutils:assembler")
    public static ItemAssembler itemAssembler;


    public static CreativeTabs creativeTab = new CreativeTabs(FaerunUtils.MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(blockAssembler);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> itemStacks)
        {
            super.displayAllRelevantItems(itemStacks);
        }
    };


    @SubscribeEvent
    public static void blockRegistry(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockAssembler());
    }

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ItemAssembler());
    }

    @SubscribeEvent
    public static void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(itemAssembler, 0, new ModelResourceLocation("minecraft:crafting_table", "inventory"));
    }
}
