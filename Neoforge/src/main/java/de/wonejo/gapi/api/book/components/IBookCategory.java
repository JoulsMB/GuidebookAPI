package de.wonejo.gapi.api.book.components;

import de.wonejo.gapi.api.client.render.ICategoryRender;
import de.wonejo.gapi.api.util.BookAccessible;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface IBookCategory extends BookAccessible {

    Component name ();
    ResourceLocation categoryId ();
    ICategoryRender render ();
    Map<ResourceLocation, IBookEntry> entries ();


    void addEntry ( ResourceLocation pId, IBookEntry pEntry );
    void addEntries (Map<ResourceLocation, IBookEntry> pEntries);

    IBookEntry getEntry ( ResourceLocation pId );

}