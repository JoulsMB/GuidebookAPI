package de.mrbunny.guidebook.example;

import de.mrbunny.guidebook.api.Guidebook;
import de.mrbunny.guidebook.api.IGuidebook;
import de.mrbunny.guidebook.api.book.IBookBuilder;
import de.mrbunny.guidebook.api.book.IBookContentProvider;
import de.mrbunny.guidebook.api.book.component.IBookCategory;
import de.mrbunny.guidebook.api.book.component.IBookEntry;
import de.mrbunny.guidebook.api.book.component.IPage;
import de.mrbunny.guidebook.api.util.References;
import de.mrbunny.guidebook.book.BookBuilder;
import de.mrbunny.guidebook.book.BookContentProvider;
import de.mrbunny.guidebook.book.component.BookCategory;
import de.mrbunny.guidebook.book.component.BookEntry;
import de.mrbunny.guidebook.client.render.category.ItemStackCategoryRender;
import de.mrbunny.guidebook.client.render.entry.ItemStackEntryRender;
import de.mrbunny.guidebook.client.render.entry.TextEntryRender;
import de.mrbunny.guidebook.page.EntityPage;
import de.mrbunny.guidebook.page.ItemStackPage;
import de.mrbunny.guidebook.page.SoundPage;
import de.mrbunny.guidebook.page.TextPage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Guidebook
public class ExampleBook implements IGuidebook {

    public IBookBuilder buildBook() {
        return new BookBuilder(new ResourceLocation(References.GUIDEBOOKAPI_ID, "example_book"))
                .setColor(new Color(250, 50, 70))
                .setTitle(Component.literal("ExampleBook"))
                .setItemName(Component.literal("ExampleBook Item name"))
                .setHeader(Component.literal("ExampleBook Header"))
                .setSubHeaderText(Component.literal("ExampleBook sub header text"))
                .setAuthor(Component.literal("ExampleGuy"))
                .contentProvider(this::buildBookContent);
    }

    private void buildBookContent(List<IBookCategory> pCategories) {
        IBookContentProvider contentProvider = new BookContentProvider(References.GUIDEBOOKAPI_ID);

        Map<ResourceLocation, IBookEntry> entries = new HashMap<>();
        List<IPage> pages = new ArrayList<>();

        pages.add(new TextPage(Component.literal("Some example test text.")));
        pages.add(new ItemStackPage(Component.literal("Extra loooooooooooooooooooooooooooooong and bored text."), new ItemStack(Items.APPLE)));
        pages.add(new EntityPage(Component.literal("Just a zombie"), EntityType.ZOMBIE::create));
        pages.add(new SoundPage(
                new TextPage(Component.literal("Sound page example")),
                SoundEvents.ZOMBIE_AMBIENT
        ));

        entries.put(new ResourceLocation(References.GUIDEBOOKAPI_ID, "entry"), new BookEntry(
                Component.literal("Test entry"),
                new ItemStackEntryRender(new ItemStack(Items.APPLE)),
                pages
        ));
        entries.put(new ResourceLocation(References.GUIDEBOOKAPI_ID, "entry2"),
                new BookEntry(
                        Component.literal("Extra looooooooooooooooooooooooooooong entry"),
                        new TextEntryRender(),
                        pages
                ));

        IBookCategory category = new BookCategory(
                Component.literal("Test category"),
                new ItemStackCategoryRender(new ItemStack(Items.CARROT)),
                entries
        );

        contentProvider.createCategory(category).buildContent(pCategories);
    }

}
