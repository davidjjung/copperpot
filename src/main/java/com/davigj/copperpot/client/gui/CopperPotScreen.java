package com.davigj.copperpot.client.gui;

import com.davigj.copperpot.common.tile.container.CopperPotContainer;
import com.davigj.copperpot.core.utils.TextUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class CopperPotScreen extends ContainerScreen<CopperPotContainer> {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("copperpot", "textures/gui/copper_pot.png");

    public CopperPotScreen(CopperPotContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.guiLeft = 0;
        this.guiTop = 0;
        this.xSize = 176;
        this.ySize = 166;
        this.titleX = 28;
        this.titleY = 16;
    }

    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(ms, mouseX, mouseY);
    }

    protected void renderHoveredToolTip(MatrixStack ms, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            if (this.hoveredSlot.slotNumber == 3) {
                List<ITextComponent> tooltip = new ArrayList();
                ItemStack meal = this.hoveredSlot.getStack();
                tooltip.add(((IFormattableTextComponent) meal.getItem().getName()).mergeStyle(meal.getRarity().color));
                ItemStack containerItem = ((CopperPotContainer) this.container).tileEntity.getContainer();
                String container = !containerItem.isEmpty() ? containerItem.getItem().getName().getString() : "";
                tooltip.add(TextUtils.getTranslation("container.copper_pot.served_on", new Object[]{container}).mergeStyle(TextFormatting.GRAY));
                this.func_243308_b(ms, tooltip, mouseX, mouseY);
            } else {
                this.renderTooltip(ms, this.hoveredSlot.getStack(), mouseX, mouseY);
            }
        }

    }

    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(ms, mouseX, mouseY);
        this.font.drawString(ms, this.playerInventory.getDisplayName().getString(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
    }

    public void blat(MatrixStack matrixStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        blat2(matrixStack, x, y, this.getBlitOffset(), (float) uOffset, (float) vOffset, uWidth, vHeight, 256, 256);
    }

    public static void blat2(MatrixStack matrixStack, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureHeight, int textureWidth) {
        innerBlat(matrixStack, x, x + uWidth, y, y + vHeight, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    private static void innerBlat(MatrixStack matrixStack, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight) {
        innerBlat2(matrixStack.getLast().getMatrix(), x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / (float) textureWidth, (uOffset + (float) uWidth) / (float) textureWidth, (vOffset + 0.0F) / (float) textureHeight, (vOffset + (float) vHeight) / (float) textureHeight);
    }

    private static void innerBlat2(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix, (float)x1, (float)y2, (float)blitOffset).tex(minU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float)x2, (float)y2, (float)blitOffset).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float)x2, (float)y1, (float)blitOffset).tex(maxU, minV).endVertex();
        bufferbuilder.pos(matrix, (float)x1, (float)y1, (float)blitOffset).tex(minU, minV).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft != null) {
            this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
            int x = (this.width - this.xSize) / 2;
            int y = (this.height - this.ySize) / 2;
            this.blit(ms, x, y, 0, 0, this.xSize, this.ySize);
            if (((CopperPotContainer) this.container).isHeated()) {
                this.blit(ms, x + 48, y + 49, 176, 0, 17, 15);
            }

            int l = ((CopperPotContainer) this.container).getCookProgressionScaled();
            this.blit(ms, this.guiLeft + 89, this.guiTop + 25, 176, 15, l + 1, 17);
            if (((CopperPotContainer) this.container).hasEffect()) {
                this.blat(ms, this.guiLeft + 124, this.guiTop + 3 + (23 - l), 176, 55 - l, 17, (int) (1 * l));
            }
        }
    }
}
