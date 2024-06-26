package de.wonejo.gapi.client.screen;

import de.wonejo.gapi.CommonGapiMod;
import de.wonejo.gapi.api.book.IBook;
import de.wonejo.gapi.api.book.components.ICategory;
import de.wonejo.gapi.api.book.components.IEntry;
import de.wonejo.gapi.api.book.components.IPage;
import de.wonejo.gapi.api.util.GuideScreenType;
import de.wonejo.gapi.api.util.RenderUtils;
import de.wonejo.gapi.cfg.ModConfigurations;
import de.wonejo.gapi.client.button.GuideButton;
import de.wonejo.gapi.ext.IScreenRenderablesAccessor;
import de.wonejo.gapi.impl.service.Services;
import de.wonejo.gapi.network.ReadingStatePayload;
import de.wonejo.gapi.wrapper.PageWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class EntryGuideScreen extends GuideScreen {

    private final List<PageWrapper> pages = Lists.newArrayList();
    private final ICategory category;
    private IEntry entry;
    public int pageId;

    public EntryGuideScreen(Player pPlayer, IBook pBook, ICategory pCategory, IEntry pEntry) {
        super(pPlayer, pBook, GuideScreenType.PAGE);
        this.category = pCategory;
        this.entry = pEntry;
        this.pageId = 0;
    }

    protected void init() {
        this.entry.getRender().init();
        this.pages.clear();

        this.addRenderableWidget(new GuideButton(
                this.xOffset() + 8, this.yOffset() + this.screenHeight() - 18,
                GuideButton.ButtonType.BACK, (button) -> {
            if ( this.pageId > 0 )
                this.previousPage();
        }));

        this.addRenderableWidget(new GuideButton(
                this.xOffset() + 24, this.yOffset() + this.screenHeight() - 20,
                GuideButton.ButtonType.CATEGORY_HOME, (button) -> {
            Minecraft.getInstance().setScreen(new CategoryGuideScreen(this.getPlayer(), this.getBook(), this.category));
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
            if ( this.pageId + 1 < this.pages.size() )
                this.nextPage();
        }));

        for ( IPage page : this.entry.getPages() ) {
            page.getRender().init();

            this.pages.add(new PageWrapper(this.getBook(), page));
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (!CommonGapiMod.isRunningOnForge()) {
            ResourceLocation key = null;
            for (Map.Entry<ResourceLocation, IEntry> entryEntry : category.entries().entrySet())
                if ( entryEntry.getValue().equals(this.entry) )
                    key = entryEntry.getKey();

            if ( key != null )
                Services.NETWORK.sendToServer(this.getPlayer(), new ReadingStatePayload(this.pageId, Optional.of(getBook().categories().indexOf(this.category)), Optional.of(key)));
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if ( !super.mouseClicked(pMouseX, pMouseY, pButton) ) {
            for (PageWrapper wrapper : this.pages) {
                if ( wrapper.isMouseOnWrapper(pMouseX, pMouseY)){
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
        if ( this.pageId < this.pages.size() )
            if ( this.pages.get(this.pageId).canView() )
                this.pages.get(this.pageId).tick();
    }

    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
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

        RenderUtils.drawCenteredStringWithoutShadow(pGuiGraphics, Minecraft.getInstance().font, this.entry.getRender().name(), this.xOffset() + this.screenWidth() / 2, this.yOffset() - 12, ChatFormatting.WHITE.getColor());

        this.pageId = Mth.clamp(this.pageId, 0, this.pages.size() - 1);

        if ( this.pageId < this.pages.size() )
            if ( this.pages.get(this.pageId).canView() )
                this.pages.get(this.pageId).render(pGuiGraphics, Minecraft.getInstance().level.registryAccess(), pMouseX, pMouseY, this);

        ((IScreenRenderablesAccessor) (Screen) this).getAllRenderables().forEach(it -> it.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick));
    }

    private void nextPage ( ) {
        if ( this.pageId != this.pages.size() - 1 && !this.pages.isEmpty() )
            this.pageId++;
    }

    private void previousPage () {
        if ( this.pageId != 0 )
            this.pageId--;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }
}
