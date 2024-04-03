package de.wonejo.gapi.client.render.recipe;

import de.wonejo.gapi.api.client.IModScreen;
import de.wonejo.gapi.api.util.Constants;
import de.wonejo.gapi.api.util.ItemRotation;
import de.wonejo.gapi.api.util.RenderUtils;
import de.wonejo.gapi.config.ModConfigurations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractCraftingRecipeRender<T extends Recipe<?>> extends AbstractRecipeRender<T> {

    private static final ResourceLocation CRAFTING_GRID = new ResourceLocation(Constants.MOD_ID, "textures/gui/recipe/crafting_recipe.png");

    private final Component title;

    public AbstractCraftingRecipeRender(T pRecipe, Component pTitle ) {
        super(pRecipe);
        this.title = pTitle;
    }

    public void render(GuiGraphics pGraphics, RegistryAccess pAccess, int pMouseX, int pMouseY, IModScreen pScreen, Font pFont, ItemRotation pRotation) {
        pRotation.tick(Minecraft.getInstance());

        RenderUtils.renderImage(pGraphics, CRAFTING_GRID, pScreen.xOffset() + pScreen.widthSize() / 2 - 100, pScreen.yOffset() + pScreen.heightSize() / 2 - 58, 100, 58);

        RenderUtils.drawCenteredStringWithoutShadow(pGraphics, pFont, this.title, pScreen.xOffset() + pScreen.yOffset() / 2 - pFont.width(this.title), pScreen.yOffset() + 12, ModConfigurations.CLIENT.textColor.get());

        int outputX = pScreen.xOffset() + pScreen.widthSize() + 54;
        int outputY = pScreen.yOffset() + pScreen.heightSize() / 2 - 11;

        ItemStack stack = this.recipe.getResultItem(pAccess);

        RenderUtils.renderItem(pGraphics, stack, outputX, outputY);

        if ( RenderUtils.isMouseBetween(pMouseX, pMouseY, outputX, outputY, 16, 16) )
            this.tooltips = RenderUtils.getTooltips(stack);
    }
}