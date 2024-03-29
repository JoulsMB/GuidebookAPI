package de.mrbunny.guidebook.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IngredientCycler {

    private final RandomSource randomSource = RandomSource.create();
    private long lastCycle = -1;
    private int cycleIdx = 0;

    public void tick ( Minecraft pMinecraft ) {
        long time = pMinecraft.level != null ? pMinecraft.level.getGameTime() : 0;
        if ( this.lastCycle < 0 || this.lastCycle < time - 20 ) {
            if ( this.lastCycle > 0 ) {
                this.cycleIdx++;
                this.cycleIdx = Math.max(0, cycleIdx);
            }
            this.lastCycle = time;
        }
    }

    /**
     * Retrieves a ItemStack that matches the ingredient
     * Cycles thought all matching stacks.
     * Must call {@link IngredientCycler#tick(Minecraft)} before (e.g. once per render method)
     *
     * @param  pIngredient The Ingredients
     * @param  pIndex An "unique" id for this ingredient, so multiplied ingredients can be cycle independently
     * @return Optional. Can be empty if ingredients is valid and has no matching stacks
     */
    public Optional<ItemStack> cycleItems (@NotNull Ingredient pIngredient, int pIndex) {
        ItemStack[] itemStacks = pIngredient.getItems();

        if ( itemStacks.length != 0 ) {
            this.randomSource.setSeed(pIndex);
            int id = (pIndex + randomSource.nextInt(itemStacks.length) + this.cycleIdx) % itemStacks.length;
            return Optional.of(itemStacks[id]);
        }

        return Optional.empty();
    }
}
