package com.fantasticsource.faerunutils.bettercrafting.table;

import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.Network;
import com.fantasticsource.faerunutils.bettercrafting.recipe.BetterRecipe;
import com.fantasticsource.tools.Collision;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

import static com.fantasticsource.faerunutils.FaerunUtils.faerun;

@SideOnly(Side.CLIENT)
public class GUIBetterCrafting extends GuiContainer
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaerunUtils.MODID, "textures/gui/bettercraftingtable.png");
    private static final ResourceLocation TEXTURE_FAERUN = new ResourceLocation(FaerunUtils.MODID, "textures/gui/bettercraftingtablefaerun.png");

    private static final double ARROW_V1 = 240d / 256, ARROW_V2 = 250d / 256;
    private static final double UP_INACTIVE_U1 = 224d / 256, UP_ACTIVE_U1 = 232d / 256, DOWN_INACTIVE_U1 = 240d / 256, DOWN_ACTIVE_U1 = 248d / 256;

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

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        //Recipe name
        BetterRecipe recipe = ((ContainerBetterCraftingTable) inventorySlots).getRecipe();
        if (recipe != null)
        {
            fontRenderer.drawString(I18n.format(recipe.translationKey()), 88, 16, recipe.color().toARGB());
        }
        else fontRenderer.drawString(I18n.format(FaerunUtils.MODID + ":recipe.null"), 88, 16, 0xFFFF0000);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(faerun ? TEXTURE_FAERUN : TEXTURE);

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


        //Up arrow
        int arrowsX1 = x1 + 152;
        int arrowsX2 = arrowsX1 + 8;
        int upY1 = y1 + 31;
        int upY2 = upY1 + 10;

        double u1 = Collision.pointRectangle(mouseX, mouseY, arrowsX1, upY1, arrowsX2, upY2) ? UP_ACTIVE_U1 : UP_INACTIVE_U1;
        double u2 = u1 + 8d / 256;

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(arrowsX1, upY2, zLevel).tex(u1, ARROW_V2).endVertex();
        bufferbuilder.pos(arrowsX2, upY2, zLevel).tex(u2, ARROW_V2).endVertex();
        bufferbuilder.pos(arrowsX2, upY1, zLevel).tex(u2, ARROW_V1).endVertex();
        bufferbuilder.pos(arrowsX1, upY1, zLevel).tex(u1, ARROW_V1).endVertex();
        tessellator.draw();


        //Down arrow
        int downY1 = upY2 + 3;
        int downY2 = downY1 + 10;

        u1 = Collision.pointRectangle(mouseX, mouseY, arrowsX1, downY1, arrowsX2, downY2) ? DOWN_ACTIVE_U1 : DOWN_INACTIVE_U1;
        u2 = u1 + 8d / 256;

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(arrowsX1, downY2, zLevel).tex(u1, ARROW_V2).endVertex();
        bufferbuilder.pos(arrowsX2, downY2, zLevel).tex(u2, ARROW_V2).endVertex();
        bufferbuilder.pos(arrowsX2, downY1, zLevel).tex(u2, ARROW_V1).endVertex();
        bufferbuilder.pos(arrowsX1, downY1, zLevel).tex(u1, ARROW_V1).endVertex();
        tessellator.draw();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int x1 = (width >> 1) - (xSize >> 1);
        int y1 = (height >> 1) - (ySize >> 1);

        int arrowsX1 = x1 + 152;
        int arrowsX2 = arrowsX1 + 8;
        int upY1 = y1 + 31;
        int upY2 = upY1 + 10;

        if (Collision.pointRectangle(mouseX, mouseY, arrowsX1, upY1, arrowsX2, upY2)) Network.WRAPPER.sendToServer(new Network.ChangeRecipePacket(-1));
        else
        {
            int downY1 = upY2 + 3;
            int downY2 = downY1 + 10;

            if (Collision.pointRectangle(mouseX, mouseY, arrowsX1, downY1, arrowsX2, downY2)) Network.WRAPPER.sendToServer(new Network.ChangeRecipePacket(1));
        }
    }
}
