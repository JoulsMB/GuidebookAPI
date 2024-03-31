package de.wonejo.gapi.api.book;

import de.wonejo.gapi.api.book.components.IBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.List;

public interface IBook {

    void initializeContent ();

    boolean shouldSpawnWithBook ();

    ResourceLocation id ();

    ResourceLocation topTexture ();
    ResourceLocation pagesTexture ();
    ResourceLocation modelLocation ();

    IBookInformation information ();

    Component title ();
    Component header ();
    Component subHeader ();
    Component itemName ();
    Component author ();

    Color color ();

    List<IBookCategory> categories ();

}