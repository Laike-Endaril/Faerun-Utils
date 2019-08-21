package com.fantasticsource.faerunutils.bettercrafting.table;

import com.fantasticsource.faerunutils.FaerunUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIBetterCrafting extends GuiContainer
{
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(FaerunUtils.MODID, "textures/gui/bettercraftingtable.png");

    public GUIBetterCrafting()
    {
        super(new ContainerBetterCraftingTable(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, BlockPos.ORIGIN));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString(I18n.format("container.crafting"), 28, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        drawTexturedModalRect(guiLeft, (height - ySize) / 2, 0, 0, xSize, ySize);
    }
}