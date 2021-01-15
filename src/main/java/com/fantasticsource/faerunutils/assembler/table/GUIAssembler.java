package com.fantasticsource.faerunutils.assembler.table;

import com.fantasticsource.mctools.inventory.gui.BetterContainerGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIAssembler extends BetterContainerGUI
{
    public GUIAssembler()
    {
        super(new ContainerAssembler(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, BlockPos.ORIGIN));
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(ContainerAssembler.TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();


        //Main background
        int x1 = (width >> 1) - (xSize >> 1);
        int y1 = (height >> 1) - (ySize >> 1);
        int x2 = x1 + xSize;
        int y2 = y1 + ySize;

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x1, y2, zLevel).tex(0, (double) ySize / 256).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex((double) xSize / 256, (double) ySize / 256).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex((double) xSize / 256, 0).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(0, 0).endVertex();
        tessellator.draw();
    }
}
