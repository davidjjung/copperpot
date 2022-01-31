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
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 28;
        this.titleLabelY = 16;
    }

    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(ms, mouseX, mouseY);
    }

    protected void renderHoveredToolTip(MatrixStack ms, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 3) {
                List<ITextComponent> tooltip = new ArrayList();
                ItemStack meal = this.hoveredSlot.getItem();
                tooltip.add(((IFormattableTextComponent) meal.getItem().getDescription()).withStyle(meal.getRarity().color));
                ItemStack containerItem = ((CopperPotContainer) this.menu).tileEntity.getContainer();
                String container = !containerItem.isEmpty() ? containerItem.getItem().getDescription().getString() : "";
                tooltip.add(TextUtils.getTranslation("container.copper_pot.served_on", new Object[]{container}).withStyle(TextFormatting.GRAY));
                this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
            } else {
                this.renderTooltip(ms, this.hoveredSlot.getItem(), mouseX, mouseY);
            }
        }

    }

    protected void renderLabels(MatrixStack ms, int mouseX, int mouseY) {
        super.renderLabels(ms, mouseX, mouseY);
        this.font.draw(ms, this.inventory.getDisplayName().getString(), 8.0F, (float) (this.imageHeight - 96 + 2), 4210752);
    }

    public void blat(MatrixStack matrixStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        blat2(matrixStack, x, y, this.getBlitOffset(), (float) uOffset, (float) vOffset, uWidth, vHeight, 256, 256);
    }

    public static void blat2(MatrixStack matrixStack, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureHeight, int textureWidth) {
        innerBlat(matrixStack, x, x + uWidth, y, y + vHeight, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    private static void innerBlat(MatrixStack matrixStack, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight) {
        innerBlat2(matrixStack.last().pose(), x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / (float) textureWidth, (uOffset + (float) uWidth) / (float) textureWidth, (vOffset + 0.0F) / (float) textureHeight, (vOffset + (float) vHeight) / (float) textureHeight);
    }

    private static void innerBlat2(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(matrix, (float)x1, (float)y2, (float)blitOffset).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y2, (float)blitOffset).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y1, (float)blitOffset).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(matrix, (float)x1, (float)y1, (float)blitOffset).uv(minU, minV).endVertex();
        bufferbuilder.end();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.end(bufferbuilder);
    }

    protected void renderBg(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft != null) {
            this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
            int x = (this.width - this.imageWidth) / 2;
            int y = (this.height - this.imageHeight) / 2;
            this.blit(ms, x, y, 0, 0, this.imageWidth, this.imageHeight);
            if (((CopperPotContainer) this.menu).isHeated()) {
                this.blit(ms, x + 48, y + 49, 176, 0, 17, 15);
            }

            int l = ((CopperPotContainer) this.menu).getCookProgressionScaled();
            this.blit(ms, this.leftPos + 89, this.topPos + 25, 176, 15, l + 1, 17);
            if (((CopperPotContainer) this.menu).hasEffect()) {
                this.blat(ms, this.leftPos + 124, this.topPos + 3 + (23 - l), 176, 55 - l, 17, (int) (1 * l));
            }
        }
    }
}
