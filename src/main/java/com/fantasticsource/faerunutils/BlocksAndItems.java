package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.assembler.table.BlockAssembler;
import com.fantasticsource.faerunutils.assembler.table.ItemAssembler;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class BlocksAndItems
{
    public static BlockAssembler blockAssembler;
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

        blockAssembler = new BlockAssembler();
        registry.register(blockAssembler);

        ReflectionTool.set(Material.class, new String[]{"field_175972_I", "BARRIER"}, null, new MaterialBarrier());
        Block blockBarrierEdit = new BlockBarrierEdit().setUnlocalizedName("barrier").setRegistryName(Objects.requireNonNull(Blocks.BARRIER.getRegistryName()));
        registry.register(blockBarrierEdit);
        ReflectionTool.set(Blocks.class, new String[]{"field_180401_cv", "BARRIER"}, null, blockBarrierEdit);
    }

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        itemAssembler = new ItemAssembler();
        registry.register(itemAssembler);
    }

    @SubscribeEvent
    public static void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(itemAssembler, 0, new ModelResourceLocation("minecraft:crafting_table", "inventory"));
    }


    public static class BlockBarrierEdit extends BlockBarrier
    {
        protected BlockBarrierEdit()
        {
            super();
        }
    }


    public static class MaterialBarrier extends Material
    {
        public MaterialBarrier()
        {
            super(MapColor.AIR);
            setRequiresTool();
            setImmovableMobility();
        }

        public boolean blocksLight()
        {
            return false;
        }

        public boolean blocksMovement()
        {
            return false;
        }
    }
}
