package de.mrbunny.guidebook.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class ScreenUtils {

    public static boolean isMouseBetween ( double pMouseX, double pMouseY,int pX, int pY, int pWidth, int pHeight ) {
        int sizeWidth = pX + pWidth;
        int sizeHeight = pY + pHeight;

        return (pMouseX >= pX && pMouseX <= sizeWidth && pMouseY >= pY && pMouseY <= sizeHeight);
    }

    public static void drawItemStack(@NotNull GuiGraphics pGraphics, ItemStack Stack, int pX, int pY) {
        PoseStack mStack = RenderSystem.getModelViewStack();

        mStack.pushPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        pGraphics.renderItem(Stack, pX, pY);
        pGraphics.renderItemDecorations(Minecraft.getInstance().font, Stack, pX, pY, null);
        mStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void drawScaledItemStack(@NotNull GuiGraphics pGraphics, ItemStack pStack, int pX, int pY, float pScale) {
        PoseStack mStack = RenderSystem.getModelViewStack();

        mStack.pushPose();
        mStack.scale(pScale, pScale, 1f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.applyModelViewMatrix();
        pGraphics.renderItem(pStack, (int) (pX / pScale), (int) (pY / pScale));
        mStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void drawImage (@NotNull GuiGraphics pGraphics, ResourceLocation pImage, int pX, int pY, int pWidth, int pHeight) {
        pGraphics.blit(pImage, pX, pY, 0, 0, pWidth, pHeight, pWidth, pHeight);
    }

    public static void drawScaledImage (@NotNull GuiGraphics pGraphics, ResourceLocation pImage, int pX, int pY, int pWidth, int pHeight, float pScale ) {
        PoseStack stack = pGraphics.pose();

        stack.pushPose();
        pGraphics.blit(pImage, pX, pY, 0, 0, pWidth, pHeight, pWidth, pHeight);
        stack.popPose();
    }

    public static <T extends Entity> void renderLivingEntity(@NotNull PoseStack pPoseStack, MultiBufferSource pBufferSource, T pEntity, int pX, int pY ) {
        double entityScale = 100.0F;
        double bbSize = Math.max(pEntity.getBbWidth(), pEntity.getBbHeight());

        if ( bbSize > 1.0 )
            entityScale /= bbSize * 1.5F;

        pPoseStack.pushPose();

        RenderSystem.setShaderLights(
                new Vector3f(1.0F, 1.0F, 1.0F).normalize(),
                new Vector3f(-1.0F, -1.0F, 0.0F).normalize()
        );

        pPoseStack.translate(pX, pY, 50.0F);
        pPoseStack.scale((float) entityScale, (float) entityScale, (float) entityScale);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(((float) Util.getMillis() / 20) % 360));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        Minecraft.getInstance().getEntityRenderDispatcher().render(pEntity, 0, 0, 0, 0.0F,
                Minecraft.getInstance().getFrameTime(), pPoseStack, pBufferSource, 15728880);
        pPoseStack.popPose();
    }

}