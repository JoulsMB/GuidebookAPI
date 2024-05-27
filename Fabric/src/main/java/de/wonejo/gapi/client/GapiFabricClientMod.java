package de.wonejo.gapi.client;

import de.wonejo.gapi.api.book.item.IBookItem;
import de.wonejo.gapi.cfg.ModConfigurations;
import de.wonejo.gapi.handler.GapiClientHandler;
import de.wonejo.gapi.impl.service.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class GapiFabricClientMod implements ClientModInitializer {

    public void onInitializeClient() {
        ModelLoadingPlugin.register(new GapiClientHandler());

        ClientLifecycleEvents.CLIENT_STARTED.register(this::finalizeLoad);
    }

    private void finalizeLoad (Minecraft pMc) {

        for (Supplier<ItemStack> bookStack : Services.BOOK_REGISTRY.getBookToStacks().values() ) {

            ColorProviderRegistry.ITEM.register((stack, tint) -> {
                IBookItem item = (IBookItem) stack.getItem();

                if ( item.get() != null && !item.get().useCustomBookModel() && tint == 0 )
                    return ModConfigurations.CLIENT_PROVIDER.getBookColors().get(item.get()).get().getRGB();

                return -1;
            }, bookStack.get().getItem());

        }
    }
}