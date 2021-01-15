package com.fantasticsource.faerunutils.bag;

import com.fantasticsource.mctools.inventory.gui.BetterContainerGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIBag extends BetterContainerGUI
{
    public GUIBag(String itemType, int size, ItemStack bag)
    {
        super(new ContainerBag(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, itemType, size, bag));
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(ContainerBag.TEXTURE);

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

        //Slots
        int i = 0, size = ((ContainerBag) inventorySlots).bagInventorySize;
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                x1 = guiLeft + 7 + 18 * (i % 9);
                y1 = guiTop + 7 + 18 * (i / 9);
                x2 = x1 + 18;
                y2 = y1 + 18;
                if (i++ < size)
                {
                    bufferbuilder.pos(x1, y2, zLevel).tex(0, 1).endVertex();
                    bufferbuilder.pos(x2, y2, zLevel).tex((double) 18 / 256, 1).endVertex();
                    bufferbuilder.pos(x2, y1, zLevel).tex((double) 18 / 256, (double) 238 / 256).endVertex();
                    bufferbuilder.pos(x1, y1, zLevel).tex(0, (double) 238 / 256).endVertex();
                }
                else
                {
                    bufferbuilder.pos(x1, y2, zLevel).tex((double) 18 / 256, 1).endVertex();
                    bufferbuilder.pos(x2, y2, zLevel).tex((double) 36 / 256, 1).endVertex();
                    bufferbuilder.pos(x2, y1, zLevel).tex((double) 36 / 256, (double) 238 / 256).endVertex();
                    bufferbuilder.pos(x1, y1, zLevel).tex((double) 18 / 256, (double) 238 / 256).endVertex();
                }
            }
        }

        tessellator.draw();
    }
}
