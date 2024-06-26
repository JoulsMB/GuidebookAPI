package de.wonejo.gapi.client.screen;

import com.google.common.collect.HashMultimap;
import de.wonejo.gapi.CommonGapiMod;
import de.wonejo.gapi.api.book.IBook;
import de.wonejo.gapi.api.book.components.ICategory;
import de.wonejo.gapi.api.util.GuideScreenType;
import de.wonejo.gapi.api.util.RenderUtils;
import de.wonejo.gapi.cfg.ModConfigurations;
import de.wonejo.gapi.client.button.GuideButton;
import de.wonejo.gapi.ext.IScreenRenderablesAccessor;
import de.wonejo.gapi.impl.service.Services;
import de.wonejo.gapi.network.ReadingStatePayload;
import de.wonejo.gapi.wrapper.CategoryWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class HomeGuideScreen extends GuideScreen {

    private final HashMultimap<Integer, CategoryWrapper> categories = HashMultimap.create();
    public int categoryPage;

    public HomeGuideScreen( Player pPlayer, IBook pBook ) {
        super(pPlayer, pBook, GuideScreenType.NORMAL);
        this.categoryPage = 0;
    }

    protected void init() {
        this.categories.clear();

        this.addRenderableWidget(new GuideButton(
                this.xOffset() + 8, this.yOffset() + this.screenHeight() - 18,
                GuideButton.ButtonType.BACK, (button) -> {
                    if ( this.categoryPage > 0 )
                        this.previousPage();
        }));

        this.addRenderableWidget(new GuideButton(
                this.xOffset() + 24, this.yOffset() + this.screenHeight() - 20,
                GuideButton.ButtonType.HOME, (button) -> {
                    Minecraft.getInstance().setScreen(new HomeGuideScreen(this.getPlayer(), this.getBook()));
        }));

        this.addRenderableWidget(new GuideButton(
                this.xOffset() + this.screenWidth() - 36, this.yOffset() + this.screenHeight() - 20,
                GuideButton.ButtonType.INFORMATION, (button) -> {
                Minecraft.getInstance().setScreen(new InformationGuideScreen(this.getPlayer(), this.getBook()));
            }
        ));

        this.addRenderableWidget(new GuideButton(
                this.xOffset() + this.screenWidth() - 22, this.yOffset() + this.screenHeight() - 18,
                GuideButton.ButtonType.NEXT, (button) -> {
                if ( this.categoryPage + 1 < this.categories.asMap().size() )
                    this.nextPage();
            }
        ));

        int categoryX = this.xOffset() + 10;
        int categoryY = this.yOffset() + 10;
        int categoryIndex = 0;
        int pageNumber = 0;

        for ( ICategory category : this.getBook().categories() )  {
            int x = categoryIndex % 5;
            int y = categoryIndex / 5;

            if (category.entries().isEmpty()) continue;

            if (categoryIndex >= 40) {
                categoryX = this.xOffset() + 10;
                categoryY = this.yOffset() + 10;

                pageNumber++;
                categoryIndex = 0;
            } else if (categoryIndex >= 20) {
                categoryX = this.xOffset() + this.screenWidth() / 2 + 8 + categoryIndex % 5;
                categoryY = this.yOffset() + 10;
            }

            this.categories.put(pageNumber, new CategoryWrapper(category, categoryX + x * 24, categoryY + (y % 4) * 24));
            categoryIndex++;
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!super.mouseClicked(pMouseX, pMouseY, pButton)) {
            for (CategoryWrapper wrapper : this.categories.get(this.categoryPage)) {
                if ( wrapper.isMouseOnWrapper(pMouseX, pMouseY) ) {
                    wrapper.get().getRender().onClick(this.getBook(), this.getPlayer(), pMouseX, pMouseY, pButton);
                    wrapper.get().onClick(this.getBook(), this.getPlayer(), pMouseX, pMouseY, pButton);

                    return true;
                }
            }

            return false;
        }

        return true;
    }

    public void tick() {
        this.categories.forEach(((integer, categoryWrapper) -> categoryWrapper.tick()));
    }

    @Override
    public void onClose() {
        super.onClose();
        if (!CommonGapiMod.isRunningOnForge())
            Services.NETWORK.sendToServer(this.getPlayer(), new ReadingStatePayload(this.categoryPage, Optional.empty(), Optional.empty()));
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (!this.getBook().useCustomPagesTexture()) {
            float red = ModConfigurations.CLIENT_PROVIDER.getPageBookColors().get(this.getBook()).get().getRed() / 255.0F;
            float green = ModConfigurations.CLIENT_PROVIDER.getPageBookColors().get(this.getBook()).get().getGreen() / 255.0F;
            float blue = ModConfigurations.CLIENT_PROVIDER.getPageBookColors().get(this.getBook()).get().getBlue() / 255.0F;

            pGuiGraphics.setColor(red, green, blue, 1.0F);
            RenderUtils.renderImage(pGuiGraphics, this.topTexture(), this.xOffset(), this.yOffset(), this.screenWidth(), this.screenHeight());
        }

        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.renderImage(pGuiGraphics, this.pagesTexture(), this.xOffset(), this.yOffset(), this.screenWidth(), this.screenHeight());

        this.renderHeader(pGuiGraphics);

        this.categoryPage = Mth.clamp(this.categoryPage, 0, this.categories.size() - 1);

        for ( CategoryWrapper wrapper : this.categories.get(this.categoryPage) )
            if ( wrapper.canView() )
                wrapper.render(pGuiGraphics, Minecraft.getInstance().level.registryAccess(), pMouseX, pMouseY, this);

        ((IScreenRenderablesAccessor) (Screen) this).getAllRenderables().forEach(it -> it.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick));
    }

    private void renderHeader (GuiGraphics pGuiGraphics) {
        if (this.getBook().subHeader().equals(Component.empty())) {
            RenderUtils.drawCenteredStringWithoutShadow(pGuiGraphics, this.font, this.getBook().header(),
                    this.xOffset() + this.screenWidth() / 2,
                    this.yOffset()  - 12,
                    ChatFormatting.WHITE.getColor());
        } else {
            RenderUtils.drawCenteredStringWithoutShadow(pGuiGraphics, this.font, this.getBook().header(),
                    this.xOffset() + this.screenWidth() / 2,
                    this.yOffset()  - 24,
                    ChatFormatting.WHITE.getColor());

            RenderUtils.drawCenteredStringWithoutShadow(pGuiGraphics, this.font, this.getBook().subHeader(),
                    this.xOffset() + this.screenWidth() / 2,
                    this.yOffset() - 12,
                    ChatFormatting.WHITE.getColor());
        }
    }

    private void nextPage () {
        if ( this.categoryPage != this.categories.asMap().size() - 1 && !this.categories.asMap().isEmpty() )
            this.categoryPage++;
    }

    private void previousPage () {
        if ( this.categoryPage != 0 )
            this.categoryPage--;
    }

}
