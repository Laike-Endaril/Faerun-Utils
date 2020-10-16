package com.fantasticsource.faerunutils.assembler.recipes;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.assembler.recipe.BetterRecipe;
import com.fantasticsource.faerunutils.assembler.table.ContainerAssembler;
import com.fantasticsource.faerunutils.assembler.table.InventoryAssemblerInput;
import com.fantasticsource.faerunutils.assembler.table.InventoryAssemblerOutput;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;

public class RecipeSell extends BetterRecipe
{
    private static final ItemStack COPPER_COIN = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("rpg_economy", "item.currency")));

    @Override
    public String translationKey()
    {
        return FaerunUtils.MODID + ":recipe.sell";
    }

    @Override
    public Color color()
    {
        return Color.AQUA;
    }

    @Override
    public boolean matches(InventoryAssemblerInput inv)
    {
        boolean found = false;
        for (ItemStack stack : inv.stackList)
        {
            if (stack.isEmpty()) continue;


            found = true;


            if (!stack.getItem().getRegistryName().toString().contains("bluerpg:dynamic")) return false;

            if (!stack.hasTagCompound()) return false;

            NBTTagCompound compound = stack.getTagCompound();
            if (!compound.hasKey("display")) return false;

            compound = compound.getCompoundTag("display");
            if (!compound.hasKey("Lore")) return false;

            String loreString = compound.getTag("Lore").toString();
            int index = loreString.indexOf("Value:");
            if (index == -1) return false;

            loreString = loreString.substring(index + 6);
            loreString = loreString.substring(0, loreString.indexOf('"'));

            try
            {
                Integer.parseInt(loreString);
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }
        return found;
    }

    @Override
    public Pair<ItemStack, ItemStack> prepareToCraft(InventoryAssemblerInput in)
    {
        int value = 0;
        for (ItemStack stack : in.stackList)
        {
            if (stack.isEmpty()) continue;

            String loreString = stack.getTagCompound().getCompoundTag("display").getTag("Lore").toString();
            loreString = loreString.substring(loreString.indexOf("Value:") + 6);
            loreString = loreString.substring(0, loreString.indexOf('"'));
            value += Integer.parseInt(loreString) * stack.getCount();
        }

        ItemStack result = COPPER_COIN.copy();
        result.setCount(value);
        return new Pair<>(result, result);
    }

    @Override
    public ArrayList<ItemStack> craft(InventoryAssemblerInput in, InventoryAssemblerOutput out, ItemStack grabbedStack)
    {
        ContainerAssembler container = (ContainerAssembler) in.container;
        if (!container.world.isRemote)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.commandManager.executeCommand(server, "/rpg_economy currency add Common " + container.player.getName() + " " + grabbedStack.getCount());
        }


        grabbedStack.setCount(0);


        for (int i = in.getSizeInventory() - 1; i >= 0; i--)
        {
            in.setInventorySlotContents(i, ItemStack.EMPTY);
        }

        return new ArrayList<>();
    }
}
