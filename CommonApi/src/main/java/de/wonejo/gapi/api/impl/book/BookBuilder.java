package de.wonejo.gapi.api.impl.book;

import de.wonejo.gapi.api.book.IBook;
import de.wonejo.gapi.api.book.IBookBuilder;
import de.wonejo.gapi.api.book.IBookInformation;
import de.wonejo.gapi.api.book.IBookInformationBuilder;
import de.wonejo.gapi.api.book.components.IBookCategory;
import de.wonejo.gapi.api.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public final class BookBuilder implements IBookBuilder {

    public static BookBuilder of(ResourceLocation pId) { return new BookBuilder(pId); }

    private final ResourceLocation id;

    private ResourceLocation topTexture = new ResourceLocation(Constants.MOD_ID, "textures/gui/book_top.png");
    private ResourceLocation pagesTexture = new ResourceLocation(Constants.MOD_ID, "textures/gui/book_pages.png");
    private ResourceLocation modelLocation = new ResourceLocation(Constants.MOD_ID, "guidebook");

    private IBookInformation bookInformation;

    private Consumer<List<IBookCategory>> contentProvider;
    private Component title;
    private Component header;
    private Component subHeader;
    private Component itemName;
    private Component author;
    private boolean shouldSpawnWithBook = false;
    private Color color = new Color(255, 128, 26);

    private BookBuilder (ResourceLocation pId) {
        this.id = pId;
    }

    public IBookBuilder information (IBookInformation pInfo) {
        this.bookInformation = pInfo;
        return this;
    }

    public IBookBuilder topTexture(ResourceLocation pId) {
        this.topTexture = pId;
        return this;
    }

    public IBookBuilder pagesTexture(ResourceLocation pPageTextures) {
        this.pagesTexture = pPageTextures;
        return this;
    }

    public IBookBuilder model(ResourceLocation pModelLocation) {
        this.modelLocation = pModelLocation;
        return this;
    }

    public IBookBuilder color(Color pColor) {
        this.color = pColor;
        return this;
    }

    public IBookBuilder spawnWithBook() {
        this.shouldSpawnWithBook = true;
        return this;
    }

    public IBookBuilder title(Component pTitle) {
        this.title = pTitle;
        return this;
    }

    public IBookBuilder header(Component pHeader) {
        this.header = pHeader;
        return this;
    }

    public IBookBuilder subHeader(Component pHeader) {
        this.subHeader = pHeader;
        return this;
    }

    public IBookBuilder itemName(Component pItemName) {
        this.itemName = pItemName;
        return this;
    }

    public IBookBuilder author(Component pAuthor) {
        this.author = pAuthor;
        return this;
    }

    public IBookBuilder contentProvider(Consumer<List<IBookCategory>> pContentProvider) {
        this.contentProvider = pContentProvider;
        return this;
    }

    public IBook build() {
        if ( this.title == null )
            this.title = Component.translatable("text.gapi.unknown");

        if ( this.header == null )
            this.header = title;

        if ( this.subHeader == null )
            this.subHeader = Component.literal("");

        if (this.itemName == null)
            this.itemName = this.title;

        if ( this.author == null )
            this.author = Component.translatable("text.gapi.author.unknown").withColor(ChatFormatting.AQUA.getColor());

        if ( this.contentProvider == null )
            throw new IllegalStateException("Content provider can't be null");

        return new Book(id,
                this.color, this.title, this.header, this.subHeader, this.itemName, this.author,
                this.topTexture, this.pagesTexture, this.modelLocation
                ,this.shouldSpawnWithBook, this.contentProvider, this.bookInformation);
    }
}