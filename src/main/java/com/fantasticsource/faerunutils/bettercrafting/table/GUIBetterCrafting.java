package com.fantasticsource.faerunutils.bettercrafting.table;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.tools.Collision;
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
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaerunUtils.MODID, "textures/gui/bettercraftingtable.png");

    private static final ResourceLocation TEXTURE_FAERUN = new ResourceLocation(FaerunUtils.MODID, "textures/gui/bettercraftingtablefaerun.png");

    public GUIBetterCrafting()
    {
        super(new ContainerBetterCraftingTable(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, BlockPos.ORIGIN));

        if (faerun)
        {
            xSize = 248;
            ySize = 202;
        }
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

        //Main background
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x1, y2, zLevel).tex(0, (double) ySize / 256).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex((double) xSize / 256, (double) ySize / 256).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex((double) xSize / 256, 0).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(0, 0).endVertex();
        tessellator.draw();


        //Up arrow
        x1 += 152;
        y1 += 31;
        x2 = x1 + 8;
        y2 = y1 + 10;

        double u1;
        if (Collision.pointRectangle(mouseX, mouseY, x1, y1, x2, y2)) u1 = 232; //active
        else u1 = 224; //inactive

        double v1 = 240, u2 = u1 + 8, v2 = v1 + 10;

        u1 /= 256;
        v1 /= 256;
        u2 /= 256;
        v2 /= 256;

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x1, y2, zLevel).tex(u1, v2).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex(u2, v2).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex(u2, v1).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(u1, v1).endVertex();
        tessellator.draw();


        //Down arrow
        y1 += 13;
        y2 += 13;

        if (Collision.pointRectangle(mouseX, mouseY, x1, y1, x2, y2)) u1 = 248; //active
        else u1 = 240; //inactive

        u2 = (u1 + 8) / 256;
        u1 /= 256;

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x1, y2, zLevel).tex(u1, v2).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex(u2, v2).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex(u2, v1).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(u1, v1).endVertex();
        tessellator.draw();
    }
}
