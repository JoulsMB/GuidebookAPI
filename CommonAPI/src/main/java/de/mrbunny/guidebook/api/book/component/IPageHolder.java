package de.mrbunny.guidebook.api.book.component;

import de.mrbunny.guidebook.api.book.IBook;

import java.awt.*;

public interface IPageHolder {

    IBook getBook ();
    IEntry getHolderEntry ();
    Color getDiscriminatorColor ();
    int getPageNumber ();

}
