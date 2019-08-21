package com.fantasticsource.faerunutils.bettercrafting.table;

import com.fantasticsource.faerunutils.FaerunUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.fantasticsource.faerunutils.FaerunUtils.faerun;

@SideOnly(Side.CLIENT)
public class GUIBetterCrafting extends GuiContainer
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/crafting_table.png");

    private static final ResourceLocation TEXTURE_FAERUN = new ResourceLocation(FaerunUtils.MODID, "textures/gui/bettercraftingtablefaerun.png");

    private final double halfPixel;

    public GUIBetterCrafting()
    {
        super(new ContainerBetterCraftingTable(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, BlockPos.ORIGIN));

        if (faerun)
        {
            xSize = 248;
            ySize = 202;
        }

        halfPixel = 0.5 / 256;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(faerun ? TEXTURE_FAERUN : TEXTURE);

        int x1 = (width >> 1) - (xSize >> 1);
        int y1 = (height >> 1) - (ySize >> 1);

        int x2 = x1 + xSize;
        int y2 = y1 + ySize;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x1, y2, zLevel).tex(halfPixel, (double) ySize / 256 - halfPixel).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex((double) xSize / 256 - halfPixel, (double) ySize / 256 - halfPixel).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex((double) xSize / 256 - halfPixel, halfPixel).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(halfPixel, halfPixel).endVertex();
        tessellator.draw();
    }
}