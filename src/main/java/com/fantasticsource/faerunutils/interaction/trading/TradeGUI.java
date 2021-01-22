package com.fantasticsource.faerunutils.interaction.trading;

import com.fantasticsource.mctools.inventory.gui.BetterContainerGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TradeGUI extends BetterContainerGUI
{
    public TradeGUI()
    {
        super(new ContainerTrade(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world));
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(ContainerTrade.TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        //Main background
        int x1 = (width >> 1) - (xSize >> 1);
        int y1 = (height >> 1) - (ySize >> 1);
        int x2 = x1 + xSize;
        int y2 = y1 + ySize;
        bufferbuilder.pos(x1, y2, zLevel).tex(0, (double) ySize / 256).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex((double) xSize / 256, (double) ySize / 256).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex((double) xSize / 256, 0).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(0, 0).endVertex();

        tessellator.draw();


        drawCenteredString(fontRenderer, "MAKE SURE YOU HAVE SPACE!", guiLeft + 88, guiTop + 67, 0xffff0000);
    }
}
