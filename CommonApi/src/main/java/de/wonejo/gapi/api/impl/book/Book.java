package de.wonejo.gapi.api.impl.book;

import de.wonejo.gapi.api.book.IBook;
import de.wonejo.gapi.api.book.IBookInformation;
import de.wonejo.gapi.api.book.components.IBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public final class Book implements IBook {

    private final ResourceLocation id;

    private final ResourceLocation topTexture;
    private final ResourceLocation pagesTexture;
    private final ResourceLocation modelLocation;

    private boolean init;

    private final IBookInformation bookInformation;

    private final Component title;
    private final Component header;
    private final Component subHeader;
    private final Component itemName;
    private final Component author;

    private final boolean shouldSpawnWithBook;
    private final Color color;

    private final Consumer<List<IBookCategory>> contentProvider;
    private final List<IBookCategory> categories = Lists.newArrayList();

    public Book ( ResourceLocation pId, Color pColor,
                  Component pTitle, Component pHeader, Component pSubHeader, Component pItemName, Component pAuthor,
                  ResourceLocation pTopTexture, ResourceLocation pPagesTexture, ResourceLocation pModelLocation,
                  boolean pShouldSpawnWithBook, Consumer<List<IBookCategory>> pContentProvider, IBookInformation pInformation ) {

        this.topTexture = pTopTexture;
        this.pagesTexture = pPagesTexture;
        this.modelLocation = pModelLocation;

        this.id = pId;
        this.color = pColor;
        this.title = pTitle;
        this.header = pHeader;
        this.subHeader = pSubHeader;
        this.itemName = pItemName;
        this.author = pAuthor;
        this.shouldSpawnWithBook = pShouldSpawnWithBook;
        this.contentProvider = pContentProvider;
        this.bookInformation = pInformation;
    }

    public void initializeContent() {
        if (!init) {
            this.contentProvider.accept(this.categories);
            this.init = true;
        }
    }

    public ResourceLocation id() {
        return this.id;
    }

    public ResourceLocation topTexture() {
        return this.topTexture;
    }

    public ResourceLocation pagesTexture() {
        return this.pagesTexture;
    }

    public ResourceLocation modelLocation() {
        return this.modelLocation;
    }

    public Component title() {
        return this.title;
    }

    public Component header() {
        return this.header;
    }

    public Component subHeader() {
        return this.subHeader;
    }

    public Component itemName() {
        return this.itemName;
    }

    public Component author() {
        return this.author;
    }

    public Color color() {
        return this.color;
    }

    public boolean shouldSpawnWithBook() {
        return this.shouldSpawnWithBook;
    }

    public IBookInformation information() {
        return this.bookInformation;
    }

    public List<IBookCategory> categories() {
        return this.categories;
    }
}