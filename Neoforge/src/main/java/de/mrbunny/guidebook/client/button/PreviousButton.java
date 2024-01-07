package de.mrbunny.guidebook.client.button;

import de.mrbunny.guidebook.api.client.IModScreen;
import de.mrbunny.guidebook.util.ScreenUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public final class PreviousButton extends GuideButton {

    public PreviousButton(OnPress pPress, IModScreen pScreen, int pX, int pY ) {
        super(Component.translatable("guidebook.button.previous"), pPress, pScreen, pX, pY, 16, 112);
    }

    protected void renderWidget(@NotNull GuiGraphics pGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if ( this.visible ) {
            if (ScreenUtils.isMouseBetween(pMouseX, pMouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                this.renderImage(pGraphics, this.getX(), this.getY() + 1, 16, 0, 16, 12);
            } else {
                this.renderImage(pGraphics, this.getX(), this.getY(), 0, 0, 16, 12);
            }
        }
    }
}
